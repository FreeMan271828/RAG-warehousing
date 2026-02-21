package com.tobacco.warehouse.modules.maintenance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 零件更换记录 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("used_element")
public class UsedElement extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 记录编号
     */
    private String recordCode;

    /**
     * 设备ID
     */
    private Long equipmentId;

    /**
     * 零件ID
     */
    private Long elementId;

    /**
     * 维修记录ID
     */
    private Long maintainRecordId;

    /**
     * 更换数量
     */
    private Integer replaceQuantity;

    /**
     * 更换日期
     */
    private LocalDate replaceDate;

    /**
     * 更换原因
     */
    private String replaceReason;

    /**
     * 费用
     */
    private BigDecimal costAmount;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 备注
     */
    private String remark;
}
