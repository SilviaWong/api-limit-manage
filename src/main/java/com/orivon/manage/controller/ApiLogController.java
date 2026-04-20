package com.orivon.manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.orivon.manage.common.Result;
import com.orivon.manage.model.entity.ApiLog;
import com.orivon.manage.service.ApiLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/log")
public class ApiLogController {

    @Autowired
    private ApiLogService apiLogService;

    /**
     * 分页查询接口调用日志
     */
    @GetMapping("/list")
    public Result<Page<ApiLog>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer apiId,
            @RequestParam(required = false) String uKey,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String errorCode) {
        
        Page<ApiLog> page = new Page<>(current, size);
        LambdaQueryWrapper<ApiLog> queryWrapper = new LambdaQueryWrapper<>();
        
        if (apiId != null) {
            queryWrapper.eq(ApiLog::getApiId, apiId);
        }
        if (StringUtils.hasText(uKey)) {
            queryWrapper.eq(ApiLog::getUKey, uKey);
        }
        if (StringUtils.hasText(ip)) {
            queryWrapper.like(ApiLog::getIp, ip);
        }
        if (StringUtils.hasText(errorCode)) {
            queryWrapper.eq(ApiLog::getErrorCode, errorCode);
        }
        
        // 按照创建时间降序排序
        queryWrapper.orderByDesc(ApiLog::getCreateTime);

        Page<ApiLog> resultPage = apiLogService.page(page, queryWrapper);
        return Result.success(resultPage);
    }
}
