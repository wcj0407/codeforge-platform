package com.codeforge.codeforge.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_problem")
public class ExamProblem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examId;
    private Long problemId;
    /** 题目顺序，从 1 开始 */
    private Integer sortNo;
    private LocalDateTime createTime;
}