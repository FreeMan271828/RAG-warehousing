package com.tobacco.warehouse.modules.warehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.warehouse.entity.ChargingStation;
import com.tobacco.warehouse.modules.warehouse.mapper.ChargingStationMapper;
import com.tobacco.warehouse.modules.warehouse.service.ChargingStationService;
import org.springframework.stereotype.Service;

/**
 * 充电站 服务实现类
 * 
 * @author warehouse
 */
@Service
public class ChargingStationServiceImpl extends ServiceImpl<ChargingStationMapper, ChargingStation> implements ChargingStationService {
}
