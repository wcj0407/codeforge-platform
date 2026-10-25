package com.codeforge.codeforge.problem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("problem_test_case")
public class ProblemTestCase {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属题目版本ID，关联 problem_version.id */
    private Long problemVersionId;

    /** 用例顺序，从 1 开始 */
    private Integer sortNo;

    private String input;

    private String output;

    /** 是否公开样例：1-公开（可下发前端），0-隐藏 */
    private Integer isPublic;

    private LocalDateTime createdAt;
}