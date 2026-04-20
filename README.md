# API Limit Manage (API 限流与代理管理系统)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

## 📖 项目简介

`api-limit-manage` 是一个基于 Spring Boot 构建的高性能 API 管理与限流系统。它不仅提供了完善的 API 元数据管理功能，还集成了多种分布式限流算法，并支持透明的 API 代理转发。本项目旨在为微服务架构或开放平台提供统一的流量控制、安全验证和审计日志解决方案。

## ✨ 核心特性

- **多策略限流**: 深度集成 Redis，提供五种经典限流算法实现：
  - **固定窗口 (Fixed Window)**: 简单高效的单位时间计数。
  - **滑动窗口 (Sliding Window)**: 解决临界突发流量问题。
  - **漏桶算法 (Leaky Bucket)**: 强制平滑流出速率。
  - **令牌桶算法 (Token Bucket)**: 允许一定程度的突发流量。
  - **Lua 脚本限流**: 利用 Redis 原子性实现高性能限流。
- **智能代理**: 内置高效代理服务，支持请求透明转发与动态路由。
- **安全认证**: 集成 Bouncy Castle 加密库，支持敏感信息的加解密处理（API Key/Secret）。
- **完整管理体系**:
  - **API 信息管理**: 定义后端接口地址、描述、状态等。
  - **单位信息管理**: 关联 API 的所属单位及权限。
  - **认证管理**: 动态生成与校验 API 访问凭证。
- **审计日志**: 详细记录每一次 API 调用的耗时、响应状态及访问详情。

## 🛠️ 技术栈

| 领域 | 技术实现 |
| :--- | :--- |
| **核心框架** | Spring Boot 4.0.5 |
| **编程语言** | Java 17 |
| **持久层** | MyBatis Plus 3.5.5 & Spring Data JPA |
| **数据库** | PostgreSQL (Hosted on Supabase) |
| **缓存/限流器** | Redis (Lettuce 客户端) |
| **网络请求** | Apache HttpClient5 |
| **安全加密** | Bouncy Castle (BCPROV) |
| **其他工具** | Lombok, Commons Lang3, Jackson |

## 🚀 快速开始

### 1. 环境准备
- JDK 17+
- Maven 3.6+
- Redis 6.0+
- PostgreSQL (或直接使用配置好的 Supabase)

### 2. 克隆项目
```bash
git clone https://github.com/your-username/api-limit-manage.git
cd api-limit-manage
```

### 3. 配置数据库与 Redis
修改 `src/main/resources/application.yml` 中的相关配置：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-db-host:5432/postgres
    username: your-username
    password: your-password
  data:
    redis:
      host: localhost
      port: 6379

# 配置默认限流策略
rate-limit:
  strategy: fixed-window # 可选: fixed-window / sliding-window / leaky-bucket / token-bucket / lua-script
```

### 4. 编译与运行
```bash
./mvnw clean package
java -jar target/api-limit-manage-0.0.1-SNAPSHOT.jar
```

## 📂 项目结构

```text
com.orivon.manage
├── common       # 通用返回对象 (Result) 与工具类 (CryptoUtils)
├── config       # RestTemplate 与 Web 跨域配置
├── controller   # REST API 控制器 (API信息、认证、代理)
├── interceptor  # 代理拦截器逻辑
├── limit        # 五种限流算法的具体实现
├── mapper       # MyBatis Plus Mapper 接口
├── model        # 数据库实体类 (Entity)
└── service      # 业务逻辑接口及其实现 (Impl)
```

## 接口说明 (部分)

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| `GET` | `/api/list` | 获取所有 API 信息列表 |
| `POST` | `/api/add` | 新增 API 接口定义 |
| `POST` | `/api/auth/generate` | 为指定单位生成访问凭证 |
| `ALL` | `/proxy/**` | API 代理入口（根据规则转发并计费/限流） |

## 🤝 贡献指南

1. Fork 本项目。
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)。
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)。
4. 推送到分支 (`git push origin feature/AmazingFeature`)。
5. 开启一个 Pull Request。

## 📄 开源协议

本项目基于 [MIT License](LICENSE) 协议开源。
