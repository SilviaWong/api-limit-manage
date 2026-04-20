<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApiInfoList, addApiInfo, updateApiInfo, deleteApiInfo } from '@/api/api-info'
import type { ApiInfo } from '@/types'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const apiList = ref<ApiInfo[]>([])

// 获取接口列表
const loadData = async () => {
  loading.value = true
  try {
    const res = await getApiInfoList()
    // 经过拦截器解包，res 直接是数组
    apiList.value = res || []
  } catch (error) {
    ElMessage.error('获取接口列表请求失败')
  } finally {
    loading.value = false
  }
}

// 弹窗与表单状态
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const formData = ref<Partial<ApiInfo>>({
  name: '',
  directory: '',
  method: 'GET',
  url: '',
  serviceCode: '',
  requestLimit: 100,
  status: 1,
  inputDesc: '',
  outputDesc: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入接口名称', trigger: 'blur' }],
  directory: [{ required: true, message: '请输入分类目录', trigger: 'blur' }],
  method: [{ required: true, message: '请选择请求方法', trigger: 'change' }],
  url: [{ required: true, message: '请输入目标接口地址', trigger: 'blur' }],
  serviceCode: [{ required: true, message: '请输入服务编码', trigger: 'blur' }],
  requestLimit: [{ required: true, message: '请输入限流阈值', trigger: 'blur' }]
}

const handleAdd = () => {
  isEdit.value = false
  formData.value = {
    name: '',
    directory: '默认分组',
    method: 'GET',
    url: '',
    serviceCode: '',
    requestLimit: 100,
    status: 1,
    inputDesc: '',
    outputDesc: '',
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: ApiInfo) => {
  isEdit.value = true
  // 深拷贝一行数据
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row: ApiInfo) => {
  ElMessageBox.confirm('确定要删除该接口吗？相关授权记录可能会失效。', '危险操作提示', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const res = await deleteApiInfo(row.id)
      if (res) {
        ElMessage.success('接口已删除')
        loadData()
      } else {
        ElMessage.error('删除失败')
      }
    } catch (error) {
      ElMessage.error('请求失败')
    }
  }).catch(() => {})
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const action = isEdit.value ? updateApiInfo : addApiInfo
        const res = await action(formData.value as ApiInfo)
        if (res) {
          ElMessage.success(isEdit.value ? '接口更新成功' : '接口新增成功')
          dialogVisible.value = false
          loadData()
        } else {
          ElMessage.error('保存失败')
        }
      } catch (error) {
        ElMessage.error('请求异常')
      }
    }
  })
}

// 格式化时间
const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString()
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="api-manage-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>接口管理台</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">注册新接口</el-button>
        </div>
      </template>

      <!-- 接口列表数据 -->
      <el-table :data="apiList" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="接口名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="directory" label="目录分类" width="120" />
        
        <el-table-column prop="method" label="请求方式" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="row.method === 'GET' ? 'success' : (row.method === 'POST' ? 'warning' : 'info')"
              effect="dark"
            >
              {{ row.method }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="url" label="目标路由 (URL)" min-width="180" show-overflow-tooltip />
        <el-table-column prop="serviceCode" label="服务编码" width="120" />
        
        <el-table-column prop="requestLimit" label="限流阈值" width="100" align="center">
          <template #default="{ row }">
            <span style="color: #F56C6C; font-weight: bold;">{{ row.requestLimit }}</span> 次/s
          </template>
        </el-table-column>
        
        <el-table-column label="发布状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              disabled
            />
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">修改</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">下线</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/修改接口弹窗 -->
    <el-dialog
      :title="isEdit ? '修改接口属性' : '注册新接口'"
      v-model="dialogVisible"
      width="650px"
      destroy-on-close
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="接口名称" prop="name">
              <el-input v-model="formData.name" placeholder="例如：查询用户信息" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类目录" prop="directory">
              <el-input v-model="formData.directory" placeholder="例如：用户中心" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="请求方式" prop="method">
              <el-select v-model="formData.method" placeholder="请选择" style="width: 100%">
                <el-option label="GET" value="GET" />
                <el-option label="POST" value="POST" />
                <el-option label="PUT" value="PUT" />
                <el-option label="DELETE" value="DELETE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务编码" prop="serviceCode">
              <el-input v-model="formData.serviceCode" placeholder="网关内部映射用的编码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="目标 URL" prop="url">
          <el-input v-model="formData.url" placeholder="代理转发到的实际地址，例如 /user/info" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="限流配置" prop="requestLimit">
              <el-input-number v-model="formData.requestLimit" :min="1" :max="100000" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否启用" prop="status">
              <el-switch
                v-model="formData.status"
                :active-value="1"
                :inactive-value="0"
                active-text="上线"
                inactive-text="停用"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="入参说明" prop="inputDesc">
          <el-input v-model="formData.inputDesc" type="textarea" :rows="2" placeholder="简要描述参数结构" />
        </el-form-item>
        <el-form-item label="出参说明" prop="outputDesc">
          <el-input v-model="formData.outputDesc" type="textarea" :rows="2" placeholder="简要描述返回结构" />
        </el-form-item>
        <el-form-item label="功能描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="接口具体的业务用途" />
        </el-form-item>

      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.api-manage-container {
  height: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
