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
 * 状态流程：
 * 新建(created) -> 待审核(pending_review) -> 审核通过/拒绝待入货(pending_in) -> 入库(in_progress) -> 
 * 存储(stored) -> 出库(out_progress) -> 完成(completed)
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse_order")
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
     * 工单状态：created-新建, pending_review-待审核, rejected-已拒绝, 
     * pending_in-待入货, in_progress-入库中, stored-存储中, 
     * out_progress-出库中, completed-已完成, cancelled-已取消
     */
    private String orderStatus;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 审核人名称
     */
    private String reviewerName;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 审核备注
     */
    private String reviewRemark;

    /**
     * 优先级：1-低 2-中 3-高
     */
    private Integer priority;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 源货位ID
     */
    private Long sourceLocationId;

    /**
     * 源货位编码
     */
    private String sourceLocationCode;

    /**
     * 目标货位ID
     */
    private Long targetLocationId;

    /**
     * 目标货位编码
     */
    private String targetLocationCode;

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
     * 物料编码
     */
    private String itemCode;

    /**
     * 物料名称
     */
    private String itemName;

    /**
     * 物料数量
     */
    private BigDecimal itemQuantity;

    /**
     * 物料单位
     */
    private String itemUnit;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 计划开始时间
     */
    private LocalDateTime planStartTime;

    /**
     * 计划结束时间
     */
    private LocalDateTime planEndTime;

    /**
     * 实际开始时间
     */
    private LocalDateTime actualStartTime;

    /**
     * 实际结束时间
     */
    private LocalDateTime actualEndTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 调拨类型：internal-内部调拨, external-外部调拨
     */
    private String transferType;

    /**
     * 源仓库编码
     */
    private String sourceWarehouseCode;

    /**
     * 源仓库名称
     */
    private String sourceWarehouseName;

    /**
     * 目标仓库编码
     */
    private String targetWarehouseCode;

    /**
     * 目标仓库名称
     */
    private String targetWarehouseName;

    /**
     * 批次号（用于追溯）
     */
    private String batchNo;

    /**
     * 承运人
     */
    private String transporter;

    /**
     * 车牌号
     */
    private String vehicleNo;

    /**
     * 实际入库/出库数量
     */
    private BigDecimal actualQuantity;

    /**
     * 差异数量
     */
    private BigDecimal differenceQuantity;

    /**
     * 操作备注
     */
    private String operationRemark;

    /**
     * 状态变更历史(JSON格式)
     */
    private String statusHistory;
}
