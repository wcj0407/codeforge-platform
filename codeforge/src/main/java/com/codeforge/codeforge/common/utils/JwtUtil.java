package com.codeforge.codeforge.common.utils;

import com.codeforge.codeforge.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(User user) {
        return Jwts.builder()
//                通过subject可以知道这个token是什么用户的
                .subject(user.getId().toString())
                //自定义参数用claim
                .claim("username",user.getUsername())
                .claim("role",user.getRole())
                //添加时间
                .issuedAt(new Date())
                //添加过期时间
                .expiration(new Date(System.currentTimeMillis() + expiration))
                //添加密钥
                .signWith(getkey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getkey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getkey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

}
