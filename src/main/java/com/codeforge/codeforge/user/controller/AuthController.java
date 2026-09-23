package com.codeforge.codeforge.user.controller;

import com.codeforge.codeforge.common.Result;
import com.codeforge.codeforge.user.dto.RegisterRequest;
import com.codeforge.codeforge.user.dto.UserVO;
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
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return Result.success(userService.register(registerRequest));
    }
}
