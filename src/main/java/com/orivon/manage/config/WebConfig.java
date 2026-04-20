package com.orivon.manage.config;

import com.orivon.manage.interceptor.ProxyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.orivon.manage.interceptor.AdminAuthInterceptor;  
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Lazy; 

/**
 * @description: Web 相关配置
 * @author: yuowu
 * @create: 2026-04-03 22:45
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    @Lazy
    private ProxyInterceptor proxyInterceptor;

    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 注册网关代理拦截器，仅拦截代理路径
        registry.addInterceptor(proxyInterceptor)
                .addPathPatterns("/api/proxy/**");
                
        // 2. 注册后台管理鉴权拦截器，拦截除了代理和登录外的所有后台接口
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/**", "/auth/**", "/unit/**")
                .excludePathPatterns("/api/proxy/**", "/sys/login");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
