package com.orivon.manage.limit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @description: 基于 Redis + Lua 脚本的原子性限流策略（固定窗口）
 * 将"读取计数 → 判断是否超限 → 自增计数 → 设置过期"打包到一个 Lua 脚本中，
 * 由 Redis 单线程执行，保证整个操作的原子性，避免并发竞态条件。
 *
 * 优点：原子操作，无并发问题，是生产环境分布式限流的推荐方案
 * 缺点：需要编写和维护 Lua 脚本
 *
 * @author: yuowu
 * @create: 2026-04-15
 **/
@Component("luaScriptLimiter")
public class LuaScriptRateLimiter implements RateLimitStrategy {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "rate_limit:lua:";

    /**
     * Lua 脚本逻辑：
     * 1. 读取当前计数
     * 2. 如果已达上限，返回 0（拒绝）
     * 3. 否则自增计数，若是第一次则设置过期时间
     * 4. 返回 1（放行）
     */
    private static final String LUA_SCRIPT =
            "local key = KEYS[1] " +
            "local limit = tonumber(ARGV[1]) " +
            "local expireTime = tonumber(ARGV[2]) " +
            "local current = tonumber(redis.call('GET', key) or '0') " +
            "if current >= limit then " +
            "    return 0 " +
            "end " +
            "current = redis.call('INCR', key) " +
            "if current == 1 then " +
            "    redis.call('EXPIRE', key, expireTime) " +
            "end " +
            "return 1";

    /** 窗口过期时间：60 秒 */
    private static final int EXPIRE_SECONDS = 60;

    @Override
    public boolean tryAcquire(String uKey, Integer apiId, Integer limit) {
        if (limit == null || limit <= 0) {
            return true;
        }

        String key = KEY_PREFIX + uKey + ":" + apiId;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(EXPIRE_SECONDS)
        );

        return result != null && result == 1;
    }
}
