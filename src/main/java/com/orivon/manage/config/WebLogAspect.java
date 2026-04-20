package com.orivon.manage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * @description: 统一的Web层日志监控切面
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 定义切点，拦截所有 controller 包下的 public 方法，排除 ProxyController
    @Pointcut("execution(public * com.orivon.manage.controller.*.*(..)) && !execution(public * com.orivon.manage.controller.ProxyController.*(..))")
    public void webLog() {}

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String url = request != null ? request.getRequestURL().toString() : "unknown";
        String httpMethod = request != null ? request.getMethod() : "unknown";
        String ip = request != null ? getClientIp(request) : "unknown";
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        log.info("================== Request Start ==================");
        log.info("URL          : {}", url);
        log.info("HTTP Method  : {}", httpMethod);
        log.info("IP           : {}", ip);
        log.info("Class Method : {}", classMethod);

        try {
            log.info("Request Args : {}", objectMapper.writeValueAsString(joinPoint.getArgs()));
        } catch (Exception e) {
             log.info("Request Args : {}", Arrays.toString(joinPoint.getArgs()));
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception    : {}", e.getMessage(), e);
            throw e;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            log.info("Cost Time    : {} ms", costTime);
            log.info("================== Request End ====================");
        }

        try {
            log.info("Response     : {}", objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.info("Response     : {}", result);
        }

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
