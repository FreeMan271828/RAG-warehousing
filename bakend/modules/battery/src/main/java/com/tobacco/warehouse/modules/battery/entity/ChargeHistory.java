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
 * 充电历史记录 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("charge_history")
public class ChargeHistory extends BaseEntity {

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
     * 充电结束时间
     */
    private LocalDateTime chargeEndTime;

    /**
     * 充电时长(秒)
     */
    private Integer duration;

    /**
     * 起始电压(V)
     */
    private BigDecimal startVoltage;

    /**
     * 结束电压(V)
     */
    private BigDecimal endVoltage;

    /**
     * 起始SOC(%)
     */
    private Integer startSoc;

    /**
     * 结束SOC(%)
     */
    private Integer endSoc;

    /**
     * 充电量(Ah)
     */
    private BigDecimal chargedCapacity;

    /**
     * 平均功率(kW)
     */
    private BigDecimal chargePower;

    /**
     * 最高温度(℃)
     */
    private BigDecimal maxTemperature;

    /**
     * 是否完成：0-中断 1-完成
     */
    private Integer isCompleted;

    /**
     * 是否有故障：0-正常 1-故障
     */
    private Integer isError;

    /**
     * 故障描述
     */
    private String errorDesc;
}
