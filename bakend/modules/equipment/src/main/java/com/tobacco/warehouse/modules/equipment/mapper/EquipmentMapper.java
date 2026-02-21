package com.tobacco.warehouse.modules.equipment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.equipment.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备管理 Mapper接口
 *
 * @author warehouse
 */
@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {
}
