package com.codeforge.codeforge.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codeforge.codeforge.user.entity.PasswordResetCode;

public interface PasswordResetService extends IService<PasswordResetCode> {
    /** 发送重置验证码到用户绑定邮箱（账号不存在/未绑定邮箱时静默返回，避免枚举账号） */
    void sendResetCode(String username);

    /** 校验验证码并重置密码 */
    void resetPassword(String username, String code, String newPassword);
}