package com.tobacco.warehouse.modules.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tobacco.warehouse.modules.warehouse.entity.InOutOrder;
import com.tobacco.warehouse.modules.warehouse.enums.OrderStatus;

import java.util.List;

/**
 * 出入库工单 Service 接口
 * 
 * @author warehouse
 */
public interface InOutOrderService extends IService<InOutOrder> {
    
    /**
     * 创建工单（初始状态为 created）
     */
    InOutOrder createOrder(InOutOrder order);
    
    /**
     * 推进工单状态
     * 
     * @param orderId 工单ID
     * @param targetStatus 目标状态
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param remark 备注
     * @return 推进后的工单
     */
    InOutOrder transitionStatus(Long orderId, String targetStatus, Long operatorId, String operatorName, String remark);
    
    /**
     * 审核工单
     * 
     * @param orderId 工单ID
     * @param approved 是否通过
     * @param reviewerId 审核人ID
     * @param reviewerName 审核人名称
     * @param remark 审核备注
     * @return 审核后的工单
     */
    InOutOrder reviewOrder(Long orderId, boolean approved, Long reviewerId, String reviewerName, String remark);
    
    /**
     * 获取工单可用的操作列表
     */
    List<String> getAvailableActions(Long orderId);
    
    /**
     * 获取所有状态列表
     */
    List<OrderStatus> getAllStatuses();
}
