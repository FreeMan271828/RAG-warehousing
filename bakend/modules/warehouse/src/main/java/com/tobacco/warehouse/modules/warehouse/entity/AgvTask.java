package com.tobacco.warehouse.modules.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AGV任务 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agv_task")
public class AgvTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务编号
     */
    private String taskCode;

    /**
     * 任务类型: in-入库 out-出库 transfer-调拨
     */
    private String taskType;

    /**
     * 关联工单ID
     */
    private Long orderId;

    /**
     * 关联工单编号
     */
    private String orderCode;

    /**
     * 执行小车ID
     */
    private Long agvId;

    /**
     * 优先级(1-10)
     */
    private Integer priority;

    /**
     * 起点位置
     */
    private String fromLocation;

    /**
     * 终点位置
     */
    private String toLocation;

    /**
     * 货物信息
     */
    private String cargoInfo;

    /**
     * 任务状态: pending-待执行 executing-执行中 completed-已完成 failed-失败 cancelled-已取消
     */
    private String taskStatus;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 实际耗时(秒)
     */
    private Integer actualDuration;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 预计行驶距离
     */
    private BigDecimal estimatedDistance;

    /**
     * 预计耗时(秒)
     */
    private Integer estimatedDuration;

    /**
     * 预计电量消耗
     */
    private BigDecimal estimatedBatteryConsumption;

    /**
     * 实际行驶距离
     */
    private BigDecimal actualDistance;

    /**
     * 返回充电站距离
     */
    private BigDecimal returnDistance;

    /**
     * 返回所需电量
     */
    private BigDecimal returnBatteryNeeded;
}
