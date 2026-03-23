package com.tobacco.warehouse.modules.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 仓库入库口 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse_entry_port")
public class WarehouseEntryPort extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 入库口编码
     */
    private String portCode;

    /**
     * 入库口名称
     */
    private String portName;

    /**
     * 位置: top-上, bottom-下, left-左, right-右
     */
    private String portPosition;

    /**
     * X坐标
     */
    private BigDecimal xPosition;

    /**
     * Y坐标
     */
    private BigDecimal yPosition;

    /**
     * Z坐标
     */
    private BigDecimal zPosition;

    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;
}
