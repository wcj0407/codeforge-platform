package com.codeforge.codeforge.problem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codeforge.codeforge.problem.entity.Problem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProblemMapper extends BaseMapper<Problem> {
}
