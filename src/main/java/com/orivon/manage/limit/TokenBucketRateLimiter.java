package com.orivon.manage.limit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description: 令牌桶算法限流策略
 * 系统以固定速率向桶中放入令牌，每次请求取走一个令牌。
 * 桶满则不再放入，桶空则拒绝请求。
 *
 * 优点：允许合理的突发流量（桶中攒的令牌可以瞬间被消耗），弹性最好
 * 缺点：单机方案简单，分布式需借助 Redis 实现
 *
 * @author: yuowu
 * @create: 2026-04-15
 **/
@Component("tokenBucketLimiter")
public class TokenBucketRateLimiter implements RateLimitStrategy {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TOKEN_KEY_PREFIX = "rate_limit:token:count:";
    private static final String TIME_KEY_PREFIX = "rate_limit:token:time:";

    /** 令牌生成速率：每秒生成的令牌数 */
    private static final double REFILL_RATE = 10.0;

    @Override
    public boolean tryAcquire(String uKey, Integer apiId, Integer limit) {
        if (limit == null || limit <= 0) {
            return true;
        }

        // limit 在此策略中作为令牌桶容量（最大令牌数）
        int capacity = limit;

        String tokenKey = TOKEN_KEY_PREFIX + uKey + ":" + apiId;
        String timeKey = TIME_KEY_PREFIX + uKey + ":" + apiId;

        long now = System.currentTimeMillis();

        // 获取当前令牌数和上次补充时间
        String tokenStr = redisTemplate.opsForValue().get(tokenKey);
        String timeStr = redisTemplate.opsForValue().get(timeKey);

        double currentTokens = tokenStr == null ? capacity : Double.parseDouble(tokenStr);
        long lastRefillTime = timeStr == null ? now : Long.parseLong(timeStr);

        // 计算这段时间内应该补充多少令牌
        long elapsed = now - lastRefillTime;
        double newTokens = (elapsed / 1000.0) * REFILL_RATE;
        currentTokens = Math.min(capacity, currentTokens + newTokens);

        // 更新补充时间
        redisTemplate.opsForValue().set(timeKey, String.valueOf(now), 120, TimeUnit.SECONDS);

        if (currentTokens >= 1) {
            // 有令牌可用，消耗一个
            currentTokens -= 1;
            redisTemplate.opsForValue().set(tokenKey, String.valueOf(currentTokens), 120, TimeUnit.SECONDS);
            return true;
        }

        // 无令牌可用，拒绝
        redisTemplate.opsForValue().set(tokenKey, String.valueOf(currentTokens), 120, TimeUnit.SECONDS);
        return false;
    }
}
