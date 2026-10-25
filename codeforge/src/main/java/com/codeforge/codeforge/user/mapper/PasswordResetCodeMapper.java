package com.codeforge.codeforge.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codeforge.codeforge.user.entity.PasswordResetCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PasswordResetCodeMapper extends BaseMapper<PasswordResetCode> {
}