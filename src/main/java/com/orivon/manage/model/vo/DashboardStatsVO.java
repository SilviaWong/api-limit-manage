package com.orivon.manage.model.vo;

import java.util.List;
import java.util.Map;

public class DashboardStatsVO {
    private long apiCount;
    private long unitCount;
    private long todayCallCount;
    private long todayErrorCount;
    private List<Map<String, Object>> trendData; // e.g. [{"date": "2026-04-15", "count": 120}]
    private List<Map<String, Object>> topApis; 
    private List<Map<String, Object>> errorDistribution;

    public long getApiCount() { return apiCount; }
    public void setApiCount(long apiCount) { this.apiCount = apiCount; }

    public long getUnitCount() { return unitCount; }
    public void setUnitCount(long unitCount) { this.unitCount = unitCount; }

    public long getTodayCallCount() { return todayCallCount; }
    public void setTodayCallCount(long todayCallCount) { this.todayCallCount = todayCallCount; }

    public long getTodayErrorCount() { return todayErrorCount; }
    public void setTodayErrorCount(long todayErrorCount) { this.todayErrorCount = todayErrorCount; }

    public List<Map<String, Object>> getTrendData() { return trendData; }
    public void setTrendData(List<Map<String, Object>> trendData) { this.trendData = trendData; }

    public List<Map<String, Object>> getTopApis() { return topApis; }
    public void setTopApis(List<Map<String, Object>> topApis) { this.topApis = topApis; }

    public List<Map<String, Object>> getErrorDistribution() { return errorDistribution; }
    public void setErrorDistribution(List<Map<String, Object>> errorDistribution) { this.errorDistribution = errorDistribution; }
}
