<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { getDashboardStats, type DashboardStats } from '@/api/dashboard'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const stats = ref<DashboardStats>({
  apiCount: 0,
  unitCount: 0,
  todayCallCount: 0,
  todayErrorCount: 0,
  trendData: [],
  topApis: [],
  errorDistribution: []
})

const trendChartRef = ref<HTMLElement>()
const topApiChartRef = ref<HTMLElement>()
const errorChartRef = ref<HTMLElement>()

let trendChartInstance: echarts.ECharts | null = null
let topApiChartInstance: echarts.ECharts | null = null
let errorChartInstance: echarts.ECharts | null = null

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDashboardStats()
    stats.value = res as DashboardStats
    initCharts()
  } catch (error) {
    ElMessage.error('获取大盘数据失败')
  } finally {
    loading.value = false
  }
}

const initCharts = () => {
  initTrendChart()
  initTopApiChart()
  initErrorChart()
}

const initTrendChart = () => {
  if (!trendChartRef.value) return
  if (!trendChartInstance) {
    trendChartInstance = echarts.init(trendChartRef.value)
  }

  const dates = stats.value.trendData.map(item => item.date)
  const counts = stats.value.trendData.map(item => item.count)

  const option = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: dates },
    yAxis: { type: 'value', name: '调用次数' },
    series: [
      {
        name: '总调用量',
        type: 'line',
        smooth: true,
        data: counts,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.5)' },
            { offset: 1, color: 'rgba(64,158,255,0.05)' }
          ])
        },
        itemStyle: { color: '#409EFF' }
      }
    ]
  }
  trendChartInstance.setOption(option)
}

const initTopApiChart = () => {
  if (!topApiChartRef.value) return
  if (!topApiChartInstance) {
    topApiChartInstance = echarts.init(topApiChartRef.value)
  }

  // ECharts bar chart usually looks better sorted ascending when displayed horizontally
  const data = [...stats.value.topApis].reverse()
  const names = data.map(item => item.name)
  const values = data.map(item => item.value)

  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', name: '调用量' },
    yAxis: { type: 'category', data: names, axisLabel: { width: 100, overflow: 'truncate' } },
    series: [
      {
        name: '调用量',
        type: 'bar',
        data: values,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
            { offset: 0, color: '#67C23A' },
            { offset: 1, color: 'rgba(103,194,58,0.3)' }
          ]),
          borderRadius: [0, 4, 4, 0]
        }
      }
    ]
  }
  topApiChartInstance.setOption(option)
}

const initErrorChart = () => {
  if (!errorChartRef.value) return
  if (!errorChartInstance) {
    errorChartInstance = echarts.init(errorChartRef.value)
  }

  const option = {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [
      {
        name: '异常分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: 16, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: stats.value.errorDistribution.length > 0 ? stats.value.errorDistribution : [{ name: '无异常', value: 0 }]
      }
    ]
  }
  errorChartInstance.setOption(option)
}

const handleResize = () => {
  trendChartInstance?.resize()
  topApiChartInstance?.resize()
  errorChartInstance?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChartInstance?.dispose()
  topApiChartInstance?.dispose()
  errorChartInstance?.dispose()
})
</script>

<template>
  <div class="dashboard" v-loading="loading">
    <el-row :gutter="20" class="data-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-header">总接口数</div>
          <div class="card-value">{{ stats.apiCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-header">接入租户</div>
          <div class="card-value">{{ stats.unitCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-header">今日调用量</div>
          <div class="card-value" style="color: #67C23A;">{{ stats.todayCallCount.toLocaleString() }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-header">错误警告</div>
          <div class="card-value" style="color: #F56C6C;">{{ stats.todayErrorCount.toLocaleString() }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 近 7 天趋势图 -->
    <div style="margin-top: 20px;">
      <el-card shadow="never">
        <template #header>
          <div class="card-header">近 7 天调用趋势</div>
        </template>
        <div ref="trendChartRef" style="height: 350px; width: 100%;"></div>
      </el-card>
    </div>

    <!-- 代理热点与异常分布 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">热点接口 TOP 5 (近7天)</div>
          </template>
          <div ref="topApiChartRef" style="height: 300px; width: 100%;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">异常状态分布 (近7天)</div>
          </template>
          <div ref="errorChartRef" style="height: 300px; width: 100%;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.data-cards .el-card {
  border: none;
}
.card-header {
  font-size: 14px;
  color: #909399;
}
.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-top: 10px;
}
</style>
