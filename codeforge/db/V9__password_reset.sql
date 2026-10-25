-- ============================================================
-- V9__password_reset.sql   邮箱唯一索引 + 密码重置验证码表
-- ------------------------------------------------------------
-- 执行方式（PowerShell）：
--   mysql --host=127.0.0.1 --user=root --password=1234 --database=codeforge --default-character-set=utf8mb4 -e "source V9__password_reset.sql"
-- ============================================================

SET NAMES utf8mb4;

-- 邮箱唯一：MySQL 唯一索引允许多个 NULL，存量未绑定邮箱的用户不受影响
ALTER TABLE `sys_user` ADD UNIQUE KEY `uk_email` (`email`);

-- 密码重置验证码（存哈希，不存明文；一次性使用）
CREATE TABLE IF NOT EXISTS `password_reset_code` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `user_id`     bigint       NOT NULL COMMENT '用户ID',
  `code_hash`   varchar(128) NOT NULL COMMENT '验证码 SHA-256 哈希',
  `expire_at`   datetime     NOT NULL COMMENT '过期时间',
  `used`        tinyint      NOT NULL DEFAULT '0' COMMENT '是否已使用：0-未使用，1-已使用',
  `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_create` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='密码重置验证码表';
