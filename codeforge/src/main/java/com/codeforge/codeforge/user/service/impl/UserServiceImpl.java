package com.codeforge.codeforge.user.service.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.common.utils.JwtUtil;
import com.codeforge.codeforge.user.dto.AdminUserVO;
import com.codeforge.codeforge.user.dto.LoginRequest;
import com.codeforge.codeforge.user.dto.LoginResponse;
import com.codeforge.codeforge.user.dto.RegisterRequest;
import com.codeforge.codeforge.user.dto.UserVO;
import com.codeforge.codeforge.user.dto.UserStatsVO;
import com.codeforge.codeforge.user.entity.User;
import com.codeforge.codeforge.user.mapper.UserMapper;
import com.codeforge.codeforge.user.service.UserService;
import com.codeforge.codeforge.submission.entity.Submission;
import com.codeforge.codeforge.submission.mapper.SubmissionMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MinioClient minioClient;
    private final SubmissionMapper submissionMapper;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Override
    public UserVO register(RegisterRequest registerRequest) {

        //查是否存在
        Long count = lambdaQuery().eq(User::getUsername, registerRequest.getUsername()).count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        // 邮箱唯一：找回密码依赖邮箱，必须保证一个邮箱只对应一个账号
        String email = registerRequest.getEmail().trim();
        Long emailCount = lambdaQuery().eq(User::getEmail, email).count();
        if (emailCount > 0) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }
        //不存在的话，就存入数据库
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(email);
        user.setRole("USER");
        user.setStatus(1);
        save(user);

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setRole(user.getRole());
        return userVO;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = lambdaQuery().eq(User::getUsername, loginRequest.getUsername()).one();
         //判断是否在数据库中存在用户
        if (user==null) {
            throw  new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        //查看是否被禁用了
        if (user.getStatus() != null && user.getStatus() == 0 ) {
            throw new BusinessException (ErrorCode.ACCOUNT_DISABLED);
        }
        //通过比对来判断密码正不正确
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        //生成token
        String token = jwtUtil.generateToken(user);
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setRole(user.getRole());

        return new LoginResponse(token, userVO);
    }

    @Override
    public UserVO getUserProfile(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    @Override
    public UserStatsVO getUserStats(Long userId) {
        UserStatsVO vo = new UserStatsVO();

        // 提交总数
        vo.setTotalSubmissions(submissionMapper.selectCount(
                new LambdaQueryWrapper<Submission>().eq(Submission::getUserId, userId)));

        // 通过的提交数
        vo.setAcceptedSubmissions(submissionMapper.selectCount(
                new LambdaQueryWrapper<Submission>()
                        .eq(Submission::getUserId, userId)
                        .eq(Submission::getStatus, "ACCEPTED")));

        // 通过题数 / 尝试题数：按 problem_id 去重
        vo.setSolvedProblemCount(countDistinctProblems(userId, true));
        vo.setAttemptedProblemCount(countDistinctProblems(userId, false));

        // 通过率 = 通过的提交数 / 提交总数
        long total = vo.getTotalSubmissions();
        vo.setPassRate(total == 0 ? 0 : (int) Math.round(vo.getAcceptedSubmissions() * 100.0 / total));
        return vo;
    }

    /** 统计该用户涉及的不同题目数；onlyAccepted=true 时只统计 ACCEPTED 的题 */
    private long countDistinctProblems(Long userId, boolean onlyAccepted) {
        QueryWrapper<Submission> wrapper = new QueryWrapper<Submission>()
                .select("DISTINCT problem_id")
                .eq("user_id", userId);
        if (onlyAccepted) {
            wrapper.eq("status", "ACCEPTED");
        }
        return submissionMapper.selectObjs(wrapper).size();
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    @Override
    public void updateEmail(Long userId, String email) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        user.setEmail(email);
        updateById(user);
    }

    @Override
    public LoginResponse updateUsername(Long userId, String username) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        Long count = lambdaQuery()
                .eq(User::getUsername, username)
                .ne(User::getId, userId)
                .count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        user.setUsername(username);
        updateById(user);

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token, getUserProfile(userId));
    }

    @Override
    public String updateAvatar(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        String original = file.getOriginalFilename();
        String ext = ".png";
        if (original != null && original.lastIndexOf('.') >= 0) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String objectKey = "avatar/" + UUID.randomUUID().toString().replace("-", "") + ext;

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            log.error("头像上传到 MinIO 失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        String url = endpoint + "/" + bucket + "/" + objectKey;
        user.setAvatar(url);
        updateById(user);
        return url;
    }

    @Override
    public Page<AdminUserVO> listUsers(Integer page, Integer size) {
        Page<User> result = page(new Page<>(page, size),
                new LambdaQueryWrapper<User>().orderByDesc(User::getId));
        Page<AdminUserVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<AdminUserVO> list = new ArrayList<>();
        for (User u : result.getRecords()) {
            AdminUserVO vo = new AdminUserVO();
            vo.setId(u.getId());
            vo.setUsername(u.getUsername());
            vo.setEmail(u.getEmail());
            vo.setRole(u.getRole());
            vo.setStatus(u.getStatus());
            vo.setCreateTime(u.getCreateTime());
            list.add(vo);
        }
        voPage.setRecords(list);
        return voPage;
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        // 防止管理员禁用自己导致把自己锁在外面
        if (id.equals(UserContext.get().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        user.setStatus(status);
        updateById(user);
    }

    @Override
    public void updateUserRole(Long id, String role) {
        // 防止管理员改自己的角色导致丢失管理员权限
        if (id.equals(UserContext.get().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // 只允许三种合法角色
        if (!"USER".equals(role) && !"AUTHOR".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        user.setRole(role);
        updateById(user);
    }

    @Override
    public String resetPasswordByAdmin(Long id) {
        // 不允许管理员重置自己的密码（避免绕过旧密码校验）
        if (id.equals(UserContext.get().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        String tempPassword = generateTempPassword(8);
        User update = new User();
        update.setId(id);
        update.setPassword(passwordEncoder.encode(tempPassword));
        updateById(update);
        return tempPassword;
    }

    private String generateTempPassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
