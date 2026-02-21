package com.tobacco.warehouse.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备类别 实体类
 *
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_category")
public class DeviceCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 类别编码
     */
    private String categoryCode;

    /**
     * 类别名称
     */
    private String categoryName;

    /**
     * 父类别ID
     */
    private Long parentId;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
