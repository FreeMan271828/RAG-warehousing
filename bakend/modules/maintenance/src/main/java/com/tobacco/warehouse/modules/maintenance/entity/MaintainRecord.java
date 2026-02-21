package com.tobacco.warehouse.modules.maintenance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备维修记录 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("maintain_record")
public class MaintainRecord extends BaseEntity {

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
     * 故障类型
     */
    private String faultType;

    /**
     * 故障描述
     */
    @NotBlank(message = "故障描述不能为空")
    private String faultDesc;

    /**
     * 故障等级：1-一般 2-重要 3-紧急
     */
    private Integer faultLevel;

    /**
     * 故障发生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime occurTime;

    /**
     * 报修人ID
     */
    private Long reportUser;

    /**
     * 分配人ID
     */
    private Long assignUser;

    /**
     * 维修人ID
     */
    private Long maintainUser;

    /**
     * 接单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime receiveTime;

    /**
     * 开始维修时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 维修完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 维修工时
     */
    private BigDecimal repairHours;

    /**
     * 故障原因
     */
    private String faultCause;

    /**
     * 解决方案
     */
    private String solution;

    /**
     * 维修费用
     */
    private BigDecimal costAmount;

    /**
     * 维修结果：已修复/未修复/待处理
     */
    private String result;

    /**
     * 状态：1-待分配 2-已分配 3-维修中 4-已完成 5-已关闭
     */
    private Integer status;
}
