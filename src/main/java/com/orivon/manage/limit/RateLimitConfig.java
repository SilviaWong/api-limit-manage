package com.orivon.manage.limit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @description: 限流策略配置类
 * 根据 application.yml 中的 rate-limit.strategy 配置项，
 * 自动选择对应的限流策略实现，并注入为 Spring Bean。
 *
 * 可选值：
 * - fixed-window    固定窗口计数器（默认）
 * - sliding-window  滑动窗口计数器
 * - leaky-bucket    漏桶算法
 * - token-bucket    令牌桶算法
 * - lua-script      Redis + Lua 原子性限流
 *
 * @author: yuowu
 * @create: 2026-04-15
 **/
@Configuration
public class RateLimitConfig {

    @Value("${rate-limit.strategy:fixed-window}")
    private String strategy;

    /**
     * Spring 会自动将所有 RateLimitStrategy 实现注入到这个 Map 中，
     * key 是 @Component 注解中的 Bean 名称，value 是实例。
     */
    @Autowired
    private Map<String, RateLimitStrategy> strategyMap;

    /**
     * 根据配置选择当前生效的限流策略
     */
    @Bean
    @org.springframework.context.annotation.Primary
    public RateLimitStrategy currentRateLimitStrategy() {
        String beanName = resolveBeanName(strategy);

        RateLimitStrategy selected = strategyMap.get(beanName);
        if (selected == null) {
            System.err.println("[限流配置] 未找到策略: " + strategy + "，回退到默认固定窗口策略");
            selected = strategyMap.get("fixedWindowLimiter");
        }

        System.out.println("[限流配置] 当前使用的限流策略: " + strategy + " -> " + selected.getClass().getSimpleName());
        return selected;
    }

    /**
     * 将配置值映射为 Bean 名称
     */
    private String resolveBeanName(String strategyName) {
        switch (strategyName) {
            case "fixed-window":   return "fixedWindowLimiter";
            case "sliding-window": return "slidingWindowLimiter";
            case "leaky-bucket":   return "leakyBucketLimiter";
            case "token-bucket":   return "tokenBucketLimiter";
            case "lua-script":     return "luaScriptLimiter";
            default:               return "fixedWindowLimiter";
        }
    }
}
