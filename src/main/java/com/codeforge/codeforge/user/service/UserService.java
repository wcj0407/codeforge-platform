package com.codeforge.codeforge.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codeforge.codeforge.user.dto.RegisterRequest;
import com.codeforge.codeforge.user.dto.UserVO;
import com.codeforge.codeforge.user.entity.User;

public interface UserService extends IService<User> {
    UserVO register(RegisterRequest registerRequest);
}
