# Pangu Gateway · 盘古智能网关

> :: Smart Gateway :: (v1.0.0)

---

## 🇨🇳 中文说明

### 项目简介

Pangu Gateway（盘古智能网关）是基于 Spring Cloud Gateway 构建的企业级智能 API 网关，支持 GraalVM Native Image 编译。项目名"盘古"取自中国神话中的创世之神，寓意开创性的基础设施组件。

它不仅包含传统网关的路由转发功能，还内建了 AI 网关代理、安全防护、灰度发布、熔断限流、审计日志、告警通知等企业级特性，并配有 Vue 3 管理控制台。

### 核心特性

| 特性 | 说明 |
|------|------|
| **动态路由管理** | REST API 配置路由，支持 Nacos / Redis / File 三种持久化方式 |
| **认证鉴权** | 支持 local (JWT) / remote (第三方) / mixed 三种认证模式 |
| **灰度发布** | 基于 `X-Gray-Tag` 头的流量染色路由 |
| **安全防护** | IP 黑白名单、签名验证 (HMAC-SHA256)、SQL 注入过滤、XSS 过滤 |
| **熔断器** | Resilience4j CircuitBreaker，动态注册配置 |
| **限流** | Caffeine 令牌桶本地限流 + Redis 分布式限流 |
| **AI 网关** | 模型路由 (priority/cost/intent/round-robin)、Prompt 注入检测、PII 脱敏、Token 计量配额 |
| **API Key 管理** | 外部客户端 API Key 认证，与 JWT 双轨并行 |
| **WebSocket 双向代理** | 前后端 WebSocket 桥接 |
| **国密算法 (SM2/SM4)** | 请求 / 响应加解密 |
| **审计日志** | 自动记录管理 API 操作 |
| **告警通知** | 钉钉 / 企业微信 / 邮件 |
| **可观测性** | Micrometer 指标 + 时间序列 + Prometheus 兼容 + 监控大屏 |
| **访问日志 & 慢请求检测** | 详细请求日志 + 慢请求告警 |
| **CORS & SPA 内嵌** | 跨域配置 + 前端静态资源内嵌服务 |

### 三种部署模式

| 模式 | 存储 | 缓存 | 集群同步 | 适用场景 |
|------|------|------|----------|----------|
| **Standalone**（默认） | Memory + File (JSON) | Caffeine 本地 | 无 | 单节点，最轻量 |
| **Simple Cluster** | Memory + Redis | Redis 分布式 | Redis Pub/Sub | 中小集群 |
| **Microservice Cluster** | Memory + Nacos | Redis / Caffeine | Nacos 推送 | 大规模微服务集群 |


### 快速开始

**1. 独立模式启动（零外部依赖）：**

```bash
cd gateway-native
.\gateway-native.exe
```

网关默认监听 `8088` 端口，无需 Redis / Nacos 即可运行。

**2. 集群模式启动：**

```bash
# Redis 集群模式
export PANGU_GATEWAY_STORAGE_TYPE=redis
.\gateway-native.exe -Dspring-boot.run.profiles=cluster

# Nacos 微服务集群模式
export PANGU_GATEWAY_STORAGE_TYPE=nacos
.\gateway-native.exe -Dspring-boot.run.profiles=cluster
```

### 配置说明

所有配置项均支持环境变量覆盖，详见 `application.yml` 中的注释说明。

关键环境变量：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `PANGU_GATEWAY_STORAGE_TYPE` | `file` | 存储模式：file / redis / nacos |
| `PANGU_GATEWAY_CACHE_TYPE` | `caffeine` | 缓存模式：caffeine / redis |
| `SERVER_PORT` | `8088` | 网关监听端口 |

---

## 🇺🇸 English Description

### Overview

Pangu Gateway is an enterprise-grade smart API gateway built on Spring Cloud Gateway with GraalVM Native Image support. Named after the Chinese mythological creator deity, it signifies a foundational infrastructure component.

Beyond traditional gateway routing, it features built-in AI gateway proxying, security protection, grayscale deployment, circuit breaking, rate limiting, audit logging, alert notifications, and a Vue 3 management console.

### Core Features

| Feature | Description |
|---------|-------------|
| **Dynamic Routing** | REST API-based route configuration with Nacos / Redis / File persistence |
| **Authentication** | Local (JWT) / Remote (3rd-party) / Mixed authentication modes |
| **Grayscale Deployment** | Traffic-stained routing via `X-Gray-Tag` header |
| **Security Protection** | IP blacklist/whitelist, HMAC-SHA256 signature verification, SQL injection & XSS filtering |
| **Circuit Breaker** | Resilience4j CircuitBreaker with dynamic configuration registration |
| **Rate Limiting** | Caffeine token-bucket local rate limiting + Redis distributed rate limiting |
| **AI Gateway** | Model routing (priority/cost/intent/round-robin), Prompt injection detection, PII desensitization, Token metering & quota |
| **API Key Management** | External client API Key authentication, dual-track with JWT |
| **WebSocket Proxy** | Bidirectional WebSocket bridging |
| **National Crypto (SM2/SM4)** | Request/response encryption/decryption |
| **Audit Logging** | Automatic management API operation recording |
| **Alert Notifications** | DingTalk / WeCom / Email |
| **Observability** | Micrometer metrics + time-series + Prometheus-compatible + monitoring dashboard |
| **Access Log & Slow Request Detection** | Detailed request logging + slow request alerts |
| **CORS & SPA Embedded** | Cross-origin configuration + embedded frontend static assets |

### Deployment Modes

| Mode | Storage | Cache | Cluster Sync | Use Case |
|------|---------|-------|--------------|----------|
| **Standalone** (default) | Memory + File (JSON) | Caffeine local | None | Single node, lightweight |
| **Simple Cluster** | Memory + Redis | Redis distributed | Redis Pub/Sub | Medium clusters |
| **Microservice Cluster** | Memory + Nacos | Redis / Caffeine | Nacos push | Large-scale microservices |

### Quick Start

**1. Standalone mode (zero external dependencies):**

```bash
cd gateway-native
.\gateway-native.exe
```

Gateway listens on port `8088` by default. No Redis / Nacos required.

**2. Cluster mode:**

```bash
# Redis cluster mode
export PANGU_GATEWAY_STORAGE_TYPE=redis
.\gateway-native.exe -Dspring-boot.run.profiles=cluster

# Nacos microservice cluster mode
export PANGU_GATEWAY_STORAGE_TYPE=nacos
.\gateway-native.exe -Dspring-boot.run.profiles=cluster
```

### Configuration

All configuration items support environment variable overrides. See `application.yml` for detailed comments.

Key environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `PANGU_GATEWAY_STORAGE_TYPE` | `file` | Storage mode: file / redis / nacos |
| `PANGU_GATEWAY_CACHE_TYPE` | `caffeine` | Cache mode: caffeine / redis |
| `SERVER_PORT` | `8088` | Gateway listen port |

---

## License

Private — All rights reserved.
