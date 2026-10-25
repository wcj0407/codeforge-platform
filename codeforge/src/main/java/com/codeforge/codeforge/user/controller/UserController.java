package com.codeforge.codeforge.user.controller;

import com.codeforge.codeforge.common.Result;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.user.dto.LoginResponse;
import com.codeforge.codeforge.user.dto.UpdateEmailRequest;
import com.codeforge.codeforge.user.dto.UpdatePasswordRequest;
import com.codeforge.codeforge.user.dto.UpdateUsernameRequest;
import com.codeforge.codeforge.user.dto.UserVO;
import com.codeforge.codeforge.user.dto.UserStatsVO;
import com.codeforge.codeforge.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<UserVO> me() {

        return Result.success(userService.getUserProfile(UserContext.get().getId()));
    }

    @GetMapping("/stats")
    public Result<UserStatsVO> stats() {
        return Result.success(userService.getUserStats(UserContext.get().getId()));
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(UserContext.get().getId(), request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    @PutMapping("/email")
    public Result<Void> updateEmail(@Valid @RequestBody UpdateEmailRequest request) {
        userService.updateEmail(UserContext.get().getId(), request.getEmail());
        return Result.success();
    }

    @PutMapping("/username")
    public Result<LoginResponse> updateUsername(@Valid @RequestBody UpdateUsernameRequest request) {
        return Result.success(userService.updateUsername(UserContext.get().getId(), request.getUsername()));
    }

    @PostMapping("/avatar")
    public Result<String> updateAvatar(@RequestParam("file") MultipartFile file) {
        return Result.success(userService.updateAvatar(UserContext.get().getId(), file));
    }
}