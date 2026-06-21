<div align="center">
  <p><strong>Spring Boot DDD项目模板</strong></p>

  <p>
    <a href="#"><img src="https://img.shields.io/badge/Java-26-blue.svg" alt="Java Version"/></a>
    <a href="#"><img src="https://img.shields.io/badge/Maven-3.8+-green.svg" alt="Maven"/></a>
    <a href="#"><img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License"/></a>
    <a href="#"><img src="https://img.shields.io/badge/JUnit-5.12.2-orange.svg" alt="JUnit"/></a>
  </p>
</div>



# Spring Boot DDD Template

基于 Spring Boot 4.1 + Java 26 的领域驱动设计（DDD）模板项目，集成了企业级开发常用的基础设施能力。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 26 |
| 框架 | Spring Boot | 4.1.0 |
| Web | Spring WebMvc | - |
| 持久层 | Spring Data JPA + H2 | - |
| 安全 | Spring Security | - |
| 对象映射 | MapStruct | 1.6.3 |
| API 文档 | SpringDoc OpenAPI | 2.8.0 |
| 本地缓存 | Caffeine | - |
| 分布式缓存 | Redis (Spring Data Redis) | - |
| 消息队列 | RabbitMQ (Spring AMQP) | - |
| 限流 | Bucket4j | 8.15.0 |
| 简化代码 | Lombok | - |
| 参数校验 | Bean Validation | - |

## 项目结构

```
src/main/java/io/github/springdddtemplate/
├── application/              # 应用层 - 用例编排
│   ├── dto/                  # 数据传输对象
│   ├── mapper/               # MapStruct 映射器
│   └── service/              # 应用服务
├── domain/                   # 领域层 - 核心业务逻辑
│   ├── event/                # 域事件（sealed interface）
│   ├── exception/            # 业务异常
│   ├── model/
│   │   ├── entity/           # 领域实体
│   │   └── valueobject/      # 值对象
│   ├── publisher/            # 域事件发布器接口（依赖倒置）
│   ├── repository/           # 仓储接口
│   └── service/              # 领域服务
├── infrastructure/           # 基础设施层 - 技术实现
│   ├── cache/                # 缓存配置（Caffeine / Redis）
│   ├── email/                # 邮件服务
│   ├── messaging/            # RabbitMQ 消息队列
│   ├── persistence/          # JPA 持久化适配器
│   ├── ratelimit/            # Bucket4j 限流过滤器
│   ├── scheduling/           # 定时任务
│   └── security/             # Spring Security 配置
├── interfaces/               # 接口层 - HTTP 入口
│   ├── config/               # Swagger 配置
│   ├── controller/           # REST 控制器
│   ├── exception/            # 全局异常处理
│   ├── response/             # 统一响应格式
│   └── validation/           # 自定义校验约束
└── SpringDddTemplateApplication.java
```

## 快速开始

### 环境要求

- Java 26+
- Maven 3.8+

### 运行

```bash
mvn spring-boot:run
```

启动后访问：

- API 文档：http://localhost:8080/swagger-ui.html
- H2 控制台：http://localhost:8080/h2-console

### 测试

```bash
mvn test
```

## 配置说明

所有自定义配置位于 `application.yaml` 的 `app` 节点下：

### 缓存

通过 `app.cache.type` 切换缓存策略：

| 值 | 说明 | 适用场景 |
|----|------|----------|
| `caffeine` | Caffeine 本地缓存（默认） | 单实例部署 / 开发 |
| `redis` | Redis 分布式缓存 | 多实例部署 / 生产 |

使用 Redis 缓存需额外配置：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 消息队列（RabbitMQ）

默认关闭。启用需设置 `app.messaging.enabled: true` 并配置 RabbitMQ 连接：

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

域事件通过 `sealed interface DomainEvent` 定义，使用 `DomainEventPublisher` 发布到 RabbitMQ Topic Exchange。未启用时自动降级为日志输出（`LoggingEventPublisher`）。

### 限流

默认启用，基于 Bucket4j 令牌桶算法。支持按端点独立配置：

```yaml
app:
  rate-limit:
    enabled: true
    default-capacity: 100       # 最大突发请求数
    default-refill-tokens: 10   # 每周期补充令牌数
    default-refill-duration: 1  # 补充周期（秒）
    endpoints:
      "/api/users": "50,5,1"    # 格式: capacity,refillTokens,refillDuration
```

### 定时任务

```yaml
app:
  scheduling:
    enabled: true
    cleanup-cron: "0 0 2 * * *"   # 每天凌晨 2 点执行清理
    health-check-interval: 300000  # 健康检查间隔（毫秒）
```

## API 示例

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/users` | 创建用户 | 需要 |
| GET | `/api/users/{id}` | 获取用户 | 需要 |
| PUT | `/api/users/{id}` | 更新用户 | 需要 |
| DELETE | `/api/users/{id}` | 删除用户 | 需要 |
| PUT | `/api/users/{id}/activate` | 激活用户 | 需要 |
| PUT | `/api/users/{id}/deactivate` | 停用用户 | 需要 |

### 请求示例

```json
POST /api/users
{
  "username": "john_doe",
  "password": "securePass123",
  "email": "john@example.com",
  "role": "MEMBER"
}
```

### 响应格式

```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "Member",
    "enabled": true
  }
}
```

## 设计要点

- **DDD 分层**：domain 层不依赖任何框架，application 层编排用例，infrastructure 层提供技术实现
- **依赖倒置**：domain 定义接口（如 `UserRepository`、`DomainEventPublisher`），infrastructure 提供实现
- **值对象**：`EmailAddress`、`UserRole` 使用不可变对象封装业务概念
- **域事件**：使用 Java `sealed interface` + pattern matching 实现类型安全的事件体系
- **条件化装配**：缓存、消息、限流等基础设施通过 `@ConditionalOnProperty` 按需加载
- **Bean Validation**：自定义 `@ValidRole` 约束 + 全局异常处理提供统一错误响应
