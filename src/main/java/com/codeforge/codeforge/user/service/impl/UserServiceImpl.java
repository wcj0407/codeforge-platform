package com.codeforge.codeforge.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.user.dto.RegisterRequest;
import com.codeforge.codeforge.user.dto.UserVO;
import com.codeforge.codeforge.user.entity.User;
import com.codeforge.codeforge.user.mapper.UserMapper;
import com.codeforge.codeforge.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserVO register(RegisterRequest registerRequest) {

        //查是否存在
        Long count = lambdaQuery().eq(User::getUsername, registerRequest.getUsername()).count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        //不存在的话，就存入数据库
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER");
        save(user);

        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setRole(user.getRole());
        return userVO;
    }
}
