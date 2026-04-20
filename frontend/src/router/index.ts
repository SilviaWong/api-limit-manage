import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '大盘监控' }
  },
  {
    path: '/api-manage',
    name: 'ApiManage',
    component: () => import('@/views/ApiManage.vue'),
    meta: { title: '接口管理' }
  },
  {
    path: '/auth-manage',
    name: 'AuthManage',
    component: () => import('@/views/AuthManage.vue'),
    meta: { title: '授权管理' }
  },
  {
    path: '/audit-log',
    name: 'AuditLog',
    component: () => import('@/views/AuditLog.vue'),
    meta: { title: '调用审计' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录时只能访问 /login
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    // 已经登录了又去访问登录页，直接跳转到首页
    next('/dashboard')
  } else {
    next()
  }
})

export default router
