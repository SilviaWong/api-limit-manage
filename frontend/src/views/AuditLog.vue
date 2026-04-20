<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getApiLogList } from '@/api/log'
import type { ApiLog, ApiLogQuery } from '@/types'
import { Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const logList = ref<ApiLog[]>([])
const total = ref(0)

const queryParams = reactive<ApiLogQuery>({
  current: 1,
  size: 10,
  apiId: undefined,
  uKey: '',
  ip: '',
  errorCode: ''
})

const fetchLogs = async () => {
  loading.value = true
  try {
    const res = await getApiLogList(queryParams)
    logList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('Failed to fetch logs:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.current = 1
  fetchLogs()
}

const handleReset = () => {
  queryParams.apiId = undefined
  queryParams.uKey = ''
  queryParams.ip = ''
  queryParams.errorCode = ''
  handleSearch()
}

const handleSizeChange = (val: number) => {
  queryParams.size = val
  fetchLogs()
}

const handleCurrentChange = (val: number) => {
  queryParams.current = val
  fetchLogs()
}

// 格式化时间
const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString()
}

onMounted(() => {
  fetchLogs()
})
</script>

<template>
  <div class="audit-log-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams" class="form-inline">
        <el-form-item label="接口 ID">
          <el-input v-model.number="queryParams.apiId" placeholder="请输入接口 ID" clearable />
        </el-form-item>
        <el-form-item label="调用方凭证(uKey)">
          <el-input v-model="queryParams.uKey" placeholder="请输入 uKey" clearable />
        </el-form-item>
        <el-form-item label="IP 地址">
          <el-input v-model="queryParams.ip" placeholder="请输入 IP" clearable />
        </el-form-item>
        <el-form-item label="状态码">
          <el-input v-model="queryParams.errorCode" placeholder="200 等" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="logList" border style="width: 100%">
        <el-table-column prop="id" label="日志 ID" width="100" align="center" />
        <el-table-column prop="apiId" label="API ID" width="100" align="center" />
        <el-table-column prop="uKey" label="调用者凭证(uKey)" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP 地址" width="140" align="center" />
        
        <el-table-column prop="errorCode" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.errorCode === '200' ? 'success' : 'danger'">
              {{ scope.row.errorCode }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="costTime" label="耗时(ms)" width="100" align="center">
          <template #default="scope">
            <span :style="{ color: scope.row.costTime > 1000 ? '#F56C6C' : '' }">
              {{ scope.row.costTime }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column label="调用时间" width="180" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <!-- 详情展开列 (可以展示具体请求参数和结果摘要) -->
        <el-table-column type="expand">
          <template #default="props">
            <div class="expand-content">
              <p><strong>请求参数：</strong> {{ props.row.requestParams }}</p>
              <p><strong>结果摘要：</strong> {{ props.row.resultSummary }}</p>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.audit-log-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.search-card {
  margin-bottom: 0px;
}
.form-inline .el-form-item {
  margin-bottom: 0;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.expand-content {
  padding: 10px 40px;
  background-color: #f8f9fa;
  border-radius: 4px;
}
.expand-content p {
  margin: 8px 0;
  font-size: 13px;
  color: #606266;
}
</style>
