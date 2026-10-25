-- ============================================================
-- V8__user_avatar.sql   用户头像字段
-- ------------------------------------------------------------
-- 执行方式（PowerShell）：
--   mysql --host=127.0.0.1 --user=root --password=1234 --database=codeforge --default-character-set=utf8mb4 -e "source V8__user_avatar.sql"
-- 说明：给 sys_user 增加头像地址字段。
-- ============================================================

SET NAMES utf8mb4;

ALTER TABLE `sys_user` ADD COLUMN `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址';