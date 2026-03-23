package com.tobacco.warehouse.modules.warehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.warehouse.entity.InOutOrder;
import com.tobacco.warehouse.modules.warehouse.enums.OrderStatus;
import com.tobacco.warehouse.modules.warehouse.mapper.InOutOrderMapper;
import com.tobacco.warehouse.modules.warehouse.service.InOutOrderService;
import com.tobacco.warehouse.modules.warehouse.service.OrderStateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 出入库工单 Service 実装
 * 
 * @author warehouse
 */
@Service
public class InOutOrderServiceImpl extends ServiceImpl<InOutOrderMapper, InOutOrder> implements InOutOrderService {

    @Autowired
    private OrderStateMachine orderStateMachine;

    @Override
    public InOutOrder createOrder(InOutOrder order) {
        // 生成工单编号
        String orderCode = generateOrderCode(order.getOrderType());
        order.setOrderCode(orderCode);
        
        // 初始状态为新建
        order.setOrderStatus(OrderStatus.CREATED.getCode());
        
        // 设置创建时间
        order.setCreateTime(LocalDateTime.now());
        
        save(order);
        return order;
    }

    @Override
    public InOutOrder transitionStatus(Long orderId, String targetStatus, Long operatorId, String operatorName, String remark) {
        InOutOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("工单不存在: " + orderId);
        }
        
        // 使用状态机进行状态转换
        InOutOrder updatedOrder = orderStateMachine.transition(
            order, targetStatus, operatorId, operatorName, remark
        );
        
        updateById(updatedOrder);
        return updatedOrder;
    }

    @Override
    public InOutOrder reviewOrder(Long orderId, boolean approved, Long reviewerId, String reviewerName, String remark) {
        InOutOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("工单不存在: " + orderId);
        }
        
        // 检查当前状态是否为待审核
        OrderStatus currentStatus = OrderStatus.fromCode(order.getOrderStatus());
        if (currentStatus != OrderStatus.PENDING_REVIEW) {
            throw new RuntimeException("当前工单状态不是待审核，无法审核");
        }
        
        // 设置审核信息
        order.setReviewerId(reviewerId);
        order.setReviewerName(reviewerName);
        order.setReviewTime(LocalDateTime.now());
        order.setReviewRemark(remark);
        order.setOperatorId(reviewerId);
        order.setOperatorName(reviewerName);
        
        // 根据审核结果设置状态
        if (approved) {
            // 根据工单类型设置不同的目标状态
            String orderType = order.getOrderType();
            OrderStatus targetStatus;
            if ("transfer".equals(orderType)) {
                // 调拨 -> 待调拨
                targetStatus = OrderStatus.PENDING_TRANSFER;
            } else {
                // 入库/出库 -> 待入货
                targetStatus = OrderStatus.PENDING_IN;
            }
            order.setOrderStatus(targetStatus.getCode());
            order.setRemark((order.getRemark() != null ? order.getRemark() + "\n" : "") 
                + "[" + LocalDateTime.now() + "] 审核通过 -> " + targetStatus.getName());
        } else {
            // 审核拒绝 -> 已拒绝
            order.setOrderStatus(OrderStatus.REJECTED.getCode());
            order.setRemark((order.getRemark() != null ? order.getRemark() + "\n" : "") 
                + "[" + LocalDateTime.now() + "] 审核拒绝: " + remark);
        }
        
        updateById(order);
        return order;
    }

    @Override
    public List<String> getAvailableActions(Long orderId) {
        InOutOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("工单不存在: " + orderId);
        }
        // 根据工单类型返回不同的可用操作
        return orderStateMachine.getAvailableActionsByType(order.getOrderType(), order.getOrderStatus());
    }

    @Override
    public List<OrderStatus> getAllStatuses() {
        return orderStateMachine.getAllStatuses();
    }

    /**
     * 生成工单编号
     */
    private String generateOrderCode(String orderType) {
        String prefix;
        switch (orderType) {
            case "in":
                prefix = "IN";
                break;
            case "out":
                prefix = "OUT";
                break;
            case "transfer":
                prefix = "TR";
                break;
            default:
                prefix = "WO";
        }
        
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        
        return prefix + date + random;
    }
}
