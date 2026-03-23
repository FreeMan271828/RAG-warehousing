package com.tobacco.warehouse.modules.warehouse.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 出入库工单状态枚举
 * 
 * 详细状态机设计文档见：project-desc/backend/state-machine-design.md
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {

    // 工单创建
    CREATED("created", "新建", 1, false),
    
    // 待审核
    PENDING_REVIEW("pending_review", "待审核", 2, false),
    
    // 审核拒绝（可重新提交）
    REJECTED("rejected", "已拒绝", 99, false),
    
    // 待入货（审核通过后，入库/出库用）
    PENDING_IN("pending_in", "待入货", 3, false),
    
    // 入库中
    IN_PROGRESS("in_progress", "入库中", 4, false),
    
    // 存储中
    STORED("stored", "存储中", 5, false),
    
    // 待调拨（内部调拨审核通过）
    PENDING_TRANSFER("pending_transfer", "待调拨", 6, false),
    
    // 调拨中
    TRANSFERRING("transferring", "调拨中", 7, false),
    
    // 调拨完成
    TRANSFER_COMPLETED("transfer_completed", "调拨完成", 8, false),
    
    // 出库中
    OUT_PROGRESS("out_progress", "出库中", 9, false),
    
    // 已完成
    COMPLETED("completed", "已完成", 100, true),
    
    // 已取消
    CANCELLED("cancelled", "已取消", 101, true);

    /**
     * 状态编码
     */
    private final String code;
    
    /**
     * 状态名称
     */
    private final String name;
    
    /**
     * 排序（用于状态推进）
     */
    private final Integer order;
    
    /**
     * 是否为终态（不可推进）
     */
    private final boolean isFinal;

    /**
     * 根据编码获取枚举
     */
    public static OrderStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(status -> status.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取所有可用的下一状态（不带类型过滤）
     */
    public List<OrderStatus> getNextStatuses() {
        return Arrays.stream(values())
                .filter(status -> status.getOrder() > this.getOrder() && !status.isFinal())
                .sorted((a, b) -> a.getOrder().compareTo(b.getOrder()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取指定工单类型的下一状态（带类型过滤）
     * @param orderType 工单类型：in-入库, out-出库, transfer-调拨
     */
    public List<OrderStatus> getNextStatuses(String orderType) {
        // 已拒绝是终态
        if (this == REJECTED) {
            return List.of();
        }
        
        // 根据工单类型和当前状态，返回严格控制的下一状态
        if ("in".equals(orderType)) {
            // 入库流程：created -> pending_review -> pending_in -> in_progress -> stored -> completed
            // 需求：待审核后只有待入库，待入库不能直接入库完成，存储中之后可以修改为已完成
            switch (this) {
                case CREATED:
                    return List.of(PENDING_REVIEW);
                case PENDING_REVIEW:
                    // 待审核后只能到待入库（入/出库）
                    return List.of(PENDING_IN);
                case PENDING_IN:
                    // 待入库不能直接入库完成，必须经过入库中
                    return List.of(IN_PROGRESS);
                case IN_PROGRESS:
                    // 入库中只能到存储中
                    return List.of(STORED);
                case STORED:
                    // 存储中之后可以修改状态已完成
                    return List.of(COMPLETED);
                default:
                    return List.of();
            }
        }
        
        if ("out".equals(orderType)) {
            // 出库流程：created -> pending_review -> pending_in -> out_progress -> completed
            // 需求：待审核后只有待出库，待出库不能直接出库完成
            switch (this) {
                case CREATED:
                    return List.of(PENDING_REVIEW);
                case PENDING_REVIEW:
                    // 待审核后只能到待入货（出库）
                    return List.of(PENDING_IN);
                case PENDING_IN:
                    // 待出库不能直接出库完成，必须经过出库中
                    return List.of(OUT_PROGRESS);
                case OUT_PROGRESS:
                    // 出库中可以到完成
                    return List.of(COMPLETED);
                default:
                    return List.of();
            }
        }
        
        if ("transfer".equals(orderType)) {
            // 调拨流程：created -> pending_review -> pending_transfer -> transferring -> transfer_completed -> completed
            // 需求：待调拨不能直接调拨完成
            switch (this) {
                case CREATED:
                    return List.of(PENDING_REVIEW);
                case PENDING_REVIEW:
                    // 待审核后只能到待调拨
                    return List.of(PENDING_TRANSFER);
                case PENDING_TRANSFER:
                    // 待调拨不能直接调拨完成，必须经过调拨中
                    return List.of(TRANSFERRING);
                case TRANSFERRING:
                    // 调拨中只能到调拨完成
                    return List.of(TRANSFER_COMPLETED);
                case TRANSFER_COMPLETED:
                    // 调拨完成可以到最终完成
                    return List.of(COMPLETED);
                default:
                    return List.of();
            }
        }
        
        // 无类型时的默认处理
        return this.getNextStatuses();
    }

    /**
     * 检查是否可以推进到指定状态（需要考虑工单类型）
     * @param orderType 工单类型：in-入库, out-出库, transfer-调拨
     */
    public boolean canTransitionTo(OrderStatus target, String orderType) {
        // 终态不能转换
        if (this.isFinal()) {
            return false;
        }
        
        // 已拒绝只能回到新建（重新提交）
        if (this == REJECTED) {
            return target == CREATED;
        }
        
        // 只能向前推进（升序）
        if (target.getOrder() <= this.getOrder()) {
            return false;
        }
        
        // 使用类型过滤检查
        List<OrderStatus> allowed = this.getNextStatuses(orderType);
        return allowed.contains(target);
    }
    
    /**
     * 检查是否可以推进到指定状态（默认方法，用于兼容性）
     */
    public boolean canTransitionTo(OrderStatus target) {
        return canTransitionTo(target, null);
    }

    /**
     * 检查是否可以从指定状态转换到当前状态
     */
    public boolean canTransitionFrom(OrderStatus source) {
        return source.canTransitionTo(this);
    }

    /**
     * 获取状态显示颜色
     */
    public String getColor() {
        switch (this) {
            case CREATED:
                return "info";
            case PENDING_REVIEW:
                return "warning";
            case REJECTED:
                return "danger";
            case PENDING_IN:
                return "primary";
            case IN_PROGRESS:
                return "primary";
            case STORED:
                return "success";
            case OUT_PROGRESS:
                return "primary";
            case COMPLETED:
                return "success";
            case CANCELLED:
                return "info";
            default:
                return "info";
        }
    }
}
