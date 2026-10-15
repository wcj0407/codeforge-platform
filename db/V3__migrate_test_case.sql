-- ============================================================
-- V3__migrate_test_case.sql   迁移：problem_version.test_cases(JSON) → problem_test_case 表
-- ------------------------------------------------------------
-- 执行方式（PowerShell，在本文件所在目录执行）：
--   mysql --host=127.0.0.1 --user=root --password=1234 --database=codeforge < V3__migrate_test_case.sql
-- 说明：可重复执行（版本级幂等：某题目版本已有用例记录则整体跳过）。
--       迁移时所有历史用例统一标为公开样例(is_public=1)，保持与迁移前一致的行为；
--       之后作者可在编辑题目时为每个用例单独标记公开/隐藏。
-- ============================================================

SET NAMES utf8mb4;

INSERT INTO problem_test_case (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`)
SELECT
    pv.id,
    jt.ord,
    jt.`input`,
    jt.`output`,
    1
FROM problem_version pv
JOIN JSON_TABLE(
    pv.test_cases,
    '$[*]' COLUMNS (
        ord FOR ORDINALITY,
        `input`  TEXT PATH '$.input',
        `output` TEXT PATH '$.output'
    )
) jt
WHERE pv.test_cases IS NOT NULL
  AND JSON_LENGTH(pv.test_cases) > 0
  AND NOT EXISTS (
        SELECT 1 FROM problem_test_case ptc WHERE ptc.problem_version_id = pv.id
  );