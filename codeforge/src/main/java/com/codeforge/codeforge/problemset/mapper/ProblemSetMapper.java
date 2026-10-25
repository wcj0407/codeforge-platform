package com.codeforge.codeforge.problemset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codeforge.codeforge.problemset.entity.ProblemSet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProblemSetMapper extends BaseMapper<ProblemSet> {
}
