package com.codeforge.codeforge.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codeforge.codeforge.user.dto.AdminUserVO;
import com.codeforge.codeforge.user.dto.LoginRequest;
import com.codeforge.codeforge.user.dto.LoginResponse;
import com.codeforge.codeforge.user.dto.RegisterRequest;
import com.codeforge.codeforge.user.dto.UserVO;
import com.codeforge.codeforge.user.dto.UserStatsVO;
import com.codeforge.codeforge.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends IService<User> {
    UserVO register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    UserVO getUserProfile(Long userId);
    UserStatsVO getUserStats(Long userId);
    void updatePassword(Long userId, String oldPassword, String newPassword);
    void updateEmail(Long userId, String email);
    LoginResponse updateUsername(Long userId, String username);
    String updateAvatar(Long userId, MultipartFile file);
    Page<AdminUserVO> listUsers(Integer page, Integer size);
    void updateUserStatus(Long id, Integer status);
    void updateUserRole(Long id, String role);
    /** 管理员重置他人密码，返回生成的临时密码 */
    String resetPasswordByAdmin(Long id);
}
