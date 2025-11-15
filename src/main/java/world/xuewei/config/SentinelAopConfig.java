package world.xuewei.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 专门用于加载 Sentinel AOP 切面的配置类
 */
@Configuration
public class SentinelAopConfig {

    /**
     * 将 SentinelResourceAspect 注册为 Bean
     * 这是确保 @SentinelResource 生效的关键
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}