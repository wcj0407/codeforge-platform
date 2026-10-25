package com.codeforge.codeforge.common;

import com.codeforge.codeforge.common.annotation.RequireRole;
import com.codeforge.codeforge.common.aspect.RoleAspect;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.user.dto.UserVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 权限测试：@RequireRole 注解在进入业务方法前完成角色校验。
 */
class RoleAspectTest {

    @AfterEach
    void clearContext() {
        UserContext.clear();
    }

    @Test
    void unauthenticatedUserIsRejected() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> proxy().adminOnly());

        assertEquals(ErrorCode.UNAUTHORIZED, ex.getErrorCode());
    }

    @Test
    void normalUserCannotAccessAdminMethod() {
        UserContext.set(user("USER"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> proxy().adminOnly());

        assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void authorCannotAccessAdminMethod() {
        UserContext.set(user("AUTHOR"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> proxy().adminOnly());

        assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void adminCanAccessAdminMethod() {
        UserContext.set(user("ADMIN"));

        assertEquals("admin", proxy().adminOnly());
    }

    @Test
    void authorCanAccessAuthorMethod() {
        UserContext.set(user("AUTHOR"));

        assertEquals("author", proxy().authorOnly());
    }

    @Test
    void normalUserCannotAccessAuthorMethod() {
        UserContext.set(user("USER"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> proxy().authorOnly());

        assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    private RoleService proxy() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new RoleService());
        factory.addAspect(new RoleAspect());
        return factory.getProxy();
    }

    private UserVO user(String role) {
        UserVO user = new UserVO();
        user.setId(1L);
        user.setUsername("tester");
        user.setRole(role);
        return user;
    }

    static class RoleService {
        @RequireRole({"ADMIN"})
        public String adminOnly() {
            return "admin";
        }

        @RequireRole({"AUTHOR", "ADMIN"})
        public String authorOnly() {
            return "author";
        }
    }
}
