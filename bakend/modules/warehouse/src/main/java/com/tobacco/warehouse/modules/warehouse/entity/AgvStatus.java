package com.tobacco.warehouse.modules.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AGV小车状态 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agv_status")
public class AgvStatus extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联设备ID
     */
    private Long agvId;

    /**
     * 设备名称（非数据库字段，关联查询获取）
     */
    @TableField(exist = false)
    private String equipmentName;

    /**
     * 设备编号（非数据库字段，关联查询获取）
     */
    @TableField(exist = false)
    private String equipmentCode;

    /**
     * 电量百分比(0-100)
     */
    private BigDecimal batteryLevel;

    /**
     * 小车状态: idle-空闲 working-工作中 charging-充电中 returning-返回中 fault-故障
     */
    private String agvStatus;

    /**
     * 当前任务ID
     */
    private Long currentTaskId;

    /**
     * 当前位置
     */
    private String currentLocation;

    /**
     * 目标位置
     */
    private String targetLocation;

    /**
     * 当前速度(m/s)
     */
    private BigDecimal speed;

    /**
     * 上次充电时间
     */
    private LocalDateTime lastChargeTime;

    /**
     * 累计工作时间(秒)
     */
    private Integer totalWorkTime;

    /**
     * 累计运行距离(米)
     */
    private BigDecimal totalDistance;

    /**
     * 故障代码
     */
    private String faultCode;

    /**
     * 故障信息
     */
    private String faultMessage;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

    /**
     * X坐标(3D)
     */
    private BigDecimal xPosition;

    /**
     * Y坐标(3D)
     */
    private BigDecimal yPosition;

    /**
     * 旋转角度
     */
    private BigDecimal rotation;

    /**
     * 当前所在充电站ID
     */
    private Long chargingStationId;

    /**
     * 当前充电口
     */
    private String chargingPort;

    /**
     * 每秒电量消耗百分比
     */
    private BigDecimal batteryConsumptionRate;

    /**
     * 移动速度 m/s
     */
    private BigDecimal moveSpeed;

    /**
     * 转弯额外耗时(秒)
     */
    private BigDecimal turnOverhead;

    /**
     * 预警电量
     */
    private BigDecimal warningBatteryLevel;
}
