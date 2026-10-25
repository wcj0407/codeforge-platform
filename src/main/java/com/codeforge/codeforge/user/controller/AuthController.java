package com.codeforge.codeforge.user.controller;

import com.codeforge.codeforge.common.Result;
import com.codeforge.codeforge.user.dto.LoginRequest;
import com.codeforge.codeforge.user.dto.LoginResponse;
import com.codeforge.codeforge.user.dto.RegisterRequest;
import com.codeforge.codeforge.user.dto.ResetPasswordRequest;
import com.codeforge.codeforge.user.dto.SendResetCodeRequest;
import com.codeforge.codeforge.user.dto.UserVO;
import com.codeforge.codeforge.user.service.PasswordResetService;
import com.codeforge.codeforge.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return Result.success(userService.register(registerRequest));
    }
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return Result.success(userService.login(loginRequest));
    }

    /** 发送密码重置验证码到绑定邮箱 */
    @PostMapping("/password/reset-code")
    public Result<Void> sendResetCode(@Valid @RequestBody SendResetCodeRequest request) {
        passwordResetService.sendResetCode(request.getUsername());
        return Result.success();
    }

    /** 校验验证码并重置密码 */
    @PostMapping("/password/reset")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getUsername(), request.getCode(), request.getNewPassword());
        return Result.success();
    }
}
