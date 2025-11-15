package world.xuewei.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    public static final String JWT_SECRET = "your_very_secure_secret_key_that_is_at_least_32_bytes";

    //密钥
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    //过期时间
    public static final long JWT_EXPIRATION = 1000 * 60 * 60; // 1小时
    /**
     * 生成 JWT Token
     * @param subject 通常是用户ID或用户名
     * @param claims 附加信息 (如角色、权限)
     * @return 生成的 JWT 字符串
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     * 解析 JWT Token，获取 Claims (负载)
     * @param token JWT 字符串
     * @return Claims 对象
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中获取用户标识 (subject)
     */
    public String getSubjectFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }


}
