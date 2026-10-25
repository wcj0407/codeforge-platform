SET NAMES utf8mb4;
-- 100 道互不相同的 LeetCode 风格算法题，创建者 admin01(id=12)，全部已发布

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('两数之和（下标）', 'EASY', '["数组", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定一个整数数组和目标值 target，找出数组中和为 target 的两个元素，返回它们的下标（从 0 开始，第一个下标较小）。保证有且仅有一组答案。', '第一行两个整数 n、target；第二行 n 个整数。', '一行两个下标，用空格分隔；无解输出 -1 -1。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4 9
2 7 11 15', '0 1', 1), (@vid, 2, '3 6
3 2 4', '1 2', 1), (@vid, 3, '2 6
3 3', '0 1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('三数之和是否存在', 'MEDIUM', '["数组", "双指针"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '判断数组中是否存在三个元素 a、b、c，使得 a + b + c = 0。', '第一行一个整数 n；第二行 n 个整数。', '存在输出 YES，否则输出 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '6
-1 0 1 2 -1 -4', 'YES', 1), (@vid, 2, '3
1 2 3', 'NO', 1), (@vid, 3, '4
0 0 0 0', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('连续子数组的最大和', 'MEDIUM', '["数组", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定一个整数数组，找出和最大的连续子数组（至少包含一个元素），输出其最大和。', '第一行一个整数 n；第二行 n 个整数。', '一个整数，表示最大的连续子数组和。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '9
-2 1 -3 4 -1 2 1 -5 4', '6', 1), (@vid, 2, '1
1', '1', 1), (@vid, 3, '5
-3 -1 -2 -4 -5', '-1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('买卖股票的最佳时机', 'EASY', '["数组", "贪心"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定每天的股票价格，只能买入一次并卖出一次（先买后卖），求最大利润。无法获利时输出 0。', '第一行一个整数 n；第二行 n 个整数表示每天价格。', '一个整数，表示最大利润。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '6
7 1 5 3 6 4', '5', 1), (@vid, 2, '5
7 6 4 3 1', '0', 1), (@vid, 3, '5
1 2 3 4 5', '4', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('移动零', 'EASY', '["数组", "双指针"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '将数组中所有 0 移动到末尾，同时保持非零元素的相对顺序。', '第一行一个整数 n；第二行 n 个整数。', '一行，移动后的数组，用空格分隔。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5
0 1 0 3 12', '1 3 12 0 0', 1), (@vid, 2, '1
0', '0', 1), (@vid, 3, '4
1 2 3 4', '1 2 3 4', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('删除有序数组中的重复项', 'EASY', '["数组", "双指针"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定一个非递减有序数组，原地删除重复元素，使每个元素只出现一次，输出处理后的数组。', '第一行一个整数 n；第二行 n 个非递减整数。', '一行，去重后的数组，用空格分隔。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '6
1 1 2 2 3 4', '1 2 3 4', 1), (@vid, 2, '1
1', '1', 1), (@vid, 3, '5
0 0 1 1 2', '0 1 2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('合并两个有序数组', 'EASY', '["数组", "双指针"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定两个非递减有序数组，将它们合并为一个新的非递减数组。', '第一行两个整数 n、m；第二行 n 个整数；第三行 m 个整数。', '一行，合并后的数组，用空格分隔。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3 3
1 2 3
2 5 6', '1 2 2 3 5 6', 1), (@vid, 2, '2 2
1 4
2 3', '1 2 3 4', 1), (@vid, 3, '1 1
5
1', '1 5', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('旋转数组', 'MEDIUM', '["数组"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '将数组向右轮转 k 个位置，输出轮转后的数组。', '第一行两个整数 n、k；第二行 n 个整数。', '一行，轮转后的数组。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '7 3
1 2 3 4 5 6 7', '5 6 7 1 2 3 4', 1), (@vid, 2, '4 2
-1 -100 3 99', '3 99 -1 -100', 1), (@vid, 3, '3 4
1 2 3', '3 1 2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('加一', 'EASY', '["数组", "数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定一个由数字组成的非空数组表示一个非负整数，将这个整数加一，输出结果数组。', '第一行一个整数 n；第二行 n 个数字（0-9），无前导零（数字 0 本身除外）。', '一行，加一后的数字数组。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3
1 2 3', '1 2 4', 1), (@vid, 2, '1
9', '1 0', 1), (@vid, 3, '3
9 9 9', '1 0 0 0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('多数元素', 'EASY', '["数组", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定一个大小为 n 的数组，找出其中出现次数超过 n/2 的元素。保证该元素一定存在。', '第一行一个整数 n；第二行 n 个整数。', '一个整数，表示多数元素。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3
3 2 3', '3', 1), (@vid, 2, '7
2 2 1 1 1 2 2', '2', 1), (@vid, 3, '1
1', '1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('反转字符串', 'EASY', '["字符串", "双指针"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '将给定的字符串反转后输出。', '一行一个字符串（不含空格）。', '反转后的字符串。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'hello', 'olleh', 1), (@vid, 2, 'Haha', 'ahaH', 1), (@vid, 3, 'a', 'a', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('验证回文串', 'EASY', '["字符串", "双指针"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定一个字符串，只考虑字母和数字字符，忽略大小写，判断它是否为回文串。', '一行一个字符串（可包含空格和标点）。', '是回文输出 YES，否则输出 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'A man, a plan, a canal: Panama', 'YES', 1), (@vid, 2, 'race a car', 'NO', 1), (@vid, 3, '.,', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('有效的括号', 'EASY', '["字符串", "栈"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定只包含括号字符的字符串，判断括号是否有效（左括号必须以正确顺序闭合）。', '一行一个括号字符串。', '有效输出 YES，否则输出 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '()[]{}', 'YES', 1), (@vid, 2, '([)]', 'NO', 1), (@vid, 3, '{[()]}', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('最长公共前缀', 'EASY', '["字符串"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '找出字符串数组中所有字符串的最长公共前缀。', '第一行一个整数 n；第二行 n 个字符串，用空格分隔。', '最长公共前缀；不存在时输出空行。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3
flower flow flight', 'fl', 1), (@vid, 2, '3
dog racecar car', '', 1), (@vid, 3, '2
abc abc', 'abc', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('字符串中的第一个唯一字符', 'EASY', '["字符串", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '找出字符串中第一个只出现一次的字符，返回它的下标；不存在返回 -1。', '一行一个字符串（仅小写字母）。', '一个整数，表示下标；不存在输出 -1。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'leetcode', '0', 1), (@vid, 2, 'loveleetcode', '2', 1), (@vid, 3, 'aabb', '-1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('实现 strStr', 'EASY', '["字符串"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '在 haystack 中找出 needle 第一次出现的下标，不存在返回 -1。', '第一行 haystack；第二行 needle。', '一个整数，表示首次出现的下标。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'sadbutsad
sad', '0', 1), (@vid, 2, 'leetcode
leeto', '-1', 1), (@vid, 3, 'hello
ll', '2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('最后一个单词的长度', 'EASY', '["字符串"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定一个由单词和空格组成的字符串，返回最后一个单词的长度。', '一行字符串。', '一个整数，表示最后一个单词的长度。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'Hello World', '5', 1), (@vid, 2, '   fly me   to   the moon  ', '4', 1), (@vid, 3, 'luffy is still joyboy', '6', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('二进制求和', 'EASY', '["字符串", "数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定两个二进制字符串，返回它们的和（二进制字符串）。', '一行两个二进制字符串，用空格分隔。', '一个二进制字符串，表示两数之和。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '11 1', '100', 1), (@vid, 2, '1010 1011', '10101', 1), (@vid, 3, '0 0', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('字符串相加', 'EASY', '["字符串", "数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定两个非负整数字符串，返回它们的和（字符串形式）。不能使用内置大整数转换以外的库。', '一行两个数字字符串，用空格分隔。', '一个数字字符串，表示两数之和。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '11 123', '134', 1), (@vid, 2, '456 77', '533', 1), (@vid, 3, '0 0', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('压缩字符串', 'MEDIUM', '["字符串", "双指针"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '将连续重复字符压缩为「字符+次数」，次数为 1 时省略数字。例如 aabbbcc 输出 a2b3c2。', '一行一个字符串（仅小写字母）。', '压缩后的字符串。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'aabbbcc', 'a2b3c2', 1), (@vid, 2, 'abc', 'abc', 1), (@vid, 3, 'aa', 'a2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('存在重复元素', 'EASY', '["数组", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '判断数组中是否存在重复元素，存在输出 YES，否则 NO。', '第一行一个整数 n；第二行 n 个整数。', 'YES 或 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
1 2 3 1', 'YES', 1), (@vid, 2, '4
1 2 3 4', 'NO', 1), (@vid, 3, '1
1', 'NO', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('只出现一次的数字', 'EASY', '["数组", "位运算"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '数组中除一个元素只出现一次外，其余元素都出现两次，找出这个只出现一次的元素。', '第一行一个整数 n；第二行 n 个整数。', '一个整数，表示只出现一次的元素。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5
4 1 2 1 2', '4', 1), (@vid, 2, '3
2 2 1', '1', 1), (@vid, 3, '1
7', '7', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('有效的字母异位词', 'EASY', '["字符串", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '判断两个字符串是否互为字母异位词（字母种类和数量相同，顺序可不同）。', '第一行字符串 s；第二行字符串 t。', 'YES 或 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'anagram
nagaram', 'YES', 1), (@vid, 2, 'rat
car', 'NO', 1), (@vid, 3, 'a
a', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('同构字符串', 'EASY', '["字符串", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '判断两个字符串是否同构（每个字符的映射关系一一对应）。', '第一行字符串 s；第二行字符串 t。', 'YES 或 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'egg
add', 'YES', 1), (@vid, 2, 'foo
bar', 'NO', 1), (@vid, 3, 'paper
title', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('两个数组的交集', 'EASY', '["数组", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定两个数组，输出它们的交集（去重并升序排列）。', '第一行两个整数 n、m；第二行 n 个整数；第三行 m 个整数。', '一行，交集元素；为空时输出空行。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4 4
1 2 2 1
2 2', '2', 1), (@vid, 2, '3 5
4 9 5
9 4 9 8 4', '4 9', 1), (@vid, 3, '2 2
1 2
2 1', '1 2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('赎金信', 'EASY', '["字符串", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '判断 ransom 能否由 magazine 中的字符构成（每个字符只能使用一次）。', '一行两个字符串 ransom 和 magazine，用空格分隔。', '可以构成输出 YES，否则 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'a b', 'NO', 1), (@vid, 2, 'aa ab', 'NO', 1), (@vid, 3, 'aa aab', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('快乐数', 'EASY', '["数学", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '反复将数字替换为各位数字的平方和。若最终变为 1 则是快乐数，否则会无限循环。', '一行一个正整数 n。', '是快乐数输出 YES，否则 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '19', 'YES', 1), (@vid, 2, '2', 'NO', 1), (@vid, 3, '1', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('最长连续序列', 'MEDIUM', '["数组", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定未排序的整数数组，找出数字连续的最长序列的长度（不要求元素在原数组中连续）。', '第一行一个整数 n；第二行 n 个整数。', '一个整数，表示最长连续序列长度。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '10
0 3 7 2 5 8 4 6 0 1', '9', 1), (@vid, 2, '5
0 3 7 2 5', '2', 1), (@vid, 3, '3
1 2 3', '3', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('第一个重复出现的数字', 'EASY', '["数组", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '从左到右遍历数组，输出第一个第二次出现的数字；没有重复则输出 -1。', '第一行一个整数 n；第二行 n 个整数。', '一个整数，表示第一个重复数字；无重复输出 -1。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5
1 2 3 2 1', '2', 1), (@vid, 2, '3
1 2 3', '-1', 1), (@vid, 3, '2
5 5', '5', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('分糖果', 'EASY', '["数组", "哈希表"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, 'n 颗糖果，每种类型各若干颗，平均分给两个人（每人 n/2 颗），求其中一人最多能拿到多少种糖果。', '第一行一个偶数 n；第二行 n 个整数表示糖果类型。', '一个整数，表示最多糖果种类数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
1 1 2 3', '2', 1), (@vid, 2, '6
1 1 2 2 3 3', '3', 1), (@vid, 3, '2
1 1', '1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('删除字符串中的所有相邻重复项', 'EASY', '["字符串", "栈"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '反复删除字符串中相邻且相同的字符，直到无法继续删除，输出最终字符串。', '一行一个字符串（仅小写字母）。', '处理后的字符串。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'abbaca', 'ca', 1), (@vid, 2, 'azxxzy', 'ay', 1), (@vid, 3, 'a', 'a', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('逆波兰表达式求值', 'MEDIUM', '["栈", "数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定逆波兰表达式（后缀表达式），计算其值。除法向零取整。', '一行若干 token，用空格分隔，token 为整数或 + - * /。', '一个整数，表示表达式结果。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '2 1 + 3 *', '9', 1), (@vid, 2, '4 13 5 / +', '6', 1), (@vid, 3, '3 4 +', '7', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('每日温度', 'MEDIUM', '["数组", "栈"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定每天的温度，输出每一天需要等待多少天才会出现更高温度；不会升高则为 0。', '第一行一个整数 n；第二行 n 个整数表示温度。', '一行 n 个整数，表示等待天数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '8
73 74 75 71 69 72 76 73', '1 1 4 2 1 1 0 0', 1), (@vid, 2, '3
30 40 50', '1 1 0', 1), (@vid, 3, '2
30 60', '1 0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('下一个更大元素 I', 'EASY', '["数组", "栈"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定两个数组 a 和 b（b 包含 a 的所有元素），对 a 中每个元素，找出它在 b 中右侧第一个更大的元素；不存在输出 -1。', '第一行两个整数 n、m；第二行 n 个整数；第三行 m 个整数。', '一行，对应 a 中每个元素的答案。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3 5
4 1 2
1 3 4 2', '-1 3 -1', 1), (@vid, 2, '2 4
2 4
1 2 3 4', '3 -1', 1), (@vid, 3, '1 1
1
1', '-1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('柱状图中最大的矩形', 'HARD', '["数组", "栈"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定柱状图每个柱子的高度（宽度均为 1），求能勾勒出的最大矩形面积。', '第一行一个整数 n；第二行 n 个整数表示高度。', '一个整数，表示最大矩形面积。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '6
2 1 5 6 2 3', '10', 1), (@vid, 2, '2
2 4', '4', 1), (@vid, 3, '5
2 2 2 2 2', '10', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('滑动窗口最大值', 'HARD', '["数组", "队列", "滑动窗口"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定数组和窗口大小 k，窗口从左向右滑动，输出每个窗口中的最大值。', '第一行两个整数 n、k；第二行 n 个整数。', '一行，每个窗口的最大值。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '8 3
1 3 -1 -3 5 3 6 7', '3 3 5 5 6 7', 1), (@vid, 2, '3 1
1 -1 -3', '1 -1 -3', 1), (@vid, 3, '5 2
9 8 7 6 5', '9 8 7 6', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('简化路径', 'MEDIUM', '["字符串", "栈"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '将 Unix 风格绝对路径简化：处理 . 和 ..，合并多余斜杠，结果以 / 开头且不以 / 结尾。', '一行一个绝对路径字符串。', '简化后的路径。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '/home/', '/home', 1), (@vid, 2, '/../', '/', 1), (@vid, 3, '/home//foo/', '/home/foo', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('字符串解码', 'MEDIUM', '["字符串", "栈"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '按规则解码字符串：k[encoded] 表示方括号内的内容重复 k 次，可以嵌套。', '一行一个编码字符串。', '解码后的字符串。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3[a2[c]]', 'accaccacc', 1), (@vid, 2, '2[abc]3[cd]ef', 'abcabccdcdcdef', 1), (@vid, 3, '10[a]', 'aaaaaaaaaa', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('基本计算器', 'HARD', '["字符串", "栈", "数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '实现一个基本计算器，计算只包含加减运算和括号的表达式。', '一行表达式字符串，可包含空格。', '一个整数，表示计算结果。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '1 + 1', '2', 1), (@vid, 2, ' 2-1 + 2 ', '3', 1), (@vid, 3, '(1+(4+5+2)-3)+(6+8)', '23', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('最长有效括号', 'HARD', '["字符串", "栈", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定只包含左右括号的字符串，找出最长有效（格式正确且连续）括号子串的长度。', '一行括号字符串。', '一个整数，表示最长有效括号长度。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '(()', '2', 1), (@vid, 2, ')()())', '4', 1), (@vid, 3, '()(())', '6', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('二分查找', 'EASY', '["数组", "二分查找"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '在升序数组中查找目标值，存在返回下标，否则返回 -1。', '第一行两个整数 n、target；第二行 n 个升序整数。', '一个整数，表示下标或 -1。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '6 9
-1 0 3 5 9 12', '4', 1), (@vid, 2, '6 2
-1 0 3 5 9 12', '-1', 1), (@vid, 3, '1 5
5', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('搜索插入位置', 'EASY', '["数组", "二分查找"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '在升序数组中查找目标值，存在返回下标；不存在返回它应插入的位置。', '第一行两个整数 n、target；第二行 n 个升序整数。', '一个整数，表示下标或插入位置。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4 5
1 3 5 6', '2', 1), (@vid, 2, '4 2
1 3 5 6', '1', 1), (@vid, 3, '4 7
1 3 5 6', '4', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('寻找峰值', 'MEDIUM', '["数组", "二分查找"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '峰值元素指严格大于左右相邻值的元素。给定数组，返回任意一个峰值元素的下标。', '第一行一个整数 n；第二行 n 个整数（相邻元素不相等）。', '一个整数，表示峰值下标。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
1 2 3 1', '2', 1), (@vid, 2, '1
1', '0', 1), (@vid, 3, '3
1 3 2', '1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('x 的平方根', 'EASY', '["数学", "二分查找"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定非负整数 x，计算并返回 x 的算术平方根的整数部分（向下取整）。', '一行一个非负整数 x。', '一个整数，表示平方根的整数部分。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4', '2', 1), (@vid, 2, '8', '2', 1), (@vid, 3, '0', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('有效的完全平方数', 'EASY', '["数学", "二分查找"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '判断给定正整数是否为完全平方数。', '一行一个正整数 n。', 'YES 或 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '16', 'YES', 1), (@vid, 2, '14', 'NO', 1), (@vid, 3, '1', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('搜索旋转排序数组', 'MEDIUM', '["数组", "二分查找"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '一个升序数组在某个下标处被旋转，给定目标值，返回其下标；不存在返回 -1。', '第一行两个整数 n、target；第二行 n 个旋转后的整数。', '一个整数，表示下标或 -1。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '7 0
4 5 6 7 0 1 2', '4', 1), (@vid, 2, '7 3
4 5 6 7 0 1 2', '-1', 1), (@vid, 3, '1 1
1', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('寻找旋转排序数组中的最小值', 'MEDIUM', '["数组", "二分查找"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定一个旋转后的升序数组，返回其中的最小元素。', '第一行一个整数 n；第二行 n 个整数。', '一个整数，表示最小值。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5
3 4 5 1 2', '1', 1), (@vid, 2, '4
4 5 6 7', '4', 1), (@vid, 3, '2
2 1', '1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('在排序数组中查找元素的第一个和最后一个位置', 'MEDIUM', '["数组", "二分查找"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定升序数组和目标值，返回目标值第一次和最后一次出现的下标；不存在返回 -1 -1。', '第一行两个整数 n、target；第二行 n 个升序整数。', '一行两个整数，表示起始和结束下标。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '6 8
5 7 7 8 8 10', '3 4', 1), (@vid, 2, '6 6
5 7 7 8 8 10', '-1 -1', 1), (@vid, 3, '1 1
1', '0 0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('有序数组中的单一元素', 'MEDIUM', '["数组", "二分查找"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '有序数组中除一个元素只出现一次外，其余每个元素都出现两次，找出这个元素。', '第一行一个整数 n（奇数）；第二行 n 个有序整数。', '一个整数，表示只出现一次的元素。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5
1 1 2 3 3', '2', 1), (@vid, 2, '3
1 2 2', '1', 1), (@vid, 3, '1
5', '5', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('两数之和 II（有序数组）', 'MEDIUM', '["数组", "双指针", "二分查找"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定升序数组和目标值，找出两个数使它们的和为 target，返回 1 开始的下标。保证只有一组答案。', '第一行两个整数 n、target；第二行 n 个升序整数。', '一行两个 1 开始的下标。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4 9
2 7 11 15', '1 2', 1), (@vid, 2, '3 6
2 3 4', '1 3', 1), (@vid, 3, '2 6
3 3', '1 2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('爬楼梯的方案数', 'EASY', '["动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '每次可以爬 1 或 2 级台阶，求爬到第 n 级台阶共有多少种不同方法。', '一行一个正整数 n。', '一个整数，表示方法总数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '2', '2', 1), (@vid, 2, '3', '3', 1), (@vid, 3, '10', '89', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('使用最小花费爬楼梯', 'EASY', '["动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '每个台阶有一个花费，支付该花费后可以向上爬 1 或 2 级，求到达楼顶的最小花费。', '第一行一个整数 n；第二行 n 个整数表示每级台阶的花费。', '一个整数，表示最小花费。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3
10 15 20', '15', 1), (@vid, 2, '2
1 100', '1', 1), (@vid, 3, '4
1 100 1 1', '2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('打家劫舍', 'MEDIUM', '["动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '沿街房屋存放现金，不能偷相邻的两间，求一夜能偷到的最高金额。', '第一行一个整数 n；第二行 n 个非负整数表示每间房金额。', '一个整数，表示最高金额。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
1 2 3 1', '4', 1), (@vid, 2, '5
2 7 9 3 1', '12', 1), (@vid, 3, '1
5', '5', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('打家劫舍 II', 'MEDIUM', '["动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '房屋围成一个环，不能偷相邻的两间（首尾也算相邻），求最高金额。', '第一行一个整数 n；第二行 n 个非负整数。', '一个整数，表示最高金额。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3
2 3 2', '3', 1), (@vid, 2, '4
1 2 3 1', '4', 1), (@vid, 3, '5
1 2 3 4 5', '8', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('最长递增子序列', 'MEDIUM', '["数组", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定整数数组，找出最长严格递增子序列的长度（子序列不要求连续）。', '第一行一个整数 n；第二行 n 个整数。', '一个整数，表示最长递增子序列长度。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '8
10 9 2 5 3 7 101 18', '4', 1), (@vid, 2, '1
7', '1', 1), (@vid, 3, '5
5 4 3 2 1', '1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('零钱兑换', 'MEDIUM', '["动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定不同面额的硬币和总金额，求凑成总金额所需的最少硬币个数；无法凑成返回 -1。', '第一行两个整数 n、amount；第二行 n 个整数表示硬币面额。', '一个整数，表示最少硬币数或 -1。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3 11
1 2 5', '3', 1), (@vid, 2, '1 3
2', '-1', 1), (@vid, 3, '3 27
2 5 10', '4', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('完全平方数', 'MEDIUM', '["数学", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定正整数 n，找出和为 n 的完全平方数的最少数量。', '一行一个正整数 n。', '一个整数，表示最少数量。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '12', '3', 1), (@vid, 2, '13', '2', 1), (@vid, 3, '1', '1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('最长公共子序列', 'MEDIUM', '["字符串", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定两个字符串，求它们的最长公共子序列长度（子序列不要求连续）。', '第一行字符串 text1；第二行字符串 text2。', '一个整数，表示最长公共子序列长度。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'abcde
ace', '3', 1), (@vid, 2, 'abc
def', '0', 1), (@vid, 3, 'abc
abc', '3', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('字符串编辑距离', 'HARD', '["字符串", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定两个单词，求将 word1 转换成 word2 所需的最少操作数（插入、删除、替换）。', '第一行 word1；第二行 word2。', '一个整数，表示最少编辑次数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'horse
ros', '3', 1), (@vid, 2, 'intention
execution', '5', 1), (@vid, 3, 'abc
abc', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('乘积最大子数组', 'MEDIUM', '["数组", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定整数数组，找出乘积最大的连续子数组，输出最大乘积。', '第一行一个整数 n；第二行 n 个整数。', '一个整数，表示最大乘积。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
2 3 -2 4', '6', 1), (@vid, 2, '1
-2', '-2', 1), (@vid, 3, '3
-2 0 -1', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('分发饼干', 'EASY', '["贪心", "排序"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '每个孩子有胃口值，每块饼干有尺寸，饼干尺寸不小于胃口时才能满足该孩子。求最多能满足多少个孩子。', '第一行两个整数 n、m；第二行 n 个孩子胃口值；第三行 m 个饼干尺寸。', '一个整数，表示最多满足的孩子数量。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3 2
1 2 3
1 1', '1', 1), (@vid, 2, '2 3
1 2
1 2 3', '2', 1), (@vid, 3, '1 1
3
1', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('种花问题', 'EASY', '["贪心", "数组"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '0 表示空地、1 表示已种花，花不能种在相邻地块。求最多还能种多少朵花。', '第一行一个整数 n；第二行 n 个 0/1 整数。', '一个整数，表示最多还能种的花的数量。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5
1 0 0 0 1', '1', 1), (@vid, 2, '5
0 0 0 0 0', '3', 1), (@vid, 3, '5
1 0 0 0 0', '2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('判断子序列', 'EASY', '["字符串", "双指针", "贪心"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '判断字符串 s 是否为字符串 t 的子序列（不要求连续，但顺序不变）。', '一行两个字符串 s 和 t，用空格分隔。', 'YES 或 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'abc ahbgdc', 'YES', 1), (@vid, 2, 'axc ahbgdc', 'NO', 1), (@vid, 3, 'a a', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('无重叠区间', 'MEDIUM', '["贪心", "区间"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定若干区间，求最少需要移除多少个区间，才能使剩余区间互不重叠。', '第一行一个整数 n；接下来 n 行，每行两个整数表示区间起点和终点。', '一个整数，表示最少移除的区间数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
1 2
2 3
3 4
1 3', '1', 1), (@vid, 2, '2
1 2
1 2', '1', 1), (@vid, 3, '2
1 2
2 3', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('用最少数量的箭引爆气球', 'MEDIUM', '["贪心", "区间"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '气球用水平区间表示，箭可以垂直射在任意 x 位置，能引爆覆盖该位置的所有气球。求最少箭数。', '第一行一个整数 n；接下来 n 行，每行两个整数表示气球区间。', '一个整数，表示最少箭数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
10 16
2 8
1 6
7 12', '2', 1), (@vid, 2, '2
1 2
3 4', '2', 1), (@vid, 3, '3
1 2
2 3
3 4', '2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('合并区间', 'MEDIUM', '["贪心", "区间", "排序"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '合并所有重叠的区间，按起点升序输出合并后的区间。', '第一行一个整数 n；接下来 n 行，每行两个整数表示区间。', '一行，按顺序输出合并后的区间（起点 终点 起点 终点 ...）。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
1 3
2 6
8 10
15 18', '1 6 8 10 15 18', 1), (@vid, 2, '2
1 4
4 5', '1 5', 1), (@vid, 3, '2
1 2
3 4', '1 2 3 4', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('跳跃游戏', 'MEDIUM', '["贪心", "数组"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '数组每个元素表示从该位置最多可跳的步数。从下标 0 出发，判断能否到达最后一个下标。', '第一行一个整数 n；第二行 n 个非负整数。', 'YES 或 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5
2 3 1 1 4', 'YES', 1), (@vid, 2, '5
3 2 1 0 4', 'NO', 1), (@vid, 3, '1
0', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('跳跃游戏 II', 'MEDIUM', '["贪心", "数组"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '数组每个元素表示从该位置最多可跳的步数，求到达最后一个下标的最少跳跃次数。', '第一行一个整数 n；第二行 n 个非负整数。', '一个整数，表示最少跳跃次数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5
2 3 1 1 4', '2', 1), (@vid, 2, '1
0', '0', 1), (@vid, 3, '5
1 1 1 1 1', '4', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('加油站', 'MEDIUM', '["贪心", "数组"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '环形路线上有 n 个加油站，第 i 站有 gas[i] 升油，到下一站需 cost[i] 升油。求能绕环路一周的出发站编号；无解返回 -1。', '第一行一个整数 n；第二行 n 个整数表示 gas；第三行 n 个整数表示 cost。', '一个整数，表示出发站下标或 -1。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5
1 2 3 4 5
3 4 5 1 2', '3', 1), (@vid, 2, '3
2 3 4
3 4 2', '2', 1), (@vid, 3, '1
1
1', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('划分字母区间', 'MEDIUM', '["字符串", "贪心"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '把字符串划分为尽可能多的片段，同一字母最多出现在一个片段中。输出每个片段的长度。', '一行一个字符串（仅小写字母）。', '一行，每个片段的长度，用空格分隔。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, 'ababcbacadefegdehijhklij', '9 7 8', 1), (@vid, 2, 'eccbbbbdec', '10', 1), (@vid, 3, 'a', '1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('矩阵转置', 'EASY', '["矩阵"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定 r 行 c 列的矩阵，输出它的转置（c 行 r 列）。', '第一行两个整数 r、c；接下来 r 行，每行 c 个整数。', '输出转置后的矩阵，每行一行。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '2 3
1 2 3
4 5 6', '1 4\n2 5\n3 6', 1), (@vid, 2, '1 1
5', '5', 1), (@vid, 3, '2 2
1 2
3 4', '1 3\n2 4', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('螺旋矩阵', 'MEDIUM', '["矩阵", "模拟"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '按顺时针螺旋顺序遍历矩阵，输出遍历结果。', '第一行两个整数 r、c；接下来 r 行，每行 c 个整数。', '一行，按螺旋顺序输出所有元素。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3 3
1 2 3
4 5 6
7 8 9', '1 2 3 6 9 8 7 4 5', 1), (@vid, 2, '1 4
1 2 3 4', '1 2 3 4', 1), (@vid, 3, '2 2
1 2
3 4', '1 2 4 3', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('旋转图像', 'MEDIUM', '["矩阵"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '将 n x n 矩阵原地顺时针旋转 90 度，输出旋转后的矩阵。', '第一行一个整数 n；接下来 n 行，每行 n 个整数。', '输出旋转后的矩阵，每行一行。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3
1 2 3
4 5 6
7 8 9', '7 4 1\n8 5 2\n9 6 3', 1), (@vid, 2, '2
1 2
3 4', '3 1\n4 2', 1), (@vid, 3, '1
1', '1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('矩阵置零', 'MEDIUM', '["矩阵"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '若矩阵中某个元素为 0，则将它所在的行和列全部置为 0，输出处理后的矩阵。', '第一行两个整数 r、c；接下来 r 行，每行 c 个整数。', '输出处理后的矩阵，每行一行。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3 3
1 1 1
1 0 1
1 1 1', '1 0 1\n0 0 0\n1 0 1', 1), (@vid, 2, '1 2
0 1', '0 0', 1), (@vid, 3, '2 2
1 2
3 4', '1 2\n3 4', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('岛屿数量', 'MEDIUM', '["矩阵", "深度优先搜索"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '由 1（陆地）和 0（水）组成的网格中，上下左右相连的陆地算一个岛屿。求岛屿数量。', '第一行两个整数 r、c；接下来 r 行，每行 c 个 0/1 字符（空格分隔）。', '一个整数，表示岛屿数量。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4 5
1 1 0 0 0
1 1 0 0 0
0 0 1 0 0
0 0 0 1 1', '3', 1), (@vid, 2, '1 1
1', '1', 1), (@vid, 3, '2 2
0 0
0 0', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('最大正方形', 'MEDIUM', '["矩阵", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '在由 0 和 1 组成的矩阵中，找到只包含 1 的最大正方形，输出其面积。', '第一行两个整数 r、c；接下来 r 行，每行 c 个 0/1 整数。', '一个整数，表示最大正方形面积。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4 5
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0', '4', 1), (@vid, 2, '1 1
1', '1', 1), (@vid, 3, '2 2
0 0
0 0', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('不同路径', 'MEDIUM', '["动态规划", "组合数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '机器人位于 m x n 网格左上角，每次只能向下或向右移动一步，求到达右下角的不同路径数。', '一行两个整数 m、n。', '一个整数，表示路径总数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3 7', '28', 1), (@vid, 2, '3 2', '3', 1), (@vid, 3, '1 1', '1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('不同路径 II', 'MEDIUM', '["动态规划", "矩阵"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '网格中有障碍物（1 表示障碍），机器人每次只能向下或向右，求从左上角到右下角的不同路径数。', '第一行两个整数 r、c；接下来 r 行，每行 c 个 0/1 整数。', '一个整数，表示路径总数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3 3
0 0 0
0 1 0
0 0 0', '2', 1), (@vid, 2, '2 2
0 1
0 0', '1', 1), (@vid, 3, '2 2
0 0
0 0', '2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('最小路径和', 'MEDIUM', '["矩阵", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定非负整数矩阵，从左上角走到右下角（只能向下或向右），求路径上数字之和的最小值。', '第一行两个整数 r、c；接下来 r 行，每行 c 个非负整数。', '一个整数，表示最小路径和。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3 3
1 3 1
1 5 1
4 2 1', '7', 1), (@vid, 2, '2 2
1 2
3 4', '7', 1), (@vid, 3, '1 1
5', '5', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('杨辉三角的第 n 行', 'EASY', '["数组", "数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定行号 n（从 1 开始），输出杨辉三角的第 n 行。', '一行一个正整数 n。', '一行，第 n 行的所有数字。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3', '1 2 1', 1), (@vid, 2, '1', '1', 1), (@vid, 3, '5', '1 4 6 4 1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('斐波那契数', 'EASY', '["数学", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '斐波那契数列定义为 F(0)=0、F(1)=1、F(n)=F(n-1)+F(n-2)，求第 n 项。', '一行一个整数 n（0 ≤ n ≤ 30）。', '一个整数，表示 F(n)。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5', '5', 1), (@vid, 2, '0', '0', 1), (@vid, 3, '10', '55', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('第 N 个泰波那契数', 'EASY', '["数学", "动态规划"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '泰波那契数列 T(0)=0、T(1)=1、T(2)=1、T(n)=T(n-1)+T(n-2)+T(n-3)，求第 n 项。', '一行一个整数 n（0 ≤ n ≤ 37）。', '一个整数，表示 T(n)。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4', '4', 1), (@vid, 2, '0', '0', 1), (@vid, 3, '10', '149', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('计算阶乘', 'EASY', '["数学", "循环"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '读入非负整数 n，输出 n 的阶乘。', '一行一个整数 n（0 ≤ n ≤ 20）。', '一个整数，表示 n!。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5', '120', 1), (@vid, 2, '0', '1', 1), (@vid, 3, '10', '3628800', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('求两个数的最大公约数', 'EASY', '["数学", "数论"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '求两个正整数的最大公约数。', '一行两个正整数 a、b。', '一个整数，表示最大公约数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '12 18', '6', 1), (@vid, 2, '100 17', '1', 1), (@vid, 3, '48 36', '12', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('最小公倍数', 'EASY', '["数学", "数论"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '求两个正整数的最小公倍数。', '一行两个正整数 a、b。', '一个整数，表示最小公倍数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '12 18', '36', 1), (@vid, 2, '4 6', '12', 1), (@vid, 3, '7 5', '35', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('判断素数', 'EASY', '["数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '判断给定整数是否为素数（大于 1 且只能被 1 和自身整除）。', '一行一个整数 n。', '是素数输出 YES，否则 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '2', 'YES', 1), (@vid, 2, '9', 'NO', 1), (@vid, 3, '97', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('丑数 II', 'MEDIUM', '["数学", "动态规划", "堆"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '丑数是只包含质因子 2、3、5 的正整数。求第 n 个丑数。', '一行一个整数 n（1 ≤ n ≤ 1690）。', '一个整数，表示第 n 个丑数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '10', '12', 1), (@vid, 2, '1', '1', 1), (@vid, 3, '6', '6', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('计数质数', 'MEDIUM', '["数学", "数组"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '统计所有小于非负整数 n 的质数数量。', '一行一个非负整数 n。', '一个整数，表示质数个数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '10', '4', 1), (@vid, 2, '0', '0', 1), (@vid, 3, '100', '25', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('3 的幂', 'EASY', '["数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '判断给定整数是否为 3 的幂次方。', '一行一个整数 n。', 'YES 或 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '27', 'YES', 1), (@vid, 2, '0', 'NO', 1), (@vid, 3, '45', 'NO', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('数字反转', 'EASY', '["数学"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定非负整数 n，输出将它的十进制数字反转后得到的整数（忽略前导零）。', '一行一个非负整数 n。', '一个整数，表示反转后的结果。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '123', '321', 1), (@vid, 2, '120', '21', 1), (@vid, 3, '0', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('数组中的第 K 个最大元素', 'MEDIUM', '["数组", "排序", "堆"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定整数数组和整数 k，返回数组中第 k 个最大的元素（按降序排序后的第 k 个）。', '第一行两个整数 n、k；第二行 n 个整数。', '一个整数，表示第 k 个最大元素。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '6 2
3 2 1 5 6 4', '5', 1), (@vid, 2, '1 1
1', '1', 1), (@vid, 3, '5 5
1 2 3 4 5', '1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('前 K 个高频元素', 'MEDIUM', '["数组", "哈希表", "堆"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定数组和整数 k，输出出现频率最高的 k 个元素；频率相同按数值升序。', '第一行两个整数 n、k；第二行 n 个整数。', '一行 k 个整数，用空格分隔。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '6 2
1 1 1 2 2 3', '1 2', 1), (@vid, 2, '1 1
1', '1', 1), (@vid, 3, '4 2
4 4 3 3', '3 4', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('颜色分类', 'MEDIUM', '["数组", "排序", "双指针"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '数组只包含 0、1、2，分别代表红、白、蓝，请按 0、1、2 的顺序原地排序后输出。', '第一行一个整数 n；第二行 n 个 0/1/2 整数。', '一行，排序后的数组。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '5
2 0 2 1 1', '0 1 1 2 2', 1), (@vid, 2, '3
2 1 0', '0 1 2', 1), (@vid, 3, '1
0', '0', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('按奇偶排序数组', 'EASY', '["数组", "排序"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '将所有偶数放在前面、奇数放在后面，保持各自相对顺序。', '第一行一个整数 n；第二行 n 个整数。', '一行，重排后的数组。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
3 1 2 4', '2 4 3 1', 1), (@vid, 2, '3
1 2 3', '2 1 3', 1), (@vid, 3, '2
0 1', '0 1', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('最大数', 'MEDIUM', '["数组", "排序", "字符串"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '给定一组非负整数，重新排列顺序，使拼接成的数字最大，输出这个数字字符串。', '第一行一个整数 n；第二行 n 个非负整数。', '一个数字字符串，表示能拼接出的最大数。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3
10 2 35', '35210', 1), (@vid, 2, '2
3 30', '330', 1), (@vid, 3, '4
3 30 34 5', '534330', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('两个数组的交集 II', 'EASY', '["数组", "哈希表", "排序"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '输出两个数组的交集，每个元素出现次数应与在两个数组中出现的次数一致（取较小值），结果升序。', '第一行两个整数 n、m；第二行 n 个整数；第三行 m 个整数。', '一行，交集元素。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3 5
1 2 2
2 2', '2 2', 1), (@vid, 2, '2 2
1 2
1 1', '1', 1), (@vid, 3, '3 3
4 9 5
9 4 9', '4 9', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('有效的山脉数组', 'EASY', '["数组"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '判断数组是否为山脉数组：先严格递增到峰值，再严格递减。', '第一行一个整数 n；第二行 n 个整数。', 'YES 或 NO。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3
1 2 3', 'NO', 1), (@vid, 2, '4
0 3 2 1', 'YES', 1), (@vid, 3, '3
1 3 2', 'YES', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('数组拆分', 'EASY', '["数组", "排序", "贪心"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '将 2n 个数分成 n 对，每对取较小值，求这些较小值之和的最大值。', '第一行一个偶数 n；第二行 n 个整数（n 为偶数）。', '一个整数，表示最大总和。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
1 4 3 2', '4', 1), (@vid, 2, '2
6 2', '2', 1), (@vid, 3, '6
1 1 2 2 3 3', '6', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('数组中两元素的最大乘积', 'EASY', '["数组", "排序"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '选出数组中两个不同下标元素，使 (a[i]-1)*(a[j]-1) 最大，输出该最大值。', '第一行一个整数 n；第二行 n 个整数。', '一个整数，表示最大乘积。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '4
3 4 5 2', '12', 1), (@vid, 2, '3
1 5 4', '12', 1), (@vid, 3, '3
1 2 3', '2', 0);

INSERT INTO problem (title, difficulty, tags, status, creator_id) VALUES ('第三大的数', 'EASY', '["数组", "排序"]', 'PUBLISHED', 12);
SET @pid = LAST_INSERT_ID();
INSERT INTO problem_version (problem_id, version, description, input_format, output_format, time_limit, memory_limit, created_by) VALUES (@pid, 1, '输出数组中第三大的不同数字；不存在则输出最大的数字。', '第一行一个整数 n；第二行 n 个整数。', '一个整数，表示第三大的数或最大值。', 1000, 256, 12);
SET @vid = LAST_INSERT_ID();
INSERT INTO problem_test_case (problem_version_id, sort_no, input, output, is_public) VALUES (@vid, 1, '3
3 2 1', '1', 1), (@vid, 2, '2
1 2', '2', 1), (@vid, 3, '5
2 2 3 1', '1', 0);
