package com.tobacco.warehouse.modules.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

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
     * 最大承重(kg)
     */
    private BigDecimal maxWeight;

    /**
     * 材质
     */
    private String material;

    /**
     * 颜色
     */
    private String color;

    /**
     * 所属托盘ID
     */
    private Long palletId;

    /**
     * 货位ID
     */
    private Long locationId;

    /**
     * 货位编码
     */
    private String locationCode;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;
}
