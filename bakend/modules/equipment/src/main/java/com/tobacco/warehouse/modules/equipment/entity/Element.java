package com.tobacco.warehouse.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 零件 实体类
 *
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("element")
public class Element extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 零件编码
     */
    private String elementCode;

    /**
     * 零件名称
     */
    private String elementName;

    /**
     * 设备型号ID
     */
    private Long modelId;

    /**
     * 类别
     */
    private String category;

    /**
     * 规格
     */
    private String specification;

    /**
     * 单位
     */
    private String unit;

    /**
     * 库存数量
     */
    private Integer stockQuantity;

    /**
     * 最小库存
     */
    private Integer minStock;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 是否易损件：0-否 1-是
     */
    private Integer isVulnerable;

    /**
     * 更换周期(天)
     */
    private Integer replaceCycle;

    /**
     * 供应商
     */
    private String supplier;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
