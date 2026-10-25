-- ============================================================
-- V6__exam.sql   限时考核
-- 执行方式（PowerShell）：
--   mysql --host=127.0.0.1 --user=root --password=1234 --database=codeforge < V6__exam.sql
-- ============================================================

SET NAMES utf8mb4;

-- 考核主表
CREATE TABLE IF NOT EXISTS `exam` (
  `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '考核ID',
  `title`       varchar(200) NOT NULL COMMENT '考核名称',
  `description` varchar(500) DEFAULT NULL COMMENT '考核说明',
  `start_time`  datetime     NOT NULL COMMENT '开始时间',
  `end_time`    datetime     NOT NULL COMMENT '结束时间',
  `creator_id`  bigint       NOT NULL COMMENT '创建者ID（管理员）',
  `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     tinyint      NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='限时考核表';

-- 考核-题目关联表
CREATE TABLE IF NOT EXISTS `exam_problem` (
  `id`          bigint    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exam_id`     bigint    NOT NULL COMMENT '考核ID',
  `problem_id`  bigint    NOT NULL COMMENT '题目ID',
  `sort_no`     int       NOT NULL DEFAULT '1' COMMENT '题目顺序，从 1 开始',
  `create_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  KEY `idx_exam` (`exam_id`),
  UNIQUE KEY `uk_exam_problem` (`exam_id`, `problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考核-题目关联表';