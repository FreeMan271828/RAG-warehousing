# 延安烟厂三维数字孪生系统 - 后端设计文档

## 文档信息
- 项目名称：延安烟厂三维数字孪生系统
- 版本：1.0
- 创建日期：2025年2月
- 技术栈：Spring Boot 3.x + PostgreSQL + MyBatis-Plus + Redis

---

## 一、架构设计

### 1.1 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      Vue.js Frontend                        │
│                    (三维可视化展示)                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Spring Boot Backend                    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                  API Gateway / Controller           │    │
│  └─────────────────────────────────────────────────────┘    │
│                              │                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                  Service Layer                      │    │
│  └─────────────────────────────────────────────────────┘    │
│                              │                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Mapper / Repository Layer               │    │
│  └─────────────────────────────────────────────────────┘    │
│                              │                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                │
│  │ PostgreSQL│  │  Redis   │  │   MQTT   │                │
│  └──────────┘  └──────────┘  └──────────┘                │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 模块划分

采用领域驱动设计(DDD)思想，将系统划分为以下模块：

```
backend/
├── common/                    # 通用模块
│   ├── common-core/         # 核心通用(工具类、常量等)
│   ├── common-redis/        # Redis缓存
│   ├── common-security/    # 安全认证
│   └── common-swagger/     # Swagger文档
├── modules/                 # 业务模块
│   ├── system/             # 系统模块(用户、角色、权限)
│   ├── equipment/           # 设备管理模块
│   ├── maintenance/        # 运维管理模块
│   ├── battery/             # 电池管理模块
│   └── warehouse/          # 仓库管理模块
├── gateway/                 # 网关层(可选)
└── application/            # 启动入口
```

---

## 二、技术选型

### 2.1 核心技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.2.x |
| 数据库 | PostgreSQL | 15.x |
| ORM | MyBatis-Plus | 3.5.x |
| 缓存 | Redis | 7.x |
| 文档 | SpringDoc OpenAPI | 2.x |
| 安全 | Spring Security + JWT | 6.x |
| 消息队列 | MQTT | - |
| 构建 | Maven | 3.9.x |
| Java | JDK | 17+ |

### 2.2 依赖管理

采用Spring Boot Parent进行版本管理：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
    <relativePath/>
</parent>
```

---

## 三、数据库设计

### 3.1 数据库配置

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/tobacco_warehouse_db
    username: postgres
    password: postgres
  jpa:
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
```

### 3.2 表结构说明

详细表结构见 `project-desc/database/data-dict/烟厂仓库系统数据库设计.md`

---

## 四、API设计规范

### 4.1 RESTful API规范

| 方法 | 说明 | 示例 |
|------|------|------|
| GET | 查询 | GET /api/v1/equipment |
| POST | 创建 | POST /api/v1/equipment |
| PUT | 更新 | PUT /api/v1/equipment/{id} |
| DELETE | 删除 | DELETE /api/v1/equipment/{id} |

