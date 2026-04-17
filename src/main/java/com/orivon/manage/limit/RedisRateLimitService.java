package com.orivon.manage.limit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * @description: 基于 Redis 的限流服务
 * @author: yuowu
 * @create: 2026-04-03 22:30
 **/
@Service
public class RedisRateLimitService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String LIMIT_KEY_PREFIX = "api_limit:";

    /**
     * 校验并增加调用次数
     * @param uKey 授权标识
     * @param apiId 接口ID
     * @param limit 上限次数
     * @return 是否未超限
     */
    public boolean checkAndIncrement(String uKey, Integer apiId, Integer limit) {
        if (limit == null || limit <= 0) {
            return true; // 不限流
        }

        String key = LIMIT_KEY_PREFIX + uKey + ":" + apiId;
        String currentCountStr = redisTemplate.opsForValue().get(key);
        int currentCount = currentCountStr == null ? 0 : Integer.parseInt(currentCountStr);

        if (currentCount >= limit) {
            return false;
        }

        // 增加计数
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            // 第一次调用，设置 24 小时过期
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }
        
        return true;
    }
}
