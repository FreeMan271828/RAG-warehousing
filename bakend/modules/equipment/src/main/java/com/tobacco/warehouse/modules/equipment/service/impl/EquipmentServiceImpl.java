package com.tobacco.warehouse.modules.equipment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.equipment.entity.Equipment;
import com.tobacco.warehouse.modules.equipment.mapper.EquipmentMapper;
import com.tobacco.warehouse.modules.equipment.service.EquipmentService;
import org.springframework.stereotype.Service;

/**
 * 设备管理 Service实现类
 *
 * @author warehouse
 */
@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {
}
