package com.orivon.manage.config;

import com.orivon.manage.interceptor.ProxyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: Web 相关配置
 * @author: yuowu
 * @create: 2026-04-03 22:45
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ProxyInterceptor proxyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册代理拦截器，仅拦截代理路径
        registry.addInterceptor(proxyInterceptor)
                .addPathPatterns("/api/proxy/**");
    }
}
