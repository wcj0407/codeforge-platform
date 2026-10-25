package com.codeforge.codeforge.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/** 管理员视角的用户信息（比 UserVO 多一个 status，用于列表展示账号状态） */
@Data
public class AdminUserVO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
}