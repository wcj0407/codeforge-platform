-- ============================================================
-- V1__init.sql   初始表结构
-- ------------------------------------------------------------
-- 执行方式（PowerShell，在本文件所在目录执行）：
--   mysql --host=127.0.0.1 --user=root --password=1234 --database=codeforge < V1__init.sql
-- 说明：全部使用 CREATE TABLE IF NOT EXISTS，可重复执行，不会覆盖已有数据。
-- 本文件对应数据库的"初始状态"，后续变更请新建 V2、V3 ... 文件，不要回头改这里。
-- ============================================================

SET NAMES utf8mb4;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    varchar(50)  NOT NULL COMMENT '用户名',
  `password`    varchar(100) NOT NULL COMMENT '密码（BCrypt 加密）',
  `email`       varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role`        varchar(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：USER/AUTHOR/ADMIN',
  `status`      tinyint      NOT NULL DEFAULT '1' COMMENT '状态：1=正常，0=禁用',
  `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 题目主表
CREATE TABLE IF NOT EXISTS `problem` (
  `id`          bigint       NOT NULL AUTO_INCREMENT,
  `title`       varchar(200) NOT NULL COMMENT '题目标题',
  `difficulty`  varchar(20)  NOT NULL COMMENT '难度：EASY/MEDIUM/HARD',
  `tags`        json         DEFAULT NULL COMMENT '标签数组',
  `status`      varchar(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT/PUBLISHED/ARCHIVED',
  `creator_id`  bigint       NOT NULL COMMENT '创建者ID',
  `create_time` datetime     DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`     tinyint      DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_creator` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目主表';

-- 题目版本表（改描述/用例不覆盖旧版本，保证历史提交可追溯）
CREATE TABLE IF NOT EXISTS `problem_version` (
  `id`            bigint NOT NULL AUTO_INCREMENT,
  `problem_id`    bigint NOT NULL COMMENT '题目ID',
  `version`       int    NOT NULL COMMENT '版本号',
  `description`   text   NOT NULL COMMENT '题目描述',
  `input_format`  text   COMMENT '输入格式说明',
  `output_format` text   COMMENT '输出格式说明',
  `time_limit`    int    NOT NULL DEFAULT '1000' COMMENT '时间限制（毫秒）',
  `memory_limit`  int    NOT NULL DEFAULT '256' COMMENT '内存限制（MB）',
  `test_cases`    json   DEFAULT NULL COMMENT '测试用例（历史遗留：后续版本将迁移到 problem_test_case 表）',
  `created_by`    bigint NOT NULL COMMENT '创建者ID',
  `created_at`    datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_problem_version` (`problem_id`,`version`),
  KEY `idx_problem` (`problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目版本表';

-- 代码提交记录表
CREATE TABLE IF NOT EXISTS `submission` (
  `id`              bigint      NOT NULL AUTO_INCREMENT,
  `problem_id`      bigint      NOT NULL COMMENT '题目ID',
  `problem_version` int         NOT NULL COMMENT '题目版本号',
  `user_id`         bigint      NOT NULL COMMENT '提交用户ID',
  `code`            text        NOT NULL COMMENT '代码内容',
  `language`        varchar(20) NOT NULL DEFAULT 'JAVA' COMMENT '编程语言：JAVA/PYTHON/CPP',
  `status`          varchar(30) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/RUNNING/ACCEPTED/WRONG_ANSWER/TIME_LIMIT_EXCEEDED/MEMORY_LIMIT_EXCEEDED/RUNTIME_ERROR/COMPILE_ERROR',
  `score`           int         DEFAULT NULL COMMENT '得分（0-100）',
  `time_used`       int         DEFAULT NULL COMMENT '运行时间（毫秒）',
  `memory_used`     int         DEFAULT NULL COMMENT '内存使用（KB）',
  `error_message`   text        COMMENT '错误信息',
  `submit_time`     datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `judge_time`      datetime    DEFAULT NULL COMMENT '判题完成时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_problem` (`problem_id`),
  KEY `idx_status` (`status`),
  KEY `idx_submit_time` (`submit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码提交记录表';

-- 死信落库表（判题重试超限后的现场记录）
CREATE TABLE IF NOT EXISTS `judge_fail_record` (
  `id`            bigint NOT NULL AUTO_INCREMENT,
  `submission_id` bigint NOT NULL,
  `problem_id`    bigint DEFAULT NULL,
  `user_id`       bigint DEFAULT NULL,
  `fail_reason`   varchar(512) DEFAULT NULL,
  `retry_count`   int DEFAULT NULL,
  `created_at`    datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_submission_id` (`submission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='判题失败记录表';
