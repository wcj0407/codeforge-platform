package com.codeforge.codeforge.problemset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "problem_set")
public class ProblemSet {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Long creatorId;
    /** 是否公开：1-公开，0-私有 */
    private Integer isPublic;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    /** 逻辑删除：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}
