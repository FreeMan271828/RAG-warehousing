package com.tobacco.warehouse.modules.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 货位 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse_location")
public class WarehouseLocation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 货位编码
     */
    private String locationCode;

    /**
     * 货位名称
     */
    private String locationName;

    /**
     * 区域编码
     */
    private String areaCode;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 行号
     */
    private Integer rowNum;

    /**
     * 列号
     */
    private Integer colNum;

    /**
     * 层号
     */
    private Integer levelNum;

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
     * 货位类型：storage-存储位, picking-拣选位, buffer-缓存位
     */
    private String locationType;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 当前状态：empty-空, occupied-占用, reserved-预留
     */
    private String currentStatus;

    /**
     * 容量(立方米)
     */
    private BigDecimal capacity;

    /**
     * 最大承重(kg)
     */
    private BigDecimal maxWeight;

    /**
     * 长度(cm)
     */
    private BigDecimal length;

    /**
     * 宽度(cm)
     */
    private BigDecimal width;

    /**
     * 高度(cm)
     */
    private BigDecimal height;

    /**
     * 描述
     */
    private String description;
}
