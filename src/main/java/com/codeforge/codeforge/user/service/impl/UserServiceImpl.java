package com.codeforge.codeforge.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.common.utils.JwtUtil;
import com.codeforge.codeforge.user.dto.LoginRequest;
import com.codeforge.codeforge.user.dto.LoginResponse;
import com.codeforge.codeforge.user.dto.RegisterRequest;
import com.codeforge.codeforge.user.dto.UserVO;
import com.codeforge.codeforge.user.entity.User;
import com.codeforge.codeforge.user.mapper.UserMapper;
import com.codeforge.codeforge.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Override
    public UserVO register(RegisterRequest registerRequest) {

        //查是否存在
        Long count = lambdaQuery().eq(User::getUsername, registerRequest.getUsername()).count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        //不存在的话，就存入数据库
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER");
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
}
