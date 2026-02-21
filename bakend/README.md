# 延安烟厂三维数字孪生系统 - 后端

## 项目介绍

本项目是延安烟厂三维数字孪生系统的后端服务，采用Spring Boot 3.2 + PostgreSQL + MyBatis-Plus技术栈开发，实现烟厂仓库的设备管理、运维管理、电池管理等核心功能。

## 技术架构

- **框架**: Spring Boot 3.2.x
- **数据库**: PostgreSQL 15.x
- **ORM**: MyBatis-Plus 3.5.x
- **缓存**: Redis 7.x
- **安全**: Spring Security + JWT
- **文档**: SpringDoc OpenAPI 2.x
- **Java**: JDK 17+

## 项目结构

```
backend/
├── pom.xml                      # 父POM
├── common/                      # 通用模块
│   ├── common-core/            # 核心通用(工具类、常量、异常)
│   ├── common-redis/           # Redis缓存
│   ├── common-security/        # 安全认证(JWT)
│   └── common-swagger/         # Swagger文档
├── modules/                    # 业务模块
│   ├── system/                # 系统模块(用户、角色、权限)
│   ├── equipment/             # 设备管理模块
│   ├── maintenance/           # 运维管理模块
│   └── battery/               # 电池管理模块
└── application/               # 启动入口
    ├── pom.xml
    └── src/main/java/
        └── WarehouseApplication.java
```

## 模块说明

### 通用模块 (common)

| 模块 | 说明 |
|------|------|
| common-core | 核心通用模块，包含基础实体类、通用响应、异常处理、工具类 |
| common-redis | Redis缓存模块，提供缓存服务和分布式锁 |
| common-security | 安全认证模块，JWT token认证和权限控制 |
| common-swagger | API文档模块，集成SpringDoc OpenAPI |

### 业务模块 (modules)

| 模块 | 说明 |
|------|------|
| system | 系统管理模块，包含用户、角色、权限、部门管理 |
| equipment | 设备管理模块，包含设备类别、型号、设备管理 |
| maintenance | 运维管理模块，包含保养点、保养计划、保养记录、维修管理 |
| battery | 电池管理模块，包含电池信息、充电记录、故障记录 |

## API接口

### 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /auth/login | 用户登录 |
| GET | /auth/info | 获取用户信息 |
| POST | /auth/logout | 用户登出 |

### 设备管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/equipment/page | 分页查询设备 |
| GET | /api/v1/equipment/{id} | 获取设备详情 |
| GET | /api/v1/equipment/list | 获取设备列表 |
| GET | /api/v1/equipment/all | 获取所有设备(用于3D可视化) |
| POST | /api/v1/equipment | 创建设备 |
| PUT | /api/v1/equipment | 更新设备 |
| DELETE | /api/v1/equipment/{id} | 删除设备 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.9+
- PostgreSQL 15.x
- Redis 7.x

### 构建项目

```bash
cd code/bakend
mvn clean install -DskipTests
```

### 启动项目

```bash
cd application
mvn spring-boot:run
```

### 访问

- API文档: http://localhost:8080/swagger-ui.html
- 默认账号: admin / admin123

## 配置文件

配置文件位于 `application/src/main/resources/application.yml`

主要配置项：

```yaml
# 数据库配置
spring.datasource.url: jdbc:postgresql://localhost:5432/tobacco_warehouse_db
spring.datasource.username: postgres
spring.datasource.password: postgres

# Redis配置
spring.data.redis.host: localhost
spring.data.redis.port: 6379

# JWT配置
jwt.secret: tobacco-warehouse-secret-key-2025
jwt.expiration: 86400000
```

## 开发规范

1. **命名规范**
   - 类名: UpperCamelCase
   - 方法名: lowerCamelCase
   - 常量: UPPER_SNAKE_CASE

2. **分层架构**
   - Controller: 处理请求和响应
   - Service: 业务逻辑处理
   - Mapper: 数据库操作
   - Entity: 数据实体

3. **API设计**
   - 使用RESTful API规范
   - 响应格式统一使用Result包装
   - 分页使用PageResult包装

## 后续规划

- [ ] 完善设备管理模块
- [ ] 实现运维管理模块
- [ ] 实现电池管理模块
- [ ] 集成MQTT接收设备实时数据
- [ ] 实现WebSocket推送实时数据
- [ ] 添加实时数据缓存和推送功能

## License

MIT License
