-- ============================================================
-- V7__seed_problems.sql   题库种子数据：10 道规范示例题
-- ------------------------------------------------------------
-- 执行方式（PowerShell，在本文件所在目录执行）：
--   mysql --host=127.0.0.1 --user=root --password=1234 --database=codeforge --default-character-set=utf8mb4 -e "source V7__seed_problems.sql"
-- 说明：
--   1) 仅需执行一次；重复执行会新增重复题目。
--   2) 创建者统一用 admin01（sys_user.id = 12，ADMIN 角色）。
--   3) 每道题：problem 1 条 + problem_version 1 条（version=1）+ 若干测试用例。
--   4) 测试用例 is_public：1=公开样例（普通用户可见），0=隐藏用例（参与判题但不下发）。
-- ============================================================

SET NAMES utf8mb4;

-- ------------------------------------------------------------
-- 1. A+B Problem（入门）
-- ------------------------------------------------------------
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `status`, `creator_id`) VALUES
('A+B Problem', 'EASY', '["入门", "基础运算"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO `problem_version` (`problem_id`, `version`, `description`, `input_format`, `output_format`, `time_limit`, `memory_limit`, `created_by`) VALUES
(@pid, 1, '读入两个整数 a 和 b，输出它们的和。', '一行两个整数 a、b，用空格分隔。', '一个整数，表示 a+b 的结果。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO `problem_test_case` (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`) VALUES
(@vid, 1, '1 2', '3', 1),
(@vid, 2, '-5 8', '3', 1),
(@vid, 3, '100 200', '300', 0);

-- ------------------------------------------------------------
-- 2. 阶乘（入门）
-- ------------------------------------------------------------
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `status`, `creator_id`) VALUES
('阶乘', 'EASY', '["入门", "循环"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO `problem_version` (`problem_id`, `version`, `description`, `input_format`, `output_format`, `time_limit`, `memory_limit`, `created_by`) VALUES
(@pid, 1, '读入一个非负整数 n，输出 n 的阶乘 n!。', '一行一个整数 n（0 ≤ n ≤ 20）。', '一个整数，表示 n!。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO `problem_test_case` (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`) VALUES
(@vid, 1, '5', '120', 1),
(@vid, 2, '0', '1', 1),
(@vid, 3, '10', '3628800', 0);

-- ------------------------------------------------------------
-- 3. 斐波那契数列（动态规划入门）
-- ------------------------------------------------------------
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `status`, `creator_id`) VALUES
('斐波那契数列', 'EASY', '["动态规划", "递归"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO `problem_version` (`problem_id`, `version`, `description`, `input_format`, `output_format`, `time_limit`, `memory_limit`, `created_by`) VALUES
(@pid, 1, '斐波那契数列定义为 F(0)=0, F(1)=1, F(n)=F(n-1)+F(n-2)。输出第 n 项的值。', '一行一个整数 n（0 ≤ n ≤ 30）。', '一个整数，表示 F(n)。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO `problem_test_case` (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`) VALUES
(@vid, 1, '5', '5', 1),
(@vid, 2, '10', '55', 1),
(@vid, 3, '30', '832040', 0);

-- ------------------------------------------------------------
-- 4. 素数判断（数学）
-- ------------------------------------------------------------
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `status`, `creator_id`) VALUES
('素数判断', 'EASY', '["数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO `problem_version` (`problem_id`, `version`, `description`, `input_format`, `output_format`, `time_limit`, `memory_limit`, `created_by`) VALUES
(@pid, 1, '素数指大于 1 且只能被 1 和自身整除的整数。判断给定整数 n 是否为素数。', '一行一个整数 n（1 ≤ n ≤ 1000000）。', '一行，素数输出 YES，否则输出 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO `problem_test_case` (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`) VALUES
(@vid, 1, '2', 'YES', 1),
(@vid, 2, '9', 'NO', 1),
(@vid, 3, '1', 'NO', 0),
(@vid, 4, '97', 'YES', 0);

-- ------------------------------------------------------------
-- 5. 最大公约数（数论）
-- ------------------------------------------------------------
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `status`, `creator_id`) VALUES
('最大公约数', 'EASY', '["数学", "数论"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO `problem_version` (`problem_id`, `version`, `description`, `input_format`, `output_format`, `time_limit`, `memory_limit`, `created_by`) VALUES
(@pid, 1, '求两个正整数的最大公约数（Greatest Common Divisor, gcd）。', '一行两个正整数 a、b，用空格分隔。', '一个整数，表示 a 和 b 的最大公约数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO `problem_test_case` (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`) VALUES
(@vid, 1, '12 18', '6', 1),
(@vid, 2, '48 36', '12', 1),
(@vid, 3, '100 17', '1', 0);

-- ------------------------------------------------------------
-- 6. 反转数组（数组）
-- ------------------------------------------------------------
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `status`, `creator_id`) VALUES
('反转数组', 'EASY', '["数组"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO `problem_version` (`problem_id`, `version`, `description`, `input_format`, `output_format`, `time_limit`, `memory_limit`, `created_by`) VALUES
(@pid, 1, '读入 n 个整数，按相反的顺序输出。', '第一行一个整数 n；第二行 n 个整数，用空格分隔。', '一行，逆序后的 n 个整数，用空格分隔。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO `problem_test_case` (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`) VALUES
(@vid, 1, '3\n1 2 3', '3 2 1', 1),
(@vid, 2, '5\n5 4 3 2 1', '1 2 3 4 5', 1),
(@vid, 3, '4\n-1 0 8 7', '7 8 0 -1', 0);

-- ------------------------------------------------------------
-- 7. 最大子数组和（动态规划）
-- ------------------------------------------------------------
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `status`, `creator_id`) VALUES
('最大子数组和 II', 'MEDIUM', '["数组", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO `problem_version` (`problem_id`, `version`, `description`, `input_format`, `output_format`, `time_limit`, `memory_limit`, `created_by`) VALUES
(@pid, 1, '给定一个长度为 n 的整数数组，求连续子数组的最大和（子数组至少包含一个元素）。', '第一行一个整数 n；第二行 n 个整数，用空格分隔。', '一个整数，表示最大连续子数组和。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO `problem_test_case` (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`) VALUES
(@vid, 1, '6\n1 -2 3 10 -4 7', '16', 1),
(@vid, 2, '3\n5 -3 5', '7', 1),
(@vid, 3, '5\n2 3 -8 7 6', '13', 0);

-- ------------------------------------------------------------
-- 8. 爬楼梯（动态规划）
-- ------------------------------------------------------------
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `status`, `creator_id`) VALUES
('爬楼梯', 'MEDIUM', '["动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO `problem_version` (`problem_id`, `version`, `description`, `input_format`, `output_format`, `time_limit`, `memory_limit`, `created_by`) VALUES
(@pid, 1, '每次你可以爬 1 或 2 级台阶，求爬到第 n 级台阶一共有多少种不同的方法。', '一行一个整数 n（1 ≤ n ≤ 45）。', '一个整数，表示方法总数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO `problem_test_case` (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`) VALUES
(@vid, 1, '2', '2', 1),
(@vid, 2, '3', '3', 1),
(@vid, 3, '10', '89', 0);

-- ------------------------------------------------------------
-- 9. 快速幂（数学）
-- ------------------------------------------------------------
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `status`, `creator_id`) VALUES
('快速幂', 'MEDIUM', '["数学", "快速幂"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO `problem_version` (`problem_id`, `version`, `description`, `input_format`, `output_format`, `time_limit`, `memory_limit`, `created_by`) VALUES
(@pid, 1, '计算 a 的 b 次幂对 1000000007 取模的结果，即 a^b mod 1000000007。', '一行两个整数 a、b，用空格分隔（0 ≤ a ≤ 10^9，0 ≤ b ≤ 10^9）。', '一个整数，表示取模后的结果。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO `problem_test_case` (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`) VALUES
(@vid, 1, '2 10', '1024', 1),
(@vid, 2, '3 4', '81', 1),
(@vid, 3, '2 40', '511620083', 0);

-- ------------------------------------------------------------
-- 10. 编辑距离（动态规划·困难）
-- ------------------------------------------------------------
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `status`, `creator_id`) VALUES
('编辑距离', 'HARD', '["动态规划", "字符串"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO `problem_version` (`problem_id`, `version`, `description`, `input_format`, `output_format`, `time_limit`, `memory_limit`, `created_by`) VALUES
(@pid, 1, '给定两个字符串 word1 和 word2，最少需要多少次插入、删除或替换操作，才能把 word1 变成 word2。', '第一行一个字符串 word1；第二行一个字符串 word2（仅小写字母，长度 ≤ 500）。', '一个整数，表示最少编辑次数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO `problem_test_case` (`problem_version_id`, `sort_no`, `input`, `output`, `is_public`) VALUES
(@vid, 1, 'horse\nros', '3', 1),
(@vid, 2, 'intention\nexecution', '5', 1),
(@vid, 3, 'abc\nabc', '0', 0);