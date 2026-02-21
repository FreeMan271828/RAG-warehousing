package com.tobacco.warehouse.modules.equipment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.equipment.entity.DeviceCategory;
import com.tobacco.warehouse.modules.equipment.mapper.DeviceCategoryMapper;
import com.tobacco.warehouse.modules.equipment.service.DeviceCategoryService;
import org.springframework.stereotype.Service;

/**
 * 设备类别 Service实现类
 *
 * @author warehouse
 */
@Service
public class DeviceCategoryServiceImpl extends ServiceImpl<DeviceCategoryMapper, DeviceCategory> implements DeviceCategoryService {
}
