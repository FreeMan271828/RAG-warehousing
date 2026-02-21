package com.tobacco.warehouse.modules.battery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 电池基本信息 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("battery_basic_info")
public class BatteryBasicInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 电池编码
     */
    private String batteryCode;

    /**
     * 电池名称
     */
    private String batteryName;

    /**
     * 电池类型
     */
    private String batteryType;

    /**
     * 电池型号
     */
    private String batteryModel;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 采购日期
     */
    private LocalDate purchaseDate;

    /**
     * 保修到期日期
     */
    private LocalDate warrantyExpireDate;

    /**
     * 额定容量(Ah)
     */
    private BigDecimal ratedCapacity;

    /**
     * 额定电压(V)
     */
    private BigDecimal ratedVoltage;

    /**
     * 总充电次数
     */
    private Integer totalChargeTimes;

    /**
     * 总使用时长(小时)
     */
    private BigDecimal totalUsageHours;

    /**
     * 健康状态(0-100%)
     */
    private Integer healthStatus;

    /**
     * 状态：0-报废 1-空闲 2-使用中 3-充电中 4-故障
     */
    private Integer status;

    /**
     * 当前位置
     */
    private String location;

    /**
     * 备注
     */
    private String remark;
}
