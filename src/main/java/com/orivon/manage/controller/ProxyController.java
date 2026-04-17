package com.orivon.manage.controller;

import com.orivon.manage.common.Result;
import com.orivon.manage.common.ResultCode;
import com.orivon.manage.model.entity.ApiAuth;
import com.orivon.manage.model.entity.ApiInfo;
import com.orivon.manage.service.ApiLogService;
import com.orivon.manage.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @description: 接口代理入口控制器
 * @author: yuowu
 * @create: 2026-04-03 23:20
 **/
@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    @Autowired
    private ProxyService proxyService;

    @Autowired
    private ApiLogService apiLogService;

    /**
     * 统一代理入口 (POST)
     * 这里的具体请求由拦截器前置处理鉴权
     */
    @PostMapping("/**")
    public Result<Object> forward(HttpServletRequest request, @RequestBody(required = false) Map<String, Object> businessParams) {
        // 复用拦截器阶段记录的请求起始时间，使耗时计算覆盖完整链路
        Long startTime = (Long) request.getAttribute("PROXY_START_TIME");
        if (startTime == null) {
            startTime = System.currentTimeMillis();
        }

        // 1. 获取拦截器存入的元数据
        ApiAuth auth = (ApiAuth) request.getAttribute("API_AUTH_INFO");
        ApiInfo info = (ApiInfo) request.getAttribute("API_INFO");

        if (auth == null || info == null) {
            return Result.error(ResultCode.PROXY_METADATA_LOST);
        }

        // 2. 执行核心转发逻辑
        Object response = null;
        String errorCode = String.valueOf(ResultCode.SUCCESS.getCode());
        String summary = "请求成功";

        try {
            response = proxyService.doForward(info, auth, businessParams);
            if (response != null && response.toString().startsWith("调用市级接口异常")) {
                errorCode = String.valueOf(ResultCode.PROXY_UPSTREAM_ERROR.getCode());
                summary = response.toString();
            }
        } catch (Exception e) {
            errorCode = String.valueOf(ResultCode.INTERNAL_SERVER_ERROR.getCode());
            summary = "系统内部错误: " + e.getMessage();
        }

        // 3. 记录异步审计日志（统一调用 ApiLogService）
        String params = businessParams != null ? businessParams.toString() : "{}";
        apiLogService.recordLog(info.getId(), auth.getId(), auth.getUKey(), getClientIp(request),
                params, errorCode, summary, startTime);

        // 4. 封装返回
        return String.valueOf(ResultCode.SUCCESS.getCode()).equals(errorCode) ? Result.success(summary, response) : Result.error(Integer.parseInt(errorCode), summary);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
