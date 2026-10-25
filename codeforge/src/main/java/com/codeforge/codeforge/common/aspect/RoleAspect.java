package com.codeforge.codeforge.common.aspect;

import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.annotation.RequireRole;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.user.dto.UserVO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleAspect {
    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        UserVO userVO = UserContext.get();
        if (userVO == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String[] value = requireRole.value();
        for (String role : value) {
            if (role.equals(userVO.getRole())) {
                return joinPoint.proceed();
            }
        }

        throw new BusinessException(ErrorCode.FORBIDDEN);
    }
}
