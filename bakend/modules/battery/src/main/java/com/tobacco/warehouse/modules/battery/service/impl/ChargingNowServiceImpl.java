package com.tobacco.warehouse.modules.battery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.battery.entity.ChargingNow;
import com.tobacco.warehouse.modules.battery.mapper.ChargingNowMapper;
import com.tobacco.warehouse.modules.battery.service.ChargingNowService;
import org.springframework.stereotype.Service;

/**
 * 充电实时记录 Service 実装
 * 
 * @author warehouse
 */
@Service
public class ChargingNowServiceImpl extends ServiceImpl<ChargingNowMapper, ChargingNow> implements ChargingNowService {
}
