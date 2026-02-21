package com.tobacco.warehouse.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 设备管理 实体类
 *
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("equipment")
public class Equipment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 设备编号
     */
    private String equipmentCode;

    /**
     * 设备名称
     */
    private String equipmentName;

    /**
     * 设备型号ID
     */
    private Long modelId;

    /**
     * 父设备ID
     */
    private Long parentId;

    /**
     * 安装位置
     */
    private String location;

    /**
     * 序列号
     */
    private String serialNumber;

    /**
     * 采购日期
     */
    private java.time.LocalDate purchaseDate;

    /**
     * 保修到期日期
     */
    private java.time.LocalDate warrantyExpireDate;

    /**
     * 安装日期
     */
    private java.time.LocalDate installDate;

    /**
     * 状态：0-停用 1-运行中 2-维修中 3-故障
     */
    private Integer status;

    /**
     * 是否启用：0-否 1-是
     */
    private Integer isEnabled;

    /**
     * X坐标(3D)
     */
    private BigDecimal xPosition;

    /**
     * Y坐标(3D)
     */
    private BigDecimal yPosition;

    /**
     * Z坐标(3D)
     */
    private BigDecimal zPosition;

    /**
     * 描述
     */
    private String description;
}
