package com.codeforge.codeforge.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.user.entity.PasswordResetCode;
import com.codeforge.codeforge.user.entity.User;
import com.codeforge.codeforge.user.mapper.PasswordResetCodeMapper;
import com.codeforge.codeforge.user.mapper.UserMapper;
import com.codeforge.codeforge.user.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl extends ServiceImpl<PasswordResetCodeMapper, PasswordResetCode> implements PasswordResetService {

    private static final int EXPIRE_MINUTES = 10;
    private static final int RESEND_SECONDS = 60;

    private final UserMapper userMapper;
    private final PasswordResetCodeMapper resetCodeMapper;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.username:}")
    private String from;

    @Override
    @Transactional
    public void sendResetCode(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        // 账号不存在或未绑定邮箱：静默返回，不暴露账号状态
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            log.info("密码重置验证码请求：账号不存在或未绑定邮箱, username={}", username);
            return;
        }
        // 60 秒内只能发一次，防止刷邮件
        PasswordResetCode last = resetCodeMapper.selectOne(
                new LambdaQueryWrapper<PasswordResetCode>()
                        .eq(PasswordResetCode::getUserId, user.getId())
                        .orderByDesc(PasswordResetCode::getId)
                        .last("limit 1"));
        if (last != null && last.getCreateTime() != null
                && last.getCreateTime().isAfter(LocalDateTime.now().minusSeconds(RESEND_SECONDS))) {
            log.info("密码重置验证码请求过于频繁, userId={}", user.getId());
            return;
        }

        // 旧验证码全部作废
        PasswordResetCode invalid = new PasswordResetCode();
        invalid.setUsed(1);
        resetCodeMapper.update(invalid, new LambdaQueryWrapper<PasswordResetCode>()
                .eq(PasswordResetCode::getUserId, user.getId())
                .eq(PasswordResetCode::getUsed, 0));

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        PasswordResetCode row = new PasswordResetCode();
        row.setUserId(user.getId());
        row.setCodeHash(sha256(code));
        row.setExpireAt(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
        row.setUsed(0);
        resetCodeMapper.insert(row);

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.error("未配置邮件发送器，无法发送验证码");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        SimpleMailMessage message = new SimpleMailMessage();
        if (!from.isBlank()) {
            message.setFrom(from);
        }
        message.setTo(user.getEmail());
        message.setSubject("CodeForge 密码重置验证码");
        message.setText("你的密码重置验证码是：" + code + "\n\n有效期 " + EXPIRE_MINUTES
                + " 分钟，仅可使用一次。\n如非本人操作，请忽略本邮件。");
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("发送密码重置邮件失败, userId={}", user.getId(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    public void resetPassword(String username, String code, String newPassword) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            throw new BusinessException(ErrorCode.RESET_CODE_INVALID);
        }
        PasswordResetCode row = resetCodeMapper.selectOne(
                new LambdaQueryWrapper<PasswordResetCode>()
                        .eq(PasswordResetCode::getUserId, user.getId())
                        .eq(PasswordResetCode::getUsed, 0)
                        .gt(PasswordResetCode::getExpireAt, LocalDateTime.now())
                        .orderByDesc(PasswordResetCode::getId)
                        .last("limit 1"));
        if (row == null || !sha256(code).equals(row.getCodeHash())) {
            throw new BusinessException(ErrorCode.RESET_CODE_INVALID);
        }

        // 验证码一次性：用掉立即失效
        row.setUsed(1);
        resetCodeMapper.updateById(row);

        User update = new User();
        update.setId(user.getId());
        update.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(update);
    }

    private String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}