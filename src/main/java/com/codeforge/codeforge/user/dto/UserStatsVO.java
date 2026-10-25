package com.codeforge.codeforge.user.dto;

import lombok.Data;

/** 个人中心刷题统计 */
@Data
public class UserStatsVO {
    /** 提交总数 */
    private long totalSubmissions;
    /** 通过的提交数（status = ACCEPTED） */
    private long acceptedSubmissions;
    /** 通过题数（去重后的题目数） */
    private long solvedProblemCount;
    /** 尝试过的题数（去重后的题目数） */
    private long attemptedProblemCount;
    /** 通过率（百分比整数）：通过的提交数 / 提交总数 */
    private int passRate;
}