# 后端 API 接口测试报告

**测试时间**: 2026-02-20  
**测试环境**: http://localhost:8080  
**测试用户**: admin (admin123)

---

## 一、测试摘要

| 状态 | 数量 | 说明 |
|------|------|------|
| ✅ 正常 | 3 | 认证相关接口 |
| ❌ 异常 | 约70+ | 所有需要数据库操作的接口 |

---

## 二、正常接口 (✅)

### 2.1 认证模块

| 接口 | 方法 | 路径 | 状态 | 说明 |
|------|------|------|------|------|
| 用户登录 | POST | `/auth/login` | ✅ 正常 | 返回 token |
| 获取用户信息 | GET | `/auth/info` | ✅ 正常 | 返回用户权限信息 |
| 用户登出 | POST | `/auth/logout` | ✅ 正常 | 成功退出 |

---

## 三、异常接口 (❌) 及问题分析

### 3.1 核心问题：数据库字段缺失

**根本原因**: Java 实体类的 `BaseEntity` 基类定义了 `createBy`、`updateBy`、`deleted` 字段，但数据库表中缺少这些字段。

**错误信息示例**:
```
ERROR: column "create_by" does not exist
ERROR: column "update_by" does not exist
ERROR: column "deleted" does not exist
```

### 3.2 受影响的接口

#### 设备管理模块 (equipment)
| 接口 | 方法 | 路径 | 状态 | 错误 |
|------|------|------|------|------|
| 获取设备列表 | GET | `/api/v1/equipment/list` | ❌ | column "create_by" does not exist |
| 设备分页查询 | GET | `/api/v1/equipment/page` | ❌ | column "create_by" does not exist |
| 获取设备详情 | GET | `/api/v1/equipment/{id}` | ❌ | 参数名获取失败 |
| 创建设备 | POST | `/api/v1/equipment` | ❌ | column "create_by" does not exist |
| 更新设备 | PUT | `/api/v1/equipment` | ❌ | column "create_by" does not exist |
| 更新设备状态 | PUT | `/api/v1/equipment/{id}/status` | ❌ | 参数名获取失败 |
| 删除设备 | DELETE | `/api/v1/equipment/{id}` | ❌ | 参数名获取失败 |
| 获取所有设备(3D) | GET | `/api/v1/equipment/all` | ❌ | column "create_by" does not exist |

#### 电池管理模块 (battery)
| 接口 | 方法 | 路径 | 状态 | 错误 |
|------|------|------|------|------|
| 获取电池列表 | GET | `/api/v1/battery/list` | ❌ | column "create_by" does not exist |
| 电池分页查询 | GET | `/api/v1/battery/page` | ❌ | column "create_by" does not exist |
| 获取电池详情 | GET | `/api/v1/battery/{id}` | ❌ | 参数名获取失败 |
| 创建电池 | POST | `/api/v1/battery` | ❌ | column "create_by" does not exist |
| 更新电池 | PUT | `/api/v1/battery` | ❌ | column "create_by" does not exist |
| 获取故障电池 | GET | `/api/v1/battery/error` | ❌ | column "create_by" does not exist |
| 获取正在充电的电池 | GET | `/api/v1/battery/charging` | ❌ | column "create_by" does not exist |

#### 保养计划模块 (maintenance_plan)
| 接口 | 方法 | 路径 | 状态 | 错误 |
|------|------|------|------|------|
| 获取保养计划列表 | GET | `/api/v1/maintenance/plan/list` | ❌ | column "update_by" does not exist |
| 保养计划分页查询 | GET | `/api/v1/maintenance/plan/page` | ❌ | column "update_by" does not exist |
| 获取保养计划详情 | GET | `/api/v1/maintenance/plan/{id}` | ❌ | 参数名获取失败 |
| 创建保养计划 | POST | `/api/v1/maintenance/plan` | ❌ | column "update_by" does not exist |
| 更新保养计划 | PUT | `/api/v1/maintenance/plan` | ❌ | column "update_by" does not exist |

#### 保养记录模块 (maintenance_record)
| 接口 | 方法 | 路径 | 状态 | 错误 |
|------|------|------|------|------|
| 获取保养记录列表 | GET | `/api/v1/maintenance/record/list` | ❌ | column "create_by" does not exist |
| 创建保养记录 | POST | `/api/v1/maintenance/record` | ❌ | column "create_by" does not exist |

#### 维修记录模块 (maintain_record)
| 接口 | 方法 | 路径 | 状态 | 错误 |
|------|------|------|------|------|
| 获取维修记录列表 | GET | `/api/v1/maintenance/maintain/list` | ❌ | column "create_by" does not exist |
| 创建维修记录 | POST | `/api/v1/maintenance/maintain` | ❌ | column "create_by" does not exist |

#### 保养点模块 (keep_point)
| 接口 | 方法 | 路径 | 状态 | 错误 |
|------|------|------|------|------|
| 获取保养点列表 | GET | `/api/v1/maintenance/keep-point/list` | ❌ | column "create_by" does not exist |
| 创建保养点 | POST | `/api/v1/maintenance/keep-point` | ❌ | column "create_by" does not exist |

#### 零件更换记录模块 (used_element)
| 接口 | 方法 | 路径 | 状态 | 错误 |
|------|------|------|------|------|
| 获取零件更换记录列表 | GET | `/api/v1/maintenance/used-element/list` | ❌ | column "create_by" does not exist |
| 创建零件更换记录 | POST | `/api/v1/maintenance/used-element` | ❌ | column "create_by" does not exist |

#### 充电故障模块 (charge_error)
| 接口 | 方法 | 路径 | 状态 | 错误 |
|------|------|------|------|------|
| 获取未解决故障 | GET | `/api/v1/battery/error/unresolved` | ❌ | column "create_by" does not exist |

