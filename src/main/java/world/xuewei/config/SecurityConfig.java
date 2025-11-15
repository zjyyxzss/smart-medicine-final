package world.xuewei.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 开启 Spring Security
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 定义安全过滤链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. 关闭 CSRF 保护
        http.csrf(AbstractHttpConfigurer::disable);

        // 2. 配置会话管理为“无状态” s)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 3. 配置 URL 授权规则
        http.authorizeHttpRequests(authz -> authz
                // --- 白名单：放行以下路径，无需认证 ---
                .requestMatchers(
                        "/login",             // 登录页面
                        "/login/login",    // 登录接口
                        "/login/register", // 注册接口
                        "/medicine/search-es",// (临时) 放行我们的 ES 搜索接口
                        "/medicine/sync-es"   // (临时) 放行 ES 同步接口

                ).permitAll()

                // --- 保护名单：除了白名单外，其他所有请求都需要认证 ---
                .anyRequest().authenticated()
        );

        return http.build();
    }
}