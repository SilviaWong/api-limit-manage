<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown, Cpu } from '@element-plus/icons-vue'

const isCollapse = ref(false)
const route = useRoute()
const router = useRouter()

const username = computed(() => localStorage.getItem('username') || 'Admin')

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/login')
}
</script>

<template>
  <router-view v-if="route.path === '/login'" />
  
  <el-container class="layout-container" v-else>
    <el-aside :width="isCollapse ? '64px' : '240px'" class="aside">
      <div class="logo">
        <el-icon class="logo-icon"><Cpu /></el-icon>
        <h2 v-if="!isCollapse">Orivon API Hub</h2>
      </div>
      <el-menu
        :default-active="route.path"
        class="el-menu-vertical"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        :router="true"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>大盘监控</template>
        </el-menu-item>
        <el-menu-item index="/api-manage">
          <el-icon><Connection /></el-icon>
          <template #title>接口管理</template>
        </el-menu-item>
        <el-menu-item index="/auth-manage">
          <el-icon><User /></el-icon>
          <template #title>授权管理</template>
        </el-menu-item>
        <el-menu-item index="/audit-log">
          <el-icon><Document /></el-icon>
          <template #title>调用审计</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="toggle-btn" @click="isCollapse = !isCollapse">
            <Expand v-if="isCollapse" />
            <Fold v-else />
          </el-icon>
          <span class="breadcrumb">首页 / {{ route.meta.title || '仪表盘' }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleLogout">
            <span class="user-dropdown">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" style="margin-right: 8px;" />
              <span class="username">{{ username }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <!-- 路由出口 -->
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100vw;
}

.aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background-color: #2b3643;
  padding: 0 10px;
}

.logo-icon {
  font-size: 24px;
  color: #409eff;
  margin-right: 8px;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}

.el-menu-vertical {
  border-right: none;
}

.header {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 15px;
}

.breadcrumb {
  font-size: 14px;
  color: #606266;
}

.header-right {
  display: flex;
  align-items: center;
}

.username {
  font-size: 14px;
  color: #333;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  outline: none;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}

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
