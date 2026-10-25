-- ============================================================
-- V2__problem_test_case.sql   新增：测试用例独立表
-- ------------------------------------------------------------
-- 执行方式（PowerShell，在本文件所在目录执行）：
--   mysql --host=127.0.0.1 --user=root --password=1234 --database=codeforge < V2__problem_test_case.sql
-- 说明：可重复执行。本文件只建表，不改动已有数据。
--
-- 背景：原先测试用例塞在 problem_version.test_cases 的 JSON 字段里，
-- 无法区分"公开样例"和"隐藏用例"，无法排序，也无法按用例记录判题结果。
-- 本表把用例拆成行，新增 is_public 字段用于控制下发（隐藏用例不下发给普通用户）。
-- ============================================================

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `problem_test_case` (
  `id`                 bigint NOT NULL AUTO_INCREMENT,
  `problem_version_id` bigint NOT NULL COMMENT '所属题目版本ID，关联 problem_version.id',
  `sort_no`            int    NOT NULL DEFAULT '0' COMMENT '用例顺序，从 1 开始',
  `input`              text   NOT NULL COMMENT '测试输入',
  `output`             text   NOT NULL COMMENT '期望输出',
  `is_public`          tinyint NOT NULL DEFAULT '0' COMMENT '是否公开样例：1-公开（可下发前端），0-隐藏',
  `created_at`         datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_version_sort` (`problem_version_id`,`sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试用例表（按题目版本）';
