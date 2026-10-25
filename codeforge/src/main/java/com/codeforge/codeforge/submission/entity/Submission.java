package com.codeforge.codeforge.submission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "submission")
public class Submission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long problemId;
    private Integer problemVersion;
    private Long userId;
    private String code;
    private String language;
    private String status;
    private Integer score;
    private Integer timeUsed;
    private Integer memoryUsed;
    private String errorMessage;
    private LocalDateTime submitTime;
    private LocalDateTime judgeTime;
}
