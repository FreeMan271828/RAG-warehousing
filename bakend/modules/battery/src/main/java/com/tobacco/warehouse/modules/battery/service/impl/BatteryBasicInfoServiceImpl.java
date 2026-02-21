package com.tobacco.warehouse.modules.battery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.battery.entity.BatteryBasicInfo;
import com.tobacco.warehouse.modules.battery.mapper.BatteryBasicInfoMapper;
import com.tobacco.warehouse.modules.battery.service.BatteryBasicInfoService;
import org.springframework.stereotype.Service;

/**
 * 电池基本信息 Service 実装
 * 
 * @author warehouse
 */
@Service
public class BatteryBasicInfoServiceImpl extends ServiceImpl<BatteryBasicInfoMapper, BatteryBasicInfo> implements BatteryBasicInfoService {
}
