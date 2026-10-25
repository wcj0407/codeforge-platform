package com.codeforge.codeforge.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeforge.codeforge.common.Result;
import com.codeforge.codeforge.common.annotation.RequireRole;
import com.codeforge.codeforge.user.dto.AdminUserVO;
import com.codeforge.codeforge.user.dto.UpdateUserRoleRequest;
import com.codeforge.codeforge.user.dto.UpdateUserStatusRequest;
import com.codeforge.codeforge.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    /** 用户列表（分页） */
    @RequireRole("ADMIN")
    @GetMapping("/users")
    public Result<Page<AdminUserVO>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(userService.listUsers(page, size));
    }

    /** 禁用 / 启用账号：status 1=正常，0=禁用 */
    @RequireRole("ADMIN")
    @PutMapping("/users/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @Valid @RequestBody UpdateUserStatusRequest request) {
        userService.updateUserStatus(id, request.getStatus());
        return Result.success();
    }

    /** 修改用户角色：USER / AUTHOR / ADMIN */
    @RequireRole("ADMIN")
    @PutMapping("/users/{id}/role")
    public Result<Void> updateRole(@PathVariable Long id,
                                   @Valid @RequestBody UpdateUserRoleRequest request) {
        userService.updateUserRole(id, request.getRole());
        return Result.success();
    }

    /** 管理员重置用户密码，返回临时密码（需转告用户） */
    @RequireRole("ADMIN")
    @PutMapping("/users/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Long id) {
        return Result.success(userService.resetPasswordByAdmin(id));
    }
}