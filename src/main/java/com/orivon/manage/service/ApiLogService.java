package com.orivon.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.orivon.manage.model.entity.ApiLog;

public interface ApiLogService extends IService<ApiLog> {
    /**
     * 异步记录审计日志
     */
    void logAsync(ApiLog apiLog);

    /**
     * 统一构建并异步记录审计日志
     * 拦截器和控制器均通过此方法记录，避免日志构建逻辑分散
     *
     * @param apiId         接口 ID (可为 null，例如凭证缺失时尚未解析出 apiId)
     * @param authId        授权 ID (可为 null)
     * @param uKey          调用方凭证
     * @param ip            调用方 IP
     * @param requestParams 请求参数 (JSON 字符串)
     * @param errorCode     错误码
     * @param resultSummary 结果摘要
     * @param startTime     请求开始时间戳 (用于计算耗时)
     */
    void recordLog(Integer apiId, Integer authId, String uKey, String ip,
                   String requestParams, String errorCode, String resultSummary, long startTime);
}
