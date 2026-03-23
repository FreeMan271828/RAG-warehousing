package com.tobacco.warehouse.modules.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 充电站 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("charging_station")
public class ChargingStation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 充电站编码
     */
    private String stationCode;

    /**
     * 充电站名称
     */
    private String stationName;

    /**
     * 安装位置
     */
    private String location;

    /**
     * X坐标(3D)
     */
    private BigDecimal xPosition;

    /**
     * Y坐标(3D)
     */
    private BigDecimal yPosition;

    /**
     * Z坐标(3D)
     */
    private BigDecimal zPosition;

    /**
     * 充电功率(W)
     */
    private Integer power;

    /**
     * 额定电压(V)
     */
    private Integer voltage;

    /**
     * 额定电流(A)
     */
    private Integer current;

    /**
     * 状态: 0-禁用 1-启用 2-使用中
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 上充电口
     */
    private String topPort;

    /**
     * 下充电口
     */
    private String bottomPort;

    /**
     * 左充电口
     */
    private String leftPort;

    /**
     * 右充电口
     */
    private String rightPort;
}
