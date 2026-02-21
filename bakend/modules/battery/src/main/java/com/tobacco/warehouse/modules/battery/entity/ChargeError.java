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
 * 充电故障记录 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("charge_error")
public class ChargeError extends BaseEntity {

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
     * 故障代码
     */
    private String errorCode;

    /**
     * 故障类型
     */
    private String errorType;

    /**
     * 故障描述
     */
    private String errorDesc;

    /**
     * 故障发生时间
     */
    private LocalDateTime errorTime;

    /**
     * 解决时间
     */
    private LocalDateTime resolveTime;

    /**
     * 故障时电压(V)
     */
    private BigDecimal voltage;

    /**
     * 故障时电流(A)
     */
    private BigDecimal current;

    /**
     * 故障时温度(℃)
     */
    private BigDecimal temperature;

    /**
     * 是否解决：0-未解决 1-已解决
     */
    private Integer isResolved;

    /**
     * 解决方法
     */
    private String resolveMethod;
}
