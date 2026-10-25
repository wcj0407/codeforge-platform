-- ============================================================
-- V5__problem_set.sql   训练题单
-- 执行方式（PowerShell，在本文件所在目录执行）：
--   mysql --host=127.0.0.1 --user=root --password=1234 --database=codeforge < V5__problem_set.sql
-- ============================================================

SET NAMES utf8mb4;

-- 题单主表
CREATE TABLE IF NOT EXISTS `problem_set` (
  `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '题单ID',
  `title`       varchar(200) NOT NULL COMMENT '题单名称',
  `description` varchar(500) DEFAULT NULL COMMENT '题单简介',
  `creator_id`  bigint       NOT NULL COMMENT '创建者ID',
  `is_public`   tinyint      NOT NULL DEFAULT '0' COMMENT '是否公开：1=公开，0=私有',
  `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     tinyint      NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_creator` (`creator_id`),
  KEY `idx_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题单表';

-- 题单-题目关联表
CREATE TABLE IF NOT EXISTS `problem_set_item` (
  `id`          bigint    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `set_id`      bigint    NOT NULL COMMENT '所属题单ID',
  `problem_id`  bigint    NOT NULL COMMENT '题目ID',
  `sort_no`     int       NOT NULL DEFAULT '1' COMMENT '题目在题单中的顺序，从 1 开始',
  `create_time` datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  KEY `idx_set` (`set_id`),
  UNIQUE KEY `uk_set_problem` (`set_id`, `problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题单-题目关联表';