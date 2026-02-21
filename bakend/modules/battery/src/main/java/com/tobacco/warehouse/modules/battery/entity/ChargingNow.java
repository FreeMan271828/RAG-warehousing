package com.tobacco.warehouse.modules.battery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充电实时记录 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("charging_now")
public class ChargingNow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 电池ID
     */
    private Long batteryId;

    /**
     * 电池编码
     */
    private String batteryCode;

    /**
     * 充电开始时间
     */
    private LocalDateTime chargeStartTime;

    /**
     * 当前电压(V)
     */
    private BigDecimal voltage;

    /**
     * 当前电流(A)
     */
    private BigDecimal current;

    /**
     * 温度(℃)
     */
    private BigDecimal temperature;

    /**
     * 充电功率(kW)
     */
    private BigDecimal chargePower;

    /**
     * 充电时长(秒)
     */
    private Integer chargeDuration;

    /**
     * 已充电量(Ah)
     */
    private BigDecimal chargedCapacity;

    /**
     * 荷电状态(0-100%)
     */
    private Integer soc;

    /**
     * 是否充电中：0-否 1-是
     */
    private Integer isCharging;
}
