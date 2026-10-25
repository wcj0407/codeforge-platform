package com.codeforge.codeforge.submission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codeforge.codeforge.submission.entity.Submission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {

    /**
     * 原子抢占判题权：只有 PENDING / SYSTEM_ERROR 的提交能改成 RUNNING。
     * 返回受影响行数，0 表示已被其他消费者抢走，直接跳过即可。
     */
    @Update("UPDATE submission SET status = 'RUNNING' WHERE id = #{id} AND status IN ('PENDING', 'SYSTEM_ERROR')")
    int claimForJudge(@Param("id") Long id);
}