#### 实时充电数据模块 (charging_now)
| 接口 | 方法 | 路径 | 状态 | 错误 |
|------|------|------|------|------|
| 获取实时充电列表 | GET | `/api/v1/battery/charging-now/list` | ❌ | 待测试 |

#### 充电历史模块 (charge_history)
| 接口 | 方法 | 路径 | 状态 | 错误 |
|------|------|------|------|------|
| 获取充电历史列表 | GET | `/api/v1/battery/history/list` | ❌ | 待测试 |

---

## 四、修改方案

### 方案一：修改推荐）

在所有数据库表结构（业务表中添加缺失的字段。为以下表添加 `update_by` 和 `deleted` 列：

```sql
-- 为 equipment 表添加缺失字段
ALTER TABLE equipment ADD COLUMN create_by BIGINT;
ALTER TABLE equipment ADD COLUMN update_by BIGINT;
ALTER TABLE equipment ADD COLUMN deleted SMALLINT DEFAULT 0;

-- 为 battery_basic_info 表添加缺失字段
ALTER TABLE battery_basic_info ADD COLUMN create_by BIGINT;
ALTER TABLE battery_basic_info ADD COLUMN update_by BIGINT;
ALTER TABLE battery_basic_info ADD COLUMN deleted SMALLINT DEFAULT 0;

-- 为 maintenance_plan 表添加缺失字段 (缺少 update_by)
ALTER TABLE maintenance_plan ADD COLUMN update_by BIGINT;
ALTER TABLE maintenance_plan ADD COLUMN deleted SMALLINT DEFAULT 0;

-- 为 maintenance_record 表添加缺失字段
ALTER TABLE maintenance_record ADD COLUMN create_by BIGINT;
ALTER TABLE maintenance_record ADD COLUMN update_by BIGINT;
ALTER TABLE maintenance_record ADD COLUMN deleted SMALLINT DEFAULT 0;

-- 为 maintain_record 表添加缺失字段
ALTER TABLE maintain_record ADD COLUMN create_by BIGINT;
ALTER TABLE maintain_record ADD COLUMN update_by BIGINT;
ALTER TABLE maintain_record ADD COLUMN deleted SMALLINT DEFAULT 0;

-- 为 keep_point 表添加缺失字段
ALTER TABLE keep_point ADD COLUMN create_by BIGINT;
ALTER TABLE keep_point ADD COLUMN update_by BIGINT;
ALTER TABLE keep_point ADD COLUMN deleted SMALLINT DEFAULT 0;

-- 为 used_element 表添加缺失字段
ALTER TABLE used_element ADD COLUMN create_by BIGINT;
ALTER TABLE used_element ADD COLUMN update_by BIGINT;
ALTER TABLE used_element ADD COLUMN deleted SMALLINT DEFAULT 0;

-- 为 charging_now 表添加缺失字段
ALTER TABLE charging_now ADD COLUMN create_by BIGINT;
ALTER TABLE charging_now ADD COLUMN update_by BIGINT;
ALTER TABLE charging_now ADD COLUMN deleted SMALLINT DEFAULT 0;

-- 为 charge_history 表添加缺失字段
ALTER TABLE charge_history ADD COLUMN create_by BIGINT;
ALTER TABLE charge_history ADD COLUMN update_by BIGINT;
ALTER TABLE charge_history ADD COLUMN deleted SMALLINT DEFAULT 0;

-- 为 charge_error 表添加缺失字段
ALTER TABLE charge_error ADD COLUMN create_by BIGINT;
ALTER TABLE charge_error ADD COLUMN update_by BIGINT;
ALTER TABLE charge_error ADD COLUMN deleted SMALLINT DEFAULT 0;

-- 为 sys_user 表添加缺失字段 (缺少 update_by)
ALTER TABLE sys_user ADD COLUMN update_by BIGINT;
```

### 方案二：修改 Java 代码

如果不想修改数据库，可以修改 Java 实体类：

1. 移除或修改 `BaseEntity` 中的字段
2. 或者在 MyBatis-Plus 的 mapper XML 中指定插入/更新时忽略这些字段

### 方案三：修复参数名问题

Spring Boot 编译时需要保留参数名，需要在 pom.xml 中添加：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <compilerArgs>
                    <arg>-parameters</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 五、建议

1. **优先使用方案一**：修改数据库是最直接的解决方案，保持代码和数据库的一致性
2. **执行修改脚本**：运行上述 SQL 语句添加缺失的列
3. **重新测试**：修改后重新测试所有接口

---

## 六、数据库表与字段对照

| 表名 | create_by | update_by | deleted |
|------|-----------|-----------|---------|
| equipment | ❌ 缺失 | ❌ 缺失 | ❌ 缺失 |
| battery_basic_info | ❌ 缺失 | ❌ 缺失 | ❌ 缺失 |
| maintenance_plan | ✅ 已有 | ❌ 缺失 | ❌ 缺失 |
| maintenance_record | ❌ 缺失 | ❌ 缺失 | ❌ 缺失 |
| maintain_record | ❌ 缺失 | ❌ 缺失 | ❌ 缺失 |
| keep_point | ❌ 缺失 | ❌ 缺失 | ❌ 缺失 |
| used_element | ❌ 缺失 | ❌ 缺失 | ❌ 缺失 |
| charging_now | ❌ 缺失 | ❌ 缺失 | ❌ 缺失 |
| charge_history | ❌ 缺失 | ❌ 缺失 | ❌ 缺失 |
| charge_error | ❌ 缺失 | ❌ 缺失 | ❌ 缺失 |
| sys_user | ✅ 已有 | ❌ 缺失 | ❌ 缺失 |

---

**报告生成时间**: 2026-02-20 19:42
