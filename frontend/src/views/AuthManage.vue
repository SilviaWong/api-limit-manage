<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAuthList, addAuth, updateAuth, deleteAuth } from '@/api/auth'
import { getApiInfoList } from '@/api/api-info'
import { getUnitList } from '@/api/unit'
import type { ApiAuth, ApiInfo, UnitInfo } from '@/types'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const authList = ref<ApiAuth[]>([])
const apiList = ref<ApiInfo[]>([])
const unitList = ref<UnitInfo[]>([])

// 获取关联数据
const loadOptions = async () => {
  try {
    const [apiRes, unitRes] = await Promise.all([
      getApiInfoList(),
      getUnitList()
    ])
    // axios interceptor directly returns res.data
    apiList.value = apiRes || []
    unitList.value = unitRes || []
  } catch (error) {
    console.error('获取选项数据失败', error)
  }
}

// 辅助函数：根据ID找名称
const getApiName = (apiId: number) => {
  const api = apiList.value.find(item => item.id === apiId)
  return api ? api.name : `未知接口(${apiId})`
}

const getUnitName = (unitId: number) => {
  const unit = unitList.value.find(item => item.id === unitId)
  return unit ? unit.unitName : `未知租户(${unitId})`
}

// 获取列表数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getAuthList()
    // interceptor unwraps data, so res is ApiAuth[]
    authList.value = res || []
  } catch (error) {
    ElMessage.error('请求失败')
  } finally {
    loading.value = false
  }
}

// 弹窗表单相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const formData = ref<ApiAuth>({
  apiId: undefined,
  unitId: undefined,
  uKey: '',
  mKey: '',
  whiteList: '',
  status: 1
})

const rules = {
  apiId: [{ required: true, message: '请选择授权接口', trigger: 'change' }],
  unitId: [{ required: true, message: '请选择授权租户', trigger: 'change' }],
  uKey: [{ required: true, message: '请输入 uKey', trigger: 'blur' }],
  mKey: [{ required: true, message: '请输入 mKey', trigger: 'blur' }]
}

// 自动生成 uKey 和 mKey
const generateKeys = () => {
  formData.value.uKey = 'uk_' + Math.random().toString(36).substring(2, 10)
  formData.value.mKey = 'mk_' + Math.random().toString(36).substring(2, 15)
}

const handleAdd = () => {
  isEdit.value = false
  formData.value = {
    apiId: undefined,
    unitId: undefined,
    uKey: '',
    mKey: '',
    whiteList: '',
    status: 1
  }
  generateKeys()
  dialogVisible.value = true
}

const handleEdit = (row: ApiAuth) => {
  isEdit.value = true
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row: ApiAuth) => {
  ElMessageBox.confirm('确定要删除该授权配置吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const res = await deleteAuth(row.id!)
      // interceptor returns boolean
      if (res) {
        ElMessage.success('删除成功')
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
        const action = isEdit.value ? updateAuth : addAuth
        const res = await action(formData.value)
        if (res) {
          ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
          dialogVisible.value = false
          loadData()
        } else {
          ElMessage.error('操作失败')
        }
      } catch (error) {
        ElMessage.error('请求失败')
      }
    }
  })
}

onMounted(async () => {
  await loadOptions()
  await loadData()
})
</script>

<template>
  <div class="auth-manage-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>授权管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增授权</el-button>
        </div>
      </template>

      <!-- 列表数据 -->
      <el-table :data="authList" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="所属接口" min-width="150">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ getApiName(row.apiId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="授权租户" min-width="150">
          <template #default="{ row }">
            {{ getUnitName(row.unitId) }}
          </template>
        </el-table-column>
        <el-table-column prop="uKey" label="U-Key (调用凭证)" min-width="140" />
        <el-table-column prop="mKey" label="M-Key (签名密钥)" min-width="160" show-overflow-tooltip />
        <el-table-column prop="whiteList" label="IP 白名单" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.whiteList || '未配置(不限制)' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      :title="isEdit ? '编辑授权' : '新增授权'"
      v-model="dialogVisible"
      width="550px"
      destroy-on-close
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="授权接口" prop="apiId">
          <el-select v-model="formData.apiId" placeholder="请选择API" style="width: 100%">
            <el-option
              v-for="item in apiList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.url }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="授权租户" prop="unitId">
          <el-select v-model="formData.unitId" placeholder="请选择租户" style="width: 100%">
            <el-option
              v-for="item in unitList"
              :key="item.id"
              :label="item.unitName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="U-Key" prop="uKey">
          <el-input v-model="formData.uKey" placeholder="调用凭证，可自动生成">
            <template #append>
              <el-button @click="generateKeys">重新生成</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="M-Key" prop="mKey">
          <el-input v-model="formData.mKey" placeholder="签名密钥" />
        </el-form-item>

        <el-form-item label="IP白名单" prop="whiteList">
          <el-input 
            v-model="formData.whiteList" 
            type="textarea" 
            placeholder="允许调用的IP地址，多个用逗号隔开，留空则不限制" 
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.auth-manage-container {
  height: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
