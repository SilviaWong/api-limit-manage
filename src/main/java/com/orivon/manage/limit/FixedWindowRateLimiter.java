package com.orivon.manage.limit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description: 固定窗口计数器限流策略
 * 在一个固定的时间窗口（24小时）内维护计数器，达到阈值则拒绝。
 * 
 * 优点：实现最简单，适合日级粒度限流
 * 缺点：存在临界突发问题（窗口交界处可能瞬间承受2倍流量）
 *
 * @author: yuowu
 * @create: 2026-04-15
 **/
@Component("fixedWindowLimiter")
public class FixedWindowRateLimiter implements RateLimitStrategy {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "rate_limit:fixed:";

    @Override
    public boolean tryAcquire(String uKey, Integer apiId, Integer limit) {
        if (limit == null || limit <= 0) {
            return true;
        }

        String key = KEY_PREFIX + uKey + ":" + apiId;
        String currentCountStr = redisTemplate.opsForValue().get(key);
        int currentCount = currentCountStr == null ? 0 : Integer.parseInt(currentCountStr);

        if (currentCount >= limit) {
            return false;
        }

        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }

        return true;
    }
}
