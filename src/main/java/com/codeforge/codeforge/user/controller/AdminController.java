package com.codeforge.codeforge.user.controller;

import com.codeforge.codeforge.common.Result;
import com.codeforge.codeforge.common.annotation.RequireRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @RequireRole("ADMIN")
    @GetMapping("/users")
    public Result<String> listUsers() {
        return Result.success("这是管理员才能看到的用户列表");
    }
}