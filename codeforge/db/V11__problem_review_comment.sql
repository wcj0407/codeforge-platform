-- ============================================================
-- V11__problem_review_comment.sql   题目审核驳回原因
-- ------------------------------------------------------------
-- 说明：两段式审核（DRAFT → PENDING_REVIEW → PUBLISHED / REJECTED）
-- 需要记录管理员驳回时填写的原因，此前该列只在本地库手工添加，
-- 从 V1-V10 全新建库时会缺失，导致题目详情接口报错。
-- 可重复执行前请确认列不存在（MySQL 8.0.29+ 可用 IF NOT EXISTS）。
-- ============================================================

SET NAMES utf8mb4;

ALTER TABLE `problem`
  ADD COLUMN `review_comment` varchar(500) DEFAULT NULL COMMENT '审核驳回原因';
