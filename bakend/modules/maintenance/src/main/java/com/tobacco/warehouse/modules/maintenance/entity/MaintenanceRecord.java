package com.tobacco.warehouse.modules.maintenance.entity;

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
 * 保养记录 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("maintenance_record")
public class MaintenanceRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 记录编号
     */
    private String recordCode;

    /**
     * 保养计划ID
     */
    private Long planId;

    /**
     * 设备ID
     */
    private Long equipmentId;

    /**
     * 保养类型
     */
    private String maintenanceType;

    /**
     * 执行日期
     */
    private LocalDate executeDate;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 实际工时
     */
    private BigDecimal actualHours;

    /**
     * 执行人ID
     */
    private Long executorId;

    /**
     * 检查点情况(JSON)
     */
    private String checkPoints;

    /**
     * 执行结果：正常/异常
     */
    private String result;

    /**
     * 问题描述
     */
    private String problemDesc;

    /**
     * 解决方案
     */
    private String solution;

    /**
     * 附件(JSON数组)
     */
    private String attachments;

    /**
     * 状态：0-已取消 1-进行中 2-已完成 3-已超时
     */
    private Integer status;
}
