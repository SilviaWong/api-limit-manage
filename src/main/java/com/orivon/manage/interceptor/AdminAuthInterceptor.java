package com.orivon.manage.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orivon.manage.common.Result;
import com.orivon.manage.common.ResultCode;
import com.orivon.manage.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = JwtUtil.validateToken(token);
            if (username != null) {
                // 验证通过
                request.setAttribute("ADMIN_USERNAME", username);
                return true;
            }
        }

        // 验证失败，返回 401
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(ResultCode.UNAUTHORIZED, "登录已过期或未登录，请重新登录")));
        return false;
    }
}
