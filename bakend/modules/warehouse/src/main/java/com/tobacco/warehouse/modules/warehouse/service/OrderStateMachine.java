package com.tobacco.warehouse.modules.warehouse.service;

import com.tobacco.warehouse.modules.warehouse.entity.InOutOrder;
import com.tobacco.warehouse.modules.warehouse.enums.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 出入库工单状态机服务
 * 
 * 详细设计文档见：project-desc/backend/state-machine-design.md
 */
@Service
public class OrderStateMachine {

    /**
     * 工单状态转换
     * 
     * @param order 工单
     * @param targetStatus 目标状态
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param remark 备注
     * @return 转换后的工单
     */
    public InOutOrder transition(InOutOrder order, String targetStatus, Long operatorId, String operatorName, String remark) {
        OrderStatus currentStatus = OrderStatus.fromCode(order.getOrderStatus());
        OrderStatus target = OrderStatus.fromCode(targetStatus);
        
        if (currentStatus == null) {
            throw new IllegalStateException("工单当前状态无效: " + order.getOrderStatus());
        }
        
        if (target == null) {
            throw new IllegalArgumentException("无效的目标状态: " + targetStatus);
        }
        
        // 检查状态转换是否合法（带类型过滤）
        if (!currentStatus.canTransitionTo(target, order.getOrderType())) {
            throw new IllegalStateException(
                String.format("状态转换不合法: %s -> %s", currentStatus.getName(), target.getName())
            );
        }
        
        // 更新工单状态
        order.setOrderStatus(target.getCode());
        
        // 记录操作历史
        String history = String.format("[%s] %s -> %s", 
            LocalDateTime.now().toString(), 
            currentStatus.getName(), 
            target.getName());
        
        if (remark != null && !remark.isEmpty()) {
            history += "，备注: " + remark;
        }
        
        // 追加历史记录
        String existingHistory = order.getRemark();
        if (existingHistory == null || existingHistory.isEmpty()) {
            order.setRemark(history);
        } else {
            order.setRemark(existingHistory + "\n" + history);
        }
        
        // 设置操作人和时间
        order.setOperatorId(operatorId);
        order.setOperatorName(operatorName);
        
        // 根据状态设置时间
        switch (target) {
            case PENDING_REVIEW:
                // 提交审核时记录
                break;
            case IN_PROGRESS:
                order.setActualStartTime(LocalDateTime.now());
                break;
            case STORED:
                // 存储完成
                break;
            case OUT_PROGRESS:
                // 开始出库
                break;
            case COMPLETED:
                order.setActualEndTime(LocalDateTime.now());
                break;
            default:
                break;
        }
        
        return order;
    }

    /**
     * 获取当前状态可用的操作列表
     */
    public List<String> getAvailableActions(String currentStatusCode) {
        OrderStatus current = OrderStatus.fromCode(currentStatusCode);
        if (current == null) {
            return List.of();
        }
        
        // 终态不可操作
        if (current.isFinal()) {
            return List.of();
        }
        
        // 获取所有可用的下一状态
        return current.getNextStatuses().stream()
            .map(OrderStatus::getCode)
            .collect(Collectors.toList());
    }

    /**
     * 获取所有状态列表
     */
    public List<OrderStatus> getAllStatuses() {
        return Arrays.asList(OrderStatus.values());
    }

    /**
     * 检查是否可以执行特定操作
     */
    public boolean canPerformAction(String currentStatusCode, String action) {
        OrderStatus current = OrderStatus.fromCode(currentStatusCode);
        OrderStatus target = OrderStatus.fromCode(action);
        
        if (current == null || target == null) {
            return false;
        }
        
        return current.canTransitionTo(target);
    }

    /**
     * 获取状态对应的操作名称
     */
    public String getActionName(String statusCode) {
        OrderStatus status = OrderStatus.fromCode(statusCode);
        if (status == null) {
            return statusCode;
        }
        
        switch (status) {
            case CREATED:
                return "提交审核";
            case PENDING_REVIEW:
                return "审核通过";
            case REJECTED:
                return "重新提交";
            case PENDING_IN:
                return "开始入库";
            case IN_PROGRESS:
                return "入库完成";
            case STORED:
                return "开始出库";
            case PENDING_TRANSFER:
                return "开始调拨";
            case TRANSFERRING:
                return "调拨完成";
            case TRANSFER_COMPLETED:
                return "确认完成";
            case OUT_PROGRESS:
                return "出库完成";
            case COMPLETED:
                return "完成";
            default:
                return status.getName();
        }
    }
    
    /**
     * 获取工单类型对应的可用操作（根据工单类型返回不同的状态推进选项）
     * 
     * @param orderType 工单类型：in-入库, out-出库, transfer-调拨
     * @param currentStatusCode 当前状态
     * @return 可用的操作列表
     */
    public List<String> getAvailableActionsByType(String orderType, String currentStatusCode) {
        OrderStatus current = OrderStatus.fromCode(currentStatusCode);
        if (current == null) {
            return List.of();
        }
        
        // 终态不可操作
        if (current.isFinal()) {
            return List.of();
        }
        
        // 使用带类型过滤的 getNextStatuses 方法
        List<OrderStatus> allNext = current.getNextStatuses(orderType);
        
        return allNext.stream()
            .map(OrderStatus::getCode)
            .collect(Collectors.toList());
    }
}
