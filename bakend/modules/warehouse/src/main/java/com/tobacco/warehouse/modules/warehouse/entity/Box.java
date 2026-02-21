package com.tobacco.warehouse.modules.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 箱子 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse_box")
public class Box extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 箱子编码
     */
    private String boxCode;

    /**
     * 箱子名称
     */
    private String boxName;

    /**
     * 箱子类型
     */
    private String boxType;

    /**
     * 规格
     */
    private String specification;

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
     * 重量(kg)
     */
    private BigDecimal weight;

    /**
     * 最大承重(kg)
     */
    private BigDecimal maxWeight;

    /**
     * 所属托盘ID
     */
    private Long palletId;

    /**
     * 所属托盘编码
     */
    private String palletCode;

    /**
     * 当前状态：empty-空, loaded-有货, in_transit-运输中
     */
    private String currentStatus;

    /**
     * 货物名称
     */
    private String cargoName;

    /**
     * 货物数量
     */
    private Integer cargoQuantity;

    /**
     * 货物单位
     */
    private String cargoUnit;

    /**
     * 采购日期
     */
    private LocalDate purchaseDate;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;
}
