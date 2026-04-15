# 出入库工单状态机设计文档

## 文档信息
- 模块名称：出入库工单状态机
- 版本：1.0
- 创建日期：2026年2月
- 关联代码：`code/bakend/modules/warehouse/src/main/java/com/tobacco/warehouse/modules/warehouse/`

---

## 一、状态定义

### 1.1 状态枚举

| 状态编码 | 状态名称 | 排序 | 是否终态 | 说明 |
|---------|---------|------|---------|------|
| `created` | 新建 | 1 | 否 | 工单刚创建，可提交审核 |
| `pending_review` | 待审核 | 2 | 否 | 等待管理员审核 |
| `rejected` | 已拒绝 | 99 | 否 | 审核未通过，可修改后重新提交 |
| `pending_in` | 待入货 | 3 | 否 | 审核通过，待执行入库/出库操作 |
| `in_progress` | 入库中 | 4 | 否 | 正在执行入库操作 |
| `stored` | 存储中 | 5 | 否 | 已入库，等待确认完成 |
| `pending_transfer` | 待调拨 | 6 | 否 | 等待执行调拨操作 |
| `transferring` | 调拨中 | 7 | 否 | 正在执行调拨操作 |
| `transfer_completed` | 调拨完成 | 8 | 否 | 调拨完成，等待确认 |
| `out_progress` | 出库中 | 9 | 否 | 正在执行出库操作 |
| `completed` | 已完成 | 100 | 是 | 工单正常完成 |
| `cancelled` | 已取消 | 101 | 是 | 工单被取消 |

### 1.2 状态分类

**终态（不可再推进）：**
- `completed` - 已完成
- `cancelled` - 已取消

**非终态（可继续推进）：**
- 其他所有状态

---

## 二、状态流程

### 2.1 入库工单 (orderType = "in")

```
新建(created) → 待审核(pending_review) → 待入库(pending_in) → 入库中(in_progress) → 存储中(stored) → 已完成(completed)
```

### 2.2 出库工单 (orderType = "out")

```
新建(created) → 待审核(pending_review) → 待出库(pending_in) → 出库中(out_progress) → 已完成(completed)
```

### 2.3 调拨工单 (orderType = "transfer")

```
新建(created) → 待审核(pending_review) → 待调拨(pending_transfer) → 调拨中(transferring) → 调拨完成(transfer_completed) → 已完成(completed)
```

---

## 三、状态推进规则

### 3.1 核心规则

1. **单向推进**：所有状态只能向前推进（升序），不能回退
2. **终态不可推进**：终态（COMPLETED、CANCELLED）不可再转换到其他状态
3. **类型过滤**：不同工单类型（in/out/transfer）有不同的状态推进路径

### 3.2 详细规则

#### 待审核 (pending_review) 后：

| 工单类型 | 允许的下一状态 |
|---------|--------------|
| 入库 (in) | 待入库 (pending_in) |
| 出库 (out) | 待出库 (pending_in) |
| 调拨 (transfer) | 待调拨 (pending_transfer) |

#### 待入库/待出库 (pending_in) 后：

| 工单类型 | 允许的下一状态 | 规则说明 |
|---------|--------------|---------|
| 入库 (in) | 入库中 (in_progress) | **不能直接入库完成**，必须经过入库中和存储中 |
| 出库 (out) | 出库中 (out_progress) | **不能直接出库完成**，必须经过出库中 |

#### 待调拨 (pending_transfer) 后：

| 工单类型 | 允许的下一状态 | 规则说明 |
|---------|--------------|---------|
| 调拨 (transfer) | 调拨中 (transferring) | **不能直接调拨完成**，必须经过调拨中 |

#### 存储中 (stored) 后：

| 工单类型 | 允许的下一状态 | 规则说明 |
|---------|--------------|---------|
| 入库 (in) | 已完成 (completed) | **可以直接修改状态为已完成** |

#### 审核拒绝 (rejected) 后：

| 允许的下一状态 | 规则说明 |
|--------------|---------|
| 新建 (created) | 修改后可重新提交 |

---

## 四、业务规则

### 4.1 状态转换约束

- 状态转换由 `OrderStateMachine` 服务统一管理
- 使用 `getNextStatuses(orderType)` 方法获取当前状态可用的下一状态
- 使用 `canTransitionTo(target, orderType)` 方法检查状态转换是否合法

### 4.2 特殊处理

| 场景 | 处理方式 |
|-----|---------|
| 审核拒绝后重新提交 | 状态从 rejected 回到 created，可再次提交审核 |
| 入库存储中确认完成 | stored 状态可以直接转到 completed |
| 出库完成 | 必须经过 out_progress 才能到 completed |
| 调拨完成 | 必须经过 transferring -> transfer_completed -> completed |

---

## 五、API 接口

### 5.1 获取可用操作

```
GET /api/warehouse/inout-order/{id}/actions

Response: ["pending_in"]  // 当前状态可用的操作列表
```

### 5.2 执行状态转换

```
POST /api/warehouse/inout-order/transition

Request:
{
  "orderId": 123,
  "targetStatus": "in_progress",
  "operatorId": 1,
  "operatorName": "张三",
  "remark": "开始入库操作"
}
```

---

## 六、代码实现

### 6.1 核心类

| 类名 | 职责 |
|-----|------|
| `OrderStatus` | 状态枚举，定义所有状态及状态转换逻辑 |
| `OrderStateMachine` | 状态机服务，执行状态转换和管理 |
| `InOutOrderServiceImpl` | 工单服务，调用状态机完成业务操作 |

### 6.2 关键方法

**OrderStatus 类：**

```java
// 根据编码获取枚举
public static OrderStatus fromCode(String code)

// 获取所有可用的下一状态（不带类型过滤）
public List<OrderStatus> getNextStatuses()

// 获取指定工单类型的下一状态（带类型过滤）
public List<OrderStatus> getNextStatuses(String orderType)

// 检查是否可以推进到指定状态（需要考虑工单类型）
public boolean canTransitionTo(OrderStatus target, String orderType)
```

**OrderStateMachine 类：**

```java
// 执行状态转换
public InOutOrder transition(InOutOrder order, String targetStatus, Long operatorId, String operatorName, String remark)

// 获取当前状态可用的操作列表（不带类型过滤）
public List<String> getAvailableActions(String currentStatusCode)

// 获取工单类型对应的可用操作（带类型过滤）⭐推荐
public List<String> getAvailableActionsByType(String orderType, String currentStatusCode)

// 检查是否可以执行特定操作
public boolean canPerformAction(String currentStatusCode, String action)

// 获取状态对应的操作名称
public String getActionName(String statusCode)
```

---

## 七、前端集成

### 7.1 获取可用操作

前端调用 `getAvailableActions(id)` 接口获取当前工单可用的操作列表，根据工单类型自动过滤。

### 7.2 状态显示颜色

| 状态 | 颜色 | Element UI 标签类型 |
|-----|------|-------------------|
| 新建 | info | info |
| 待审核 | warning | warning |
| 已拒绝 | danger | danger |
| 待入货/入库中/出库中/调拨中 | primary | primary |
| 存储中/已完成 | success | success |
| 已取消 | info | info |

---

## 八、版本历史

| 版本 | 日期 | 修改内容 |
|-----|------|---------|
| 1.0 | 2026-02-22 | 初始版本，定义三种工单类型的状态流程 |

