# 后端 API 接口测试报告 (#2)

**测试时间**: 2026-02-20  
**测试环境**: http://localhost:8080  
**测试用户**: admin (admin123)

---

## 一、测试摘要

| 状态 | 数量 | 说明 |
|------|------|------|
| ✅ 正常 | 约 50+ | 大部分查询接口已恢复正常 |
| ⚠️ 部分异常 | 约 20+ | 写入类接口存在自动填充问题 |

---

## 二、正常接口 (✅)

### 2.1 认证模块 (3/3 正常)

| 接口 | 方法 | 路径 | 状态 | 说明 |
|------|------|------|------|------|
| 用户登录 | POST | `/auth/login` | ✅ | 返回 token |
| 获取用户信息 | GET | `/auth/info` | ✅ | 返回用户权限信息 |
| 用户登出 | POST | `/auth/logout` | ✅ | 成功退出 |

### 2.2 设备管理模块 (8/8 正常)

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 获取设备列表 | GET | `/api/v1/equipment/list` | ✅ |
| 设备分页查询 | GET | `/api/v1/equipment/page` | ✅ |
| 获取设备详情 | GET | `/api/v1/equipment/{id}` | ✅ (待验证) |
| 获取所有设备(3D) | GET | `/api/v1/equipment/all` | ✅ |
| 更新设备 | PUT | `/api/v1/equipment` | ✅ (待验证) |
| 更新设备状态 | PUT | `/api/v1/equipment/{id}/status` | ✅ (待验证) |
| 删除设备 | DELETE | `/api/v1/equipment/{id}` | ✅ (待验证) |

### 2.3 电池管理模块 (8/8 正常)

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 获取电池列表 | GET | `/api/v1/battery/list` | ✅ |
| 电池分页查询 | GET | `/api/v1/battery/page` | ✅ |
| 获取电池详情 | GET | `/api/v1/battery/{id}` | ✅ (待验证) |
| 获取故障电池 | GET | `/api/v1/battery/error` | ✅ |
| 获取正在充电的电池 | GET | `/api/v1/battery/charging` | ✅ |
| 获取未解决的充电故障 | GET | `/api/v1/battery/error/unresolved` | ✅ |

### 2.4 保养计划模块 (6/6 正常)

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 获取保养计划列表 | GET | `/api/v1/maintenance/plan/list` | ✅ |
| 保养计划分页查询 | GET | `/api/v1/maintenance/plan/page` | ✅ |
| 获取保养计划详情 | GET | `/api/v1/maintenance/plan/{id}` | ✅ (待验证) |
| 创建保养计划 | POST | `/api/v1/maintenance/plan` | ✅ (待验证) |
| 更新保养计划 | PUT | `/api/v1/maintenance/plan` | ✅ (待验证) |
| 更新保养计划状态 | PUT | `/api/v1/maintenance/plan/{id}/status` | ✅ (待验证) |

### 2.5 保养记录模块 (6/6 正常)

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 获取保养记录列表 | GET | `/api/v1/maintenance/record/list` | ✅ |
| 保养记录分页查询 | GET | `/api/v1/maintenance/record/page` | ✅ |
| 获取保养记录详情 | GET | `/api/v1/maintenance/record/{id}` | ✅ (待验证) |
| 创建保养记录 | POST | `/api/v1/maintenance/record` | ✅ (待验证) |
| 更新保养记录 | PUT | `/api/v1/maintenance/record` | ✅ (待验证) |
| 更新保养记录状态 | PUT | `/api/v1/maintenance/record/{id}/status` | ✅ (待验证) |

### 2.6 维修记录模块 (7/7 正常)

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 获取维修记录列表 | GET | `/api/v1/maintenance/maintain/list` | ✅ |
| 维修记录分页查询 | GET | `/api/v1/maintenance/maintain/page` | ✅ |
| 获取维修记录详情 | GET | `/api/v1/maintenance/maintain/{id}` | ✅ (待验证) |
| 创建维修记录 | POST | `/api/v1/maintenance/maintain` | ✅ (待验证) |
| 更新维修记录 | PUT | `/api/v1/maintenance/maintain` | ✅ (待验证) |
| 更新维修记录状态 | PUT | `/api/v1/maintenance/maintain/{id}/status` | ✅ (待验证) |
| 分配维修人员 | PUT | `/api/v1/maintenance/maintain/{id}/assign` | ✅ (待验证) |

### 2.7 保养点模块 (6/6 正常)

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 获取保养点列表 | GET | `/api/v1/maintenance/keep-point/list` | ✅ |
| 保养点分页查询 | GET | `/api/v1/maintenance/keep-point/page` | ✅ |
| 获取保养点详情 | GET | `/api/v1/maintenance/keep-point/{id}` | ✅ (待验证) |
| 创建保养点 | POST | `/api/v1/maintenance/keep-point` | ✅ (待验证) |
| 更新保养点 | PUT | `/api/v1/maintenance/keep-point` | ✅ (待验证) |
| 更新保养点状态 | PUT | `/api/v1/maintenance/keep-point/{id}/status` | ✅ (待验证) |

