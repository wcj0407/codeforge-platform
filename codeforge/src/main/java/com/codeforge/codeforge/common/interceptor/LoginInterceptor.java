package com.codeforge.codeforge.common.interceptor;

import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.common.utils.JwtUtil;
import com.codeforge.codeforge.user.dto.UserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       //通过request来获取请求头
        String header = request.getHeader("Authorization");
        //先判断是否有数据或者数据头是否正确
        if(header==null ||!header.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        //获取token,切割bearer 来获取真正的token
        String token = header.substring(7);
        try {
            Claims claims = jwtUtil.parseToken(token);
            UserVO userVO=new UserVO();
            userVO.setId(Long.valueOf(claims.getSubject()));
            userVO.setUsername(claims.get("username",String.class));
            userVO.setRole(claims.get("role",String.class));
            UserContext.set(userVO);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
          UserContext.clear();
    }
}
