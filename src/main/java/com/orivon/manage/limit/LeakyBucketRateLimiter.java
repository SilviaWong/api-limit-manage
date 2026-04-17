package com.orivon.manage.limit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description: 漏桶算法限流策略
 * 请求以任意速率进入"桶"中，但以固定速率被处理。
 * 使用 Redis 记录上次漏水时间和当前水量，模拟漏桶行为。
 *
 * 优点：输出速率绝对均匀，能有效保护下游服务
 * 缺点：无法应对合理的突发流量，即使系统空闲也不会加速处理
 *
 * @author: yuowu
 * @create: 2026-04-15
 **/
@Component("leakyBucketLimiter")
public class LeakyBucketRateLimiter implements RateLimitStrategy {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String WATER_KEY_PREFIX = "rate_limit:leaky:water:";
    private static final String TIME_KEY_PREFIX = "rate_limit:leaky:time:";

    /** 漏水速率：每秒漏出的请求数 */
    private static final double LEAK_RATE = 10.0;

    @Override
    public boolean tryAcquire(String uKey, Integer apiId, Integer limit) {
        if (limit == null || limit <= 0) {
            return true;
        }

        // limit 在此策略中作为桶的容量（最大排队数）
        int capacity = limit;

        String waterKey = WATER_KEY_PREFIX + uKey + ":" + apiId;
        String timeKey = TIME_KEY_PREFIX + uKey + ":" + apiId;

        long now = System.currentTimeMillis();

        // 获取当前水量和上次漏水时间
        String waterStr = redisTemplate.opsForValue().get(waterKey);
        String timeStr = redisTemplate.opsForValue().get(timeKey);

        double currentWater = waterStr == null ? 0 : Double.parseDouble(waterStr);
        long lastLeakTime = timeStr == null ? now : Long.parseLong(timeStr);

        // 计算这段时间内漏掉了多少水
        long elapsed = now - lastLeakTime;
        double leaked = (elapsed / 1000.0) * LEAK_RATE;
        currentWater = Math.max(0, currentWater - leaked);

        // 更新漏水时间
        redisTemplate.opsForValue().set(timeKey, String.valueOf(now), 120, TimeUnit.SECONDS);

        if (currentWater < capacity) {
            // 桶未满，加水（放行）
            currentWater += 1;
            redisTemplate.opsForValue().set(waterKey, String.valueOf(currentWater), 120, TimeUnit.SECONDS);
            return true;
        }

        // 桶已满，拒绝
        redisTemplate.opsForValue().set(waterKey, String.valueOf(currentWater), 120, TimeUnit.SECONDS);
        return false;
    }
}
