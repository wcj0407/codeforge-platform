package com.codeforge.codeforge.problem.constant;

public final class ProblemStatus {

    private ProblemStatus() {
    }

    /** 草稿：作者刚创建 / 编辑中，未对外发布 */
    public static final String DRAFT = "DRAFT";

    /** 待审核：作者已提交，等待管理员审核 */
    public static final String PENDING_REVIEW = "PENDING_REVIEW";

    /** 已发布：管理员审核通过，普通用户可见 */
    public static final String PUBLISHED = "PUBLISHED";

    /** 已驳回：管理员驳回，作者可见驳回原因 */
    public static final String REJECTED = "REJECTED";

    /** 已下架：从已发布状态下架，不再对外展示 */
    public static final String OFFLINE = "OFFLINE";
}