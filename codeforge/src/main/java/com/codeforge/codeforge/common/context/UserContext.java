package com.codeforge.codeforge.common.context;

import com.codeforge.codeforge.user.dto.UserVO;

public class UserContext {
    private static final ThreadLocal<UserVO> CURRENT_USER = new ThreadLocal<>();
    public static UserVO get() {
        return CURRENT_USER.get();
    }
    public static void set(UserVO user) {
        CURRENT_USER.set(user);
    }
    public static void clear() {
        CURRENT_USER.remove();
    }

}
