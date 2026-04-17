package com.orivon.manage.limit;

/**
 * @description: 限流策略统一接口，所有限流算法需实现此接口
 * @author: yuowu
 * @create: 2026-04-15
 **/
public interface RateLimitStrategy {

    /**
     * 校验是否允许本次请求通过
     * @param uKey  授权标识
     * @param apiId 接口ID
     * @param limit 上限次数（含义因策略而异：固定窗口=总次数，令牌桶=每秒令牌数，等等）
     * @return true=放行，false=拒绝
     */
    boolean tryAcquire(String uKey, Integer apiId, Integer limit);
}
