package com.orivon.manage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orivon.manage.common.Result;
import com.orivon.manage.model.entity.ApiLog;
import com.orivon.manage.model.vo.DashboardStatsVO;
import com.orivon.manage.service.ApiInfoService;
import com.orivon.manage.service.ApiLogService;
import com.orivon.manage.service.UnitInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ApiInfoService apiInfoService;
    @Autowired
    private UnitInfoService unitInfoService;
    @Autowired
    private ApiLogService apiLogService;

    @GetMapping("/stats")
    public Result<DashboardStatsVO> getStats() {
        DashboardStatsVO stats = new DashboardStatsVO();
        
        // 1. Total APIs
        stats.setApiCount(apiInfoService.count());
        
        // 2. Total Units
        stats.setUnitCount(unitInfoService.count());

        // 3. Today's logs and errors
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        
        LambdaQueryWrapper<ApiLog> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(ApiLog::getCreateTime, startOfToday);
        stats.setTodayCallCount(apiLogService.count(todayWrapper));

        LambdaQueryWrapper<ApiLog> errorWrapper = new LambdaQueryWrapper<>();
        errorWrapper.ge(ApiLog::getCreateTime, startOfToday)
                    .ne(ApiLog::getErrorCode, "200");
        stats.setTodayErrorCount(apiLogService.count(errorWrapper));

        // 4. Trend Data for the last 7 days (including today)
        List<Map<String, Object>> trendData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);
            
            LambdaQueryWrapper<ApiLog> dayWrapper = new LambdaQueryWrapper<>();
            dayWrapper.ge(ApiLog::getCreateTime, startOfDay)
                      .le(ApiLog::getCreateTime, endOfDay);
            
            long count = apiLogService.count(dayWrapper);
            
            Map<String, Object> point = new HashMap<>();
            point.put("date", date.format(formatter));
            point.put("count", count);
            trendData.add(point);
        }
        stats.setTrendData(trendData);

        // 5. Top 5 API Usage (Last 7 days)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LambdaQueryWrapper<ApiLog> topApiWrapper = new LambdaQueryWrapper<>();
        topApiWrapper.ge(ApiLog::getCreateTime, sevenDaysAgo)
                     .select(ApiLog::getApiId)
                     .groupBy(ApiLog::getApiId)
                     .orderByDesc(ApiLog::getApiId); // mybatis-plus limited groupBy support, so we will fetch all recent logs and aggregate in memory to be safe across DB versions

        List<ApiLog> recentLogs = apiLogService.list(new LambdaQueryWrapper<ApiLog>().ge(ApiLog::getCreateTime, sevenDaysAgo));
        
        Map<Integer, Long> apiCountMap = new HashMap<>();
        Map<String, Long> errorCountMap = new HashMap<>();
        
        for (ApiLog log : recentLogs) {
            // Aggregate Top APIs
            apiCountMap.put(log.getApiId(), apiCountMap.getOrDefault(log.getApiId(), 0L) + 1);
            
            // Aggregate Errors
            if (!"200".equals(log.getErrorCode())) {
                errorCountMap.put(log.getErrorCode(), errorCountMap.getOrDefault(log.getErrorCode(), 0L) + 1);
            }
        }
        
        // Process Top APIs
        List<Map<String, Object>> topApis = new ArrayList<>();
        apiCountMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .forEach(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    String apiName = "未知接口(" + entry.getKey() + ")";
                    try {
                        com.orivon.manage.model.entity.ApiInfo info = apiInfoService.getById(entry.getKey());
                        if (info != null) apiName = info.getName();
                    } catch (Exception e) {}
                    map.put("name", apiName);
                    map.put("value", entry.getValue());
                    topApis.add(map);
                });
        stats.setTopApis(topApis);
        
        // Process Error Distribution
        List<Map<String, Object>> errorDistribution = new ArrayList<>();
        errorCountMap.forEach((code, count) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "错误: " + code);
            map.put("value", count);
            errorDistribution.add(map);
        });
        stats.setErrorDistribution(errorDistribution);

        return Result.success(stats);
    }
}
