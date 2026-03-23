package com.tobacco.warehouse.modules.warehouse.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AGV状态枚举
 * 
 * @author warehouse
 */
@Getter
@AllArgsConstructor
public enum AgvStatusEnum {

    IDLE("idle", "空闲", "success"),
    WORKING("working", "工作中", "primary"),
    CHARGING("charging", "充电中", "warning"),
    RETURNING("returning", "返回中", "warning"),
    FAULT("fault", "故障", "danger");

    /**
     * 状态编码
     */
    private final String code;
    
    /**
     * 状态名称
     */
    private final String name;
    
    /**
     * 显示颜色
     */
    private final String color;

    /**
     * 根据编码获取枚举
     */
    public static AgvStatusEnum fromCode(String code) {
        for (AgvStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
