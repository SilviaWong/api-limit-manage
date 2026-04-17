package com.orivon.manage.limit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

/**
 * @description: 滑动窗口计数器限流策略
 * 使用 Redis Sorted Set，以时间戳为 score，滑动统计窗口内的请求数量。
 * 
 * 优点：解决了固定窗口的临界突发问题，限流更精确
 * 缺点：每个请求都会在 Redis 中存一条记录，内存开销较大
 *
 * @author: yuowu
 * @create: 2026-04-15
 **/
@Component("slidingWindowLimiter")
public class SlidingWindowRateLimiter implements RateLimitStrategy {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "rate_limit:sliding:";

    /** 滑动窗口大小：默认 60 秒 */
    private static final long WINDOW_SIZE_MS = 60 * 1000L;

    @Override
    public boolean tryAcquire(String uKey, Integer apiId, Integer limit) {
        if (limit == null || limit <= 0) {
            return true;
        }

        String key = KEY_PREFIX + uKey + ":" + apiId;
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_SIZE_MS;

        // 1. 移除窗口之外的过期记录
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        // 2. 统计当前窗口内的请求数
        Long count = redisTemplate.opsForZSet().zCard(key);
        if (count != null && count >= limit) {
            return false;
        }

        // 3. 添加当前请求记录（score=当前时间戳，value=唯一ID防重复）
        redisTemplate.opsForZSet().add(key, UUID.randomUUID().toString(), now);

        // 4. 设置 key 的过期时间为窗口大小的 2 倍，防止无限增长
        redisTemplate.expire(key, WINDOW_SIZE_MS * 2, java.util.concurrent.TimeUnit.MILLISECONDS);

        return true;
    }
}
