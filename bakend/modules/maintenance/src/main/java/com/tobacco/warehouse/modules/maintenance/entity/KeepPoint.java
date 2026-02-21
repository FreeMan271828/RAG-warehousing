package com.tobacco.warehouse.modules.maintenance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 保养点 实体类
 * 
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("keep_point")
public class KeepPoint extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 设备类别ID
     */
    private Long categoryId;

    /**
     * 保养点名称
     */
    private String pointName;

    /**
     * 保养点编码
     */
    private String pointCode;

    /**
     * 检查内容
     */
    private String checkContent;

    /**
     * 检查标准
     */
    private String checkStandard;

    /**
     * 检查方法
     */
    private String checkMethod;

    /**
     * 保养周期：daily/weekly/monthly/quarterly/yearly
     */
    private String cycleType;

    /**
     * 周期天数
     */
    private Integer cycleDays;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
