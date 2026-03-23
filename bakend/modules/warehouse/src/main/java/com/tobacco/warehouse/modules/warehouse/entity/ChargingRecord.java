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
 * 充电记录 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("charging_record")
public class ChargingRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 小车ID
     */
    private Long agvId;

    /**
     * 充电站ID
     */
    private Long stationId;

    /**
     * 开始充电时间
     */
    private LocalDateTime startTime;

    /**
     * 结束充电时间
     */
    private LocalDateTime endTime;

    /**
     * 充电前电量
     */
    private BigDecimal startBattery;

    /**
     * 充电后电量
     */
    private BigDecimal endBattery;

    /**
     * 充电时长(秒)
     */
    private Integer chargingDuration;

    /**
     * 用电量(度)
     */
    private BigDecimal electricityUsed;

    /**
     * 充电类型: auto-自动 manual-手动
     */
    private String chargeType;

    /**
     * 结果: success-成功 interrupted-中断 failed-失败
     */
    private String result;

    /**
     * 中断原因
     */
    private String interruptReason;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 备注
     */
    private String remark;
}
