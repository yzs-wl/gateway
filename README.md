<div align="center">

# 🌐 Pangu Gateway · 盘古智能网关

**企业级智能 API 网关** (v1.0.0)

[![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-6DB33F?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud-gateway)
[![Vue.js](https://img.shields.io/badge/Vue.js-3-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![GraalVM](https://img.shields.io/badge/GraalVM-Native%20Image-F27E3E?style=flat-square&logo=graalvm&logoColor=white)](https://www.graalvm.org/)
[![License](https://img.shields.io/badge/License-Private-lightgrey?style=flat-square)](#license)
[![Website](https://img.shields.io/badge/Website-xbox--admin.icu-0969DA?style=flat-square&logo=googlechrome&logoColor=white)](https://xbox-admin.icu)

</div>

---

## 🇨🇳 中文说明

### 📖 项目简介

**Pangu Gateway（盘古智能网关）** 是基于 Spring Cloud Gateway 构建的企业级智能 API 网关，支持 GraalVM Native Image 编译。项目名"盘古"取自中国神话中的创世之神，寓意开创性的基础设施组件。

它不仅包含传统网关的路由转发功能，还内建了 AI 网关代理、安全防护、灰度发布、熔断限流、审计日志、告警通知等企业级特性，并配有 Vue 3 管理控制台。

### ✨ 核心特性

<details open>
<summary><b>🛣️ 路由与流量治理</b></summary>

| 特性 | 说明 |
|------|------|
| **动态路由管理** | REST API 配置路由，支持 Nacos / Redis / File 三种持久化方式 |
| **灰度发布** | 基于 `X-Gray-Tag` 头的流量染色路由 |
| **WebSocket 双向代理** | 前后端 WebSocket 桥接 |
| **CORS & SPA 内嵌** | 跨域配置 + 前端静态资源内嵌服务 |

</details>

<details open>
<summary><b>🔐 安全与认证</b></summary>

| 特性 | 说明 |
|------|------|
| **认证鉴权** | 支持 local (JWT) / remote (第三方) / mixed 三种认证模式 |
| **API Key 管理** | 外部客户端 API Key 认证，与 JWT 双轨并行 |
| **安全防护** | IP 黑白名单、签名验证 (HMAC-SHA256)、SQL 注入过滤、XSS 过滤 |
| **国密算法 (SM2/SM4)** | 请求 / 响应加解密 |

</details>

<details open>
<summary><b>🛡️ 稳定性与可观测性</b></summary>

| 特性 | 说明 |
|------|------|
| **熔断器** | Resilience4j CircuitBreaker，动态注册配置 |
| **限流** | Caffeine 令牌桶本地限流 + Redis 分布式限流 |
| **可观测性** | Micrometer 指标 + 时间序列 + Prometheus 兼容 + 监控大屏 |
| **访问日志 & 慢请求检测** | 详细请求日志 + 慢请求告警 |
| **审计日志** | 自动记录管理 API 操作 |
| **告警通知** | 钉钉 / 企业微信 / 邮件 |

</details>

<details open>
<summary><b>🤖 AI 网关</b></summary>

| 特性 | 说明 |
|------|------|
| **模型路由** | priority / cost / intent / round-robin 多策略 |
| **Prompt 注入检测** | 内建提示词注入攻击识别 |
| **PII 脱敏** | 敏感信息自动脱敏 |
| **Token 计量配额** | 用量计量与配额管控 |

</details>

### 🏗️ 三种部署模式

| 模式 | 存储 | 缓存 | 集群同步 | 适用场景 |
|------|------|------|----------|----------|
| **Standalone**（默认） | Memory + File (JSON) | Caffeine 本地 | 无 | 单节点，最轻量 |
| **Simple Cluster** | Memory + Redis | Redis 分布式 | Redis Pub/Sub | 中小集群 |
| **Microservice Cluster** | Memory + Nacos | Redis / Caffeine | Nacos 推送 | 大规模微服务集群 |

---

## 🇺🇸 English Description

### 📖 Overview

**Pangu Gateway** is an enterprise-grade smart API gateway built on Spring Cloud Gateway with GraalVM Native Image support. Named after the Chinese mythological creator deity, it signifies a foundational infrastructure component.

Beyond traditional gateway routing, it features built-in AI gateway proxying, security protection, grayscale deployment, circuit breaking, rate limiting, audit logging, alert notifications, and a Vue 3 management console.

### ✨ Core Features

<details open>
<summary><b>🛣️ Routing & Traffic Governance</b></summary>

| Feature | Description |
|---------|-------------|
| **Dynamic Routing** | REST API-based route configuration with Nacos / Redis / File persistence |
| **Grayscale Deployment** | Traffic-stained routing via `X-Gray-Tag` header |
| **WebSocket Proxy** | Bidirectional WebSocket bridging |
| **CORS & SPA Embedded** | Cross-origin configuration + embedded frontend static assets |

</details>

<details open>
<summary><b>🔐 Security & Authentication</b></summary>

| Feature | Description |
|---------|-------------|
| **Authentication** | Local (JWT) / Remote (3rd-party) / Mixed authentication modes |
| **API Key Management** | External client API Key authentication, dual-track with JWT |
| **Security Protection** | IP blacklist/whitelist, HMAC-SHA256 signature verification, SQL injection & XSS filtering |
| **National Crypto (SM2/SM4)** | Request/response encryption/decryption |

</details>

<details open>
<summary><b>🛡️ Stability & Observability</b></summary>

| Feature | Description |
|---------|-------------|
| **Circuit Breaker** | Resilience4j CircuitBreaker with dynamic configuration registration |
| **Rate Limiting** | Caffeine token-bucket local rate limiting + Redis distributed rate limiting |
| **Observability** | Micrometer metrics + time-series + Prometheus-compatible + monitoring dashboard |
| **Access Log & Slow Request Detection** | Detailed request logging + slow request alerts |
| **Audit Logging** | Automatic management API operation recording |
| **Alert Notifications** | DingTalk / WeCom / Email |

</details>

<details open>
<summary><b>🤖 AI Gateway</b></summary>

| Feature | Description |
|---------|-------------|
| **Model Routing** | priority / cost / intent / round-robin strategies |
| **Prompt Injection Detection** | Built-in prompt injection attack detection |
| **PII Desensitization** | Automatic sensitive information masking |
| **Token Metering & Quota** | Usage metering and quota control |

</details>

### 🏗️ Deployment Modes

| Mode | Storage | Cache | Cluster Sync | Use Case |
|------|---------|-------|--------------|----------|
| **Standalone** (default) | Memory + File (JSON) | Caffeine local | None | Single node, lightweight |
| **Simple Cluster** | Memory + Redis | Redis distributed | Redis Pub/Sub | Medium clusters |
| **Microservice Cluster** | Memory + Nacos | Redis / Caffeine | Nacos push | Large-scale microservices |

---

## 🚀 快速开始 / Quick Start

**1. 独立模式启动（零外部依赖）/ Standalone mode (zero external dependencies):**

```bash
cd gateway-native
.\gateway-native.exe
```

网关默认监听 `8088` 端口，无需 Redis / Nacos 即可运行。
Gateway listens on port `8088` by default. No Redis / Nacos required.

**2. 集群模式启动 / Cluster mode:**

```bash
# Redis 集群模式 / Redis cluster mode
export PANGU_GATEWAY_STORAGE_TYPE=redis
.\gateway-native.exe -Dspring-boot.run.profiles=cluster

# Nacos 微服务集群模式 / Nacos microservice cluster mode
export PANGU_GATEWAY_STORAGE_TYPE=nacos
.\gateway-native.exe -Dspring-boot.run.profiles=cluster
```

## ⚙️ 配置说明 / Configuration

所有配置项均支持环境变量覆盖，详见 `application.yml` 中的注释说明。
All configuration items support environment variable overrides. See `application.yml` for detailed comments.

| 变量 / Variable | 默认值 / Default | 说明 / Description |
|-----------------|------------------|--------------------|
| `PANGU_GATEWAY_STORAGE_TYPE` | `file` | 存储模式：file / redis / nacos |
| `PANGU_GATEWAY_CACHE_TYPE` | `caffeine` | 缓存模式：caffeine / redis |
| `SERVER_PORT` | `8088` | 网关监听端口 |

---

## 🔗 友情链接 / Friendly Links

- [🎮 Xbox Admin](https://xbox-admin.icu) — 企业信息化系统智能搭建工具（RBAC 权限 / AI 智能对话 / 设计中心 / BPM 流程引擎 / 低代码模型）

---

## License

Private — All rights reserved.
