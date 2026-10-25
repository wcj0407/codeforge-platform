-- ============================================================
-- V4__outbox.sql   事务发件箱表（Outbox 模式）
-- ------------------------------------------------------------
-- 目的：解决「提交记录落库」与「发送判题消息」之间的双写不一致。
--       提交时，在同一个数据库事务里把「待发送消息」写进本表；
--       再由定时任务扫出 PENDING 记录异步发送，发成功标记 SENT。
-- 执行方式（PowerShell，在本文件所在目录执行）：
--   mysql --host=127.0.0.1 --user=root --password=1234 --database=codeforge < V4__outbox.sql
-- ============================================================

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `outbox_event` (
  `id`            bigint        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event_type`    varchar(50)   NOT NULL COMMENT '事件类型（决定发往哪个队列），如 JUDGE_SUBMISSION',
  `payload`       varchar(1000) NOT NULL COMMENT '消息体（JSON 字符串，如 {"submissionId":123}）',
  `status`        varchar(20)   NOT NULL DEFAULT 'PENDING' COMMENT '发送状态：PENDING待发送/SENT已发送',
  `retry_count`   int           NOT NULL DEFAULT '0' COMMENT '已重试次数',
  `error_message` varchar(500)  DEFAULT NULL COMMENT '最近一次发送失败原因',
  `create_time`   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `sent_time`     datetime      DEFAULT NULL COMMENT '发送成功时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_create` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='事务发件箱表';