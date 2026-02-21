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
 * 保养计划 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("maintenance_plan")
public class MaintenancePlan extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 计划编号
     */
    private String planCode;

    /**
     * 设备ID
     */
    private Long equipmentId;

    /**
     * 计划类型：保养/维修
     */
    private String planType;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 计划内容
     */
    private String planContent;

    /**
     * 周期类型
     */
    private String cycleType;

    /**
     * 周期天数
     */
    private Integer cycleDays;

    /**
     * 计划开始日期
     */
    private LocalDate startDate;

    /**
     * 计划结束日期
     */
    private LocalDate endDate;

    /**
     * 负责人ID
     */
    private Long responsibleUser;

    /**
     * 预计工时
     */
    private BigDecimal estimatedHours;

    /**
     * 状态：0-已取消 1-待执行 2-执行中 3-已完成
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Long createBy;
}
