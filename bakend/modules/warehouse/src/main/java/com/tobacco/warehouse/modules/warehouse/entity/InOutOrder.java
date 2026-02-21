package com.tobacco.warehouse.modules.warehouse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 出入库工单 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse_inout_order")
public class InOutOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 工单编号
     */
    private String orderCode;

    /**
     * 工单类型：in-入库, out-出库, transfer-调拨
     */
    private String orderType;

    /**
     * 工单状态：pending-待执行, in_progress-执行中, completed-已完成, cancelled-已取消
     */
    private String status;

    /**
     * 货位ID
     */
    private Long locationId;

    /**
     * 货位编码
     */
    private String locationCode;

    /**
     * 托盘ID
     */
    private Long palletId;

    /**
     * 托盘编码
     */
    private String palletCode;

    /**
     * 箱子ID
     */
    private Long boxId;

    /**
     * 箱子编码
     */
    private String boxCode;

    /**
     * 货物名称
     */
    private String cargoName;

    /**
     * 货物编码
     */
    private String cargoCode;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单位
     */
    private String unit;

    /**
     * 重量(kg)
     */
    private BigDecimal weight;

    /**
     * 体积(立方米)
     */
    private BigDecimal volume;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 供应商/客户
     */
    private String supplier;

    /**
     * 计划执行日期
     */
    private LocalDate planDate;

    /**
     * 实际执行时间
     */
    private LocalDateTime executeTime;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 来源/目的地
     */
    private String sourceDest;

    /**
     * 备注
     */
    private String remark;
}
