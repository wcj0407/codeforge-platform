package com.codeforge.codeforge.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "exam")

public class Exam {
        @TableId(type = IdType.AUTO)
        private Long id;
        private String title;
        private String description;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Long creatorId;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        /** 逻辑删除：0-未删除，1-已删除 */
        @TableLogic
        private Integer deleted;
}

