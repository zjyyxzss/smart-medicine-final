package world.xuewei.config;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import world.xuewei.utils.JwtUtil;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从请求头获取 Token
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Token 必须以 "Bearer " 开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // 截取 Token 字符串
            try {
                username = jwtUtil.getSubjectFromToken(jwt); // 从 Token 中获取用户ID (Subject)
            } catch (ExpiredJwtException e) {
                // Token 过期
                logger.warn("JWT Token is expired.");
            } catch (MalformedJwtException e) {
                // Token 格式错误
                logger.warn("JWT Token is malformed.");
            }
        }

        // 2. 校验 Token 并设置认证信息
        // 确保 SecurityContextHolder 中还没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 简单认证：默认赋予 USER 角色
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

            // 设置认证详情
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 将认证信息放入安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 继续执行过滤链
        filterChain.doFilter(request, response);
    }
}