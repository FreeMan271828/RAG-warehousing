package com.tobacco.warehouse.modules.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 托盘 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse_pallet")
public class Pallet extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 托盘编码
     */
    private String palletCode;

    /**
     * 托盘名称
     */
    private String palletName;

    /**
     * 规格
     */
    private String specification;

    /**
     * 托盘类型
     */
    private String palletType;

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
     * 所在货位ID
     */
    private Long locationId;

    /**
     * 所在货位编码
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
