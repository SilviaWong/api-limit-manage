package com.orivon.manage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orivon.manage.mapper.ApiLogMapper;
import com.orivon.manage.model.entity.ApiLog;
import com.orivon.manage.service.ApiLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApiLogServiceImpl extends ServiceImpl<ApiLogMapper, ApiLog> implements ApiLogService {

    @Async
    @Override
    public void logAsync(ApiLog apiLog) {
        // 执行入库操作，此处为异步调用
        this.save(apiLog);
        
        // 如果错误码不为 200，记录一条警告日志到控制台/文件
        if (!"200".equals(apiLog.getErrorCode())) {
            System.err.println("[预警] 接口调用异常: ID=" + apiLog.getApiId() + ", 错误码=" + apiLog.getErrorCode() + ", 摘要=" + apiLog.getResultSummary());
        }
    }

    @Override
    public void recordLog(Integer apiId, Integer authId, String uKey, String ip,
                          String requestParams, String errorCode, String resultSummary, long startTime) {
        ApiLog apiLog = new ApiLog();
        apiLog.setApiId(apiId);
        apiLog.setAuthId(authId);
        apiLog.setUKey(uKey);
        apiLog.setIp(ip);
        apiLog.setRequestParams(requestParams != null ? requestParams : "{}");
        apiLog.setErrorCode(errorCode);
        apiLog.setResultSummary(resultSummary);
        apiLog.setCostTime(System.currentTimeMillis() - startTime);
        apiLog.setCreateTime(new Date());
        logAsync(apiLog);
    }
}
