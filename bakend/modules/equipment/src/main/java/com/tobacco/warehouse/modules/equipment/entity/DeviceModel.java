package com.tobacco.warehouse.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备型号 实体类
 *
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_model")
public class DeviceModel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 型号编码
     */
    private String modelCode;

    /**
     * 型号名称
     */
    private String modelName;

    /**
     * 设备类别ID
     */
    private Long categoryId;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 使用年限
     */
    private Integer usefulLife;

    /**
     * 库存预警天数
     */
    private Integer warningDays;

    /**
     * 规格参数
     */
    private String specification;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
