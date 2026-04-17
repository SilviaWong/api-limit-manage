package com.orivon.manage.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orivon.manage.common.Result;
import com.orivon.manage.common.ResultCode;
import com.orivon.manage.model.entity.ApiAuth;
import com.orivon.manage.model.entity.ApiInfo;
import com.orivon.manage.service.ApiAuthService;
import com.orivon.manage.service.ApiInfoService;
import com.orivon.manage.service.ApiLogService;
import com.orivon.manage.limit.RateLimitStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * @description: API 代理拦截器：负责鉴权、限流、白名单校验
 * @author: yuowu
 * @create: 2026-04-03 22:40
 **/
@Component
public class ProxyInterceptor implements HandlerInterceptor {

    @Autowired
    private ApiAuthService apiAuthService;

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired
    private ApiLogService apiLogService;

    @Autowired
    private RateLimitStrategy rateLimitStrategy;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();

        // 1. 获取调用凭证
        String uKey = request.getHeader("uKey");
        String mKey = request.getHeader("mKey");
        String apiIdStr = request.getHeader("apiId"); // 暂时通过 Header 传 API ID，后续可改为路由解析
        String clientIp = getClientIp(request);

        if (!StringUtils.hasText(uKey) || !StringUtils.hasText(mKey) || !StringUtils.hasText(apiIdStr)) {
            apiLogService.recordLog(null, null, uKey, clientIp, "{}",
                    String.valueOf(ResultCode.PROXY_MISSING_CREDENTIALS.getCode()), ResultCode.PROXY_MISSING_CREDENTIALS.getMessage(), startTime);
            return fail(response, ResultCode.PROXY_MISSING_CREDENTIALS, ResultCode.PROXY_MISSING_CREDENTIALS.getMessage());
        }

        Integer apiId = Integer.parseInt(apiIdStr);

        // 2. 校验授权状态 (uKey + mKey + apiId)
        ApiAuth auth = apiAuthService.getOne(new LambdaQueryWrapper<ApiAuth>()
                .eq(ApiAuth::getUKey, uKey)
                .eq(ApiAuth::getMKey, mKey)
                .eq(ApiAuth::getApiId, apiId)
                .eq(ApiAuth::getStatus, 1));

        if (auth == null) {
            apiLogService.recordLog(apiId, null, uKey, clientIp, "{}",
                    String.valueOf(ResultCode.PROXY_AUTH_INVALID.getCode()), ResultCode.PROXY_AUTH_INVALID.getMessage(), startTime);
            return fail(response, ResultCode.PROXY_AUTH_INVALID, ResultCode.PROXY_AUTH_INVALID.getMessage());
        }

        // 3. 白名单校验 (如果配置了白名单)
        if (StringUtils.hasText(auth.getWhiteList())) {
            if (!auth.getWhiteList().contains(clientIp)) {
                apiLogService.recordLog(apiId, auth.getId(), uKey, clientIp, "{}",
                        String.valueOf(ResultCode.PROXY_IP_FORBIDDEN.getCode()), ResultCode.PROXY_IP_FORBIDDEN.getMessage(), startTime);
                return fail(response, ResultCode.PROXY_IP_FORBIDDEN, ResultCode.PROXY_IP_FORBIDDEN.getMessage() + ": " + clientIp);
            }
        }

        // 4. 限流校验
        ApiInfo apiInfo = apiInfoService.getById(apiId);
        if (apiInfo == null || apiInfo.getStatus() == 0) {
            apiLogService.recordLog(apiId, auth.getId(), uKey, clientIp, "{}",
                    String.valueOf(ResultCode.PROXY_API_OFFLINE.getCode()), ResultCode.PROXY_API_OFFLINE.getMessage(), startTime);
            return fail(response, ResultCode.PROXY_API_OFFLINE, ResultCode.PROXY_API_OFFLINE.getMessage());
        }

        if (!rateLimitStrategy.tryAcquire(uKey, apiId, apiInfo.getRequestLimit())) {
            apiLogService.recordLog(apiId, auth.getId(), uKey, clientIp, "{}",
                    String.valueOf(ResultCode.PROXY_LIMIT_EXCEEDED.getCode()), ResultCode.PROXY_LIMIT_EXCEEDED.getMessage(), startTime);
            return fail(response, ResultCode.PROXY_LIMIT_EXCEEDED, ResultCode.PROXY_LIMIT_EXCEEDED.getMessage() + ": " + apiInfo.getRequestLimit());
        }

        // 校验通过，存入 request 供后续转发使用
        request.setAttribute("API_AUTH_INFO", auth);
        request.setAttribute("API_INFO", apiInfo);
        request.setAttribute("PROXY_START_TIME", startTime);
        
        return true;
    }


    /**
     * 向调用方写入 JSON 格式的错误响应并中断拦截器链
     * 注意: HTTP 状态码固定返回 200，业务错误通过 Result 的 code 字段区分，
     * 这样做是为了让前端 / 调用方能统一按 JSON 解析响应体，不会因 HTTP 非 200 而走异常分支。
     *
     * @param response   HTTP 响应对象
     * @param resultCode 业务错误码枚举
     * @param msg        返回给调用方的具体错误描述
     * @return 始终返回 false，表示拦截器不放行该请求
     */
    private boolean fail(HttpServletResponse response, ResultCode resultCode, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(resultCode, msg)));
        return false;
    }

    /**
     * 获取调用方的真实 IP 地址
     * 优先从 X-Forwarded-For 请求头中读取（适用于经过 Nginx 等反向代理的场景），
     * 若该请求头不存在或值为 "unknown"，则回退到 request.getRemoteAddr() 直接获取。
     *
     * @param request HTTP 请求对象
     * @return 调用方的 IP 地址字符串
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