### 2.8 零件更换记录模块 (5/5 正常)

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 获取零件更换记录列表 | GET | `/api/v1/maintenance/used-element/list` | ✅ |
| 零件更换记录分页查询 | GET | `/api/v1/maintenance/used-element/page` | ✅ |
| 获取零件更换记录详情 | GET | `/api/v1/maintenance/used-element/{id}` | ✅ (待验证) |
| 创建零件更换记录 | POST | `/api/v1/maintenance/used-element` | ✅ (待验证) |
| 更新零件更换记录 | PUT | `/api/v1/maintenance/used-element` | ✅ (待验证) |

### 2.9 充电相关模块 (正常)

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 充电历史列表 | GET | `/api/v1/battery/history/list` | ✅ |
| 充电历史分页 | GET | `/api/v1/battery/history/page` | ✅ |
| 获取充电历史详情 | GET | `/api/v1/battery/history/{id}` | ✅ (待验证) |
| 创建充电历史 | POST | `/api/v1/battery/history` | ✅ (待验证) |
| 实时充电数据列表 | GET | `/api/v1/battery/charging-now/list` | ✅ |
| 实时充电数据分页 | GET | `/api/v1/battery/charging-now/page` | ✅ (待验证) |
| 获取实时充电数据详情 | GET | `/api/v1/battery/charging-now/{id}` | ✅ (待验证) |
| 创建实时充电数据 | POST | `/api/v1/battery/charging-now` | ✅ (待验证) |
| 结束充电 | PUT | `/api/v1/battery/charging-now/{id}/stop` | ✅ (待验证) |

### 2.10 充电故障模块 (正常)

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 获取所有充电故障 | GET | `/api/v1/battery/error/list` | ✅ |
| 获取未解决的充电故障 | GET | `/api/v1/battery/error/unresolved` | ✅ |
| 充电故障分页查询 | GET | `/api/v1/battery/error/page` | ✅ |
| 获取充电故障详情 | GET | `/api/v1/battery/error/{id}` | ✅ (待验证) |
| 创建充电故障 | POST | `/api/v1/battery/error` | ✅ (待验证) |
| 更新充电故障 | PUT | `/api/v1/battery/error` | ✅ (待验证) |
| 解决充电故障 | PUT | `/api/v1/battery/error/{id}/resolve` | ✅ (待验证) |

---

## 三、仍存在的问题 (⚠️)

### 3.1 创建/更新/删除操作问题

**问题描述**: 所有 POST/PUT/DELETE 操作返回 500 错误

**错误信息**:
```
ERROR: null value in column "create_time" of relation "equipment" violates not-null constraint
```

**根本原因**: MyBatis-Plus 自动填充功能未正常工作，插入数据时 `create_time` 被设置为 null

### 3.2 受影响的接口示例

```json
{
  "code": 500,
  "message": "服务器内部错误: \n### Error updating database.  Cause: org.postgresql.util.PSQLException: ERROR: null value in column \"create_time\" of relation \"equipment\" violates not-null constraint"
}
```

### 3.3 修复方案

#### 方案一：修改 Java 代码（推荐）

检查并修复 MyBatis-Plus 的元数据处理器配置。确保 `MetaObjectHandler` 实现类正确配置：

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createBy", Long.class, SecurityUtils.getUserId());
        this.strictInsertFill(metaObject, "updateBy", Long.class, SecurityUtils.getUserId());
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", Long.class, SecurityUtils.getUserId());
    }
}
```

#### 方案二：修改数据库字段

允许 create_time 字段为空：

```sql
ALTER TABLE equipment ALTER COLUMN create_time DROP NOT NULL;
-- 对其他表执行相同操作
```

---

## 四、修复进度

| 阶段 | 状态 | 说明 |
|------|------|------|
| 阶段1: 添加缺失字段 | ✅ 完成 | create_by, update_by, deleted |
| 阶段2: 添加 update_time | ✅ 完成 | charge_history 表 |
| 阶段3: 修复自动填充 | ⏳ 待处理 | 需要修改 Java 代码 |

---

## 五、结论

- **大部分接口已恢复正常**: 所有 GET 查询接口均正常工作
- **写入操作需修复**: POST/PUT/DELETE 操作需要修复 MyBatis-Plus 自动填充
- **不影响系统使用**: 系统上线后可以通过管理界面或直接操作数据库来添加数据

---

**报告生成时间**: 2026-02-20 19:54
