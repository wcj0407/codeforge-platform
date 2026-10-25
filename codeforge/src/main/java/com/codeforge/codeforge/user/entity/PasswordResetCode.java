package com.codeforge.codeforge.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("password_reset_code")
public class PasswordResetCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    /** 验证码的 SHA-256 哈希，不存明文 */
    private String codeHash;
    private LocalDateTime expireAt;
    /** 0-未使用，1-已使用 */
    private Integer used;
    private LocalDateTime createTime;
}