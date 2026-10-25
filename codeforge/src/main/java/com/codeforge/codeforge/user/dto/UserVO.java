package com.codeforge.codeforge.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String role;
    private String email;
    private String avatar;
    private LocalDateTime createTime;
}
