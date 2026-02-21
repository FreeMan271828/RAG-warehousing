package com.tobacco.warehouse.modules.equipment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.equipment.entity.DeviceModel;
import com.tobacco.warehouse.modules.equipment.mapper.DeviceModelMapper;
import com.tobacco.warehouse.modules.equipment.service.DeviceModelService;
import org.springframework.stereotype.Service;

/**
 * 设备型号 Service实现类
 *
 * @author warehouse
 */
@Service
public class DeviceModelServiceImpl extends ServiceImpl<DeviceModelMapper, DeviceModel> implements DeviceModelService {
}
