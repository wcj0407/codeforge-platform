package com.codeforge.codeforge.problemset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "problem_set_item")
public class ProblemSetItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long setId;
    private Long problemId;
    /** 题目在题单中的顺序，从 1 开始 */
    private Integer sortNo;
    private LocalDateTime createTime;
}