### 4.2 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 业务数据
  },
  "timestamp": 1700000000000
}
```

### 4.3 错误码规范

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 五、模块详细设计

### 5.1 通用模块 (common)

#### 5.1.1 common-core
- 工具类: JsonUtils, DateUtils, StringUtils
- 常量类: Constants, Enums
- 异常类: BusinessException, GlobalExceptionHandler
- 基础类: BaseEntity, BaseController, BaseService

#### 5.1.2 common-redis
- Redis配置: RedisConfig
- 缓存服务: CacheService
- 分布式锁: RedissonLock

#### 5.1.3 common-security
- JWT工具: JwtUtils
- 认证服务: AuthService
- 权限注解: @PreAuthorize, @HasRole
- 安全配置: SecurityConfig

#### 5.1.4 common-swagger
- Swagger配置
- 接口分组
- 文档优化

### 5.2 系统模块 (system)

#### 5.2.1 用户管理 (sys-user)
- 用户CRUD
- 密码加密存储
- 登录/登出
- 个人信息修改

#### 5.2.2 角色管理 (sys-role)
- 角色CRUD
- 角色分配
- 角色权限配置

#### 5.2.3 权限管理 (sys-permission)
- 菜单管理
- 按钮权限
- API权限

#### 5.2.4 部门管理 (sys-dept)
- 组织架构管理
- 部门CRUD

### 5.3 设备管理模块 (equipment)

#### 5.3.1 设备类别 (device-category)
- 类别树形结构
- 类别CRUD

#### 5.3.2 设备型号 (device-model)
- 型号管理
- 规格参数

#### 5.3.3 设备管理 (equipment)
- 设备CRUD
- 3D坐标管理
- 设备状态监控
- 设备位置映射

#### 5.3.4 零件管理 (element)
- 零件库存管理
- 易损件预警
- 库存流水

### 5.4 运维管理模块 (maintenance)

#### 5.4.1 保养点管理 (keep-point)
- 保养点配置
- 检查标准

#### 5.4.2 保养计划 (maintenance-plan)
- 计划制定
- 周期配置
- 任务分配

#### 5.4.3 保养记录 (maintenance-record)
- 保养执行记录
- 检查点情况

#### 5.4.4 维修管理 (maintain-record)
- 故障报修
- 维修派工
- 维修记录

### 5.5 电池管理模块 (battery)

#### 5.5.1 电池基本信息 (battery-basic-info)
- 电池CRUD
- 状态监控

#### 5.5.2 充电实时记录 (charging-now)
- 实时数据推送
- WebSocket推送

#### 5.5.3 充电历史 (charge-history)
- 历史记录查询
- 统计分析

#### 5.5.4 充电故障 (charge-error)
- 故障记录
- 告警通知

### 5.6 仓库管理模块 (warehouse) - 预留

用于未来扩展仓库业务：
- 货位管理
- 托盘管理
- 出入库工单

---

## 六、安全设计

### 6.1 认证方案

采用JWT Token认证：

```java
// Token配置
jwt:
  secret: tobacco-warehouse-secret-key
  expiration: 86400000  # 24小时
  header: Authorization
  prefix: Bearer
```

### 6.2 密码加密

使用BCrypt加密：

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### 6.3 接口权限

- 登录接口: 公开
- 系统管理: 需管理员权限
- 设备查询: 需登录
- 实时数据: 需登录

---

## 七、实时数据推送

### 7.1 MQTT集成

接收设备实时数据：

```yaml
spring:
  mqtt:
    url: tcp://localhost:1883
    username: admin
    password: public
    client-id: warehouse-backend
    topic: warehouse/#
```

### 7.2 WebSocket推送

向前端推送实时数据：

```java
@MessageMapping("/ws/real-time")
public void handleRealTimeData(Message message) {
    // 推送电池、设备状态等
}
```

---

## 八、部署架构

### 8.1 开发环境

```
localhost:8080     # Spring Boot
localhost:5432    # PostgreSQL
localhost:6379    # Redis
localhost:1883    # MQTT
```

### 8.2 生产环境(建议)

```
Nginx (负载均衡)
    ├── Spring Boot Instance 1:8080
    ├── Spring Boot Instance 2:8080
    └── Spring Boot Instance 3:8080
    
PostgreSQL Cluster
Redis Cluster (Sentinel)
MQTT Cluster
```

---

## 九、开发规范

### 9.1 代码规范

- 命名: 驼峰命名
- 类名: UpperCamelCase
- 方法名: lowerCamelCase
- 常量: UPPER_SNAKE_CASE

### 9.2 分层架构

```
Controller  -> Service  -> Mapper  -> Entity
    │           │          │
    ▼           ▼          ▼
  请求       业务逻辑     数据库操作
```

### 9.3 事务管理

- 使用 `@Transactional` 注解
- 默认传播行为: REQUIRED
- 回滚异常: RuntimeException

---

## 十、版本历史

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| 1.0 | 2025-02 | - | 初始版本 |

---

## 十一、AGV自动化调度系统

### 11.1 系统概述

AGV自动化调度系统实现仓库中小车的自动化操作和任务分配。

### 11.2 核心功能

#### 11.2.1 小车自动化
- 充电站四个充电口（上下左右）
- 空闲状态自动返回充电
- 电量消耗和预警计算
- 任务完成后电量评估

#### 11.2.2 任务调度器
- 定时扫描待处理工单（每秒执行）
- 分配任务给空闲小车
- 电量不足时拒绝任务

### 11.3 核心服务

- `AgvSchedulerService` - 调度核心接口
- `AgvSchedulerServiceImpl` - 调度实现
- `AgvSchedulerTask` - 定时任务

### 11.4 数据库表

详细见 `../database/sql/agv_automation_tables.sql`
