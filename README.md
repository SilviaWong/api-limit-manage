# Orivon API Hub (高性能 API 网关管理平台)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.0-blue)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

## 📖 项目简介

`Orivon API Hub` 是一个现代化、企业级的 API 管理与安全网关平台。它集成了高性能接口转发、多维度流量控制、全链路审计日志以及可视化的数据看板。本项目采用前后端分离架构，旨在为企业提供统一、安全、可观测的接口开放能力。

---

## ✨ 核心特性

- **🚀 高性能代理转发**: 透明转发市级接口，支持动态路由与 Header 注入，确保低延迟。
- **📊 可视化监控大盘**: 基于 **ECharts** 实现的实时看板，包含 7 天调用趋势、热点接口 TOP 5 以及异常错误分布统计。
- **🛡️ 灵活限流体系**: 深度集成 Redis，内置五种经典限流算法（固定窗口、滑动窗口、漏桶、令牌桶、Lua 脚本），支持接口级阈值配置。
- **🔐 企业级安全认证**: 
  - 基于 **JWT (JSON Web Token)** 的管理员认证体系。
  - U-Key/M-Key 双重校验机制，确保调用方身份合法。
  - 支持 IP 白名单策略。
- **📝 深度审计日志**: 毫秒级记录每一次转发详情，包括请求参数、响应结果、耗时及错误码。
- **💎 极简管理 UI**: 使用 Vue 3 + Element Plus 构建的响应式后台，交互体验流畅。

---

## 🛠️ 技术栈

### 后端 (Backend)
- **核心框架**: Spring Boot 4.0.5
- **持久层**: MyBatis Plus 3.5.5
- **数据库**: PostgreSQL (Supabase 托管)
- **缓存/限流**: Redis (Lettuce 客户端)
- **安全**: JJWT (认证), Bouncy Castle (加密)
- **切面**: Spring AOP (自动日志记录)

### 前端 (Frontend)
- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **图表**: ECharts 5.x
- **请求库**: Axios (集成请求/响应拦截器)

---

## 🚀 快速开始

### 1. 环境准备
- JDK 17+
- Maven 3.6+
- Node.js 18+ & pnpm/npm
- Redis 6.0+

### 2. 后端配置与启动
1. **配置文件**: 修改 `src/main/resources/application.yml` 中的数据库与 Redis 连接信息。
2. **运行**:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```
   *默认管理员账号: `admin` / 密码: `admin123`*

### 3. 前端配置与启动
1. **进入目录**: `cd frontend`
2. **安装依赖**: `npm install`
3. **启动**: `npm run dev`
4. **访问**: `http://localhost:5173`

---

## 📂 项目结构

### 后端结构
```text
com.orivon.manage
├── common       # 全局统一响应 Result、ResultCode
├── config       # MyBatis-Plus、WebMvc、Jackson、JWT 配置
├── controller   # REST 控制器 (Dashboard、API管理、Auth、Login)
├── interceptor  # 网关代理拦截器 (Proxy)、后台鉴权拦截器 (AdminAuth)
├── limit        # 五种分布式限流算法实现
├── mapper       # 数据库映射接口
├── model        # 实体类 (Entity) 与视图对象 (VO)
└── service      # 业务逻辑层
```

### 前端结构
```text
frontend/src
├── api          # 接口请求模块化 (Dashboard, Api, Auth, Sys)
├── utils        # axios 封装及拦截器逻辑
├── views        # 业务页面 (Login, Dashboard, ApiManage, AuthManage, AuditLog)
├── router       # 路由配置与全局导航守卫
└── App.vue      # 响应式布局架构 (Sidebar + Header)
```

---

## 📄 开源协议

本项目基于 [MIT License](LICENSE) 协议开源。
