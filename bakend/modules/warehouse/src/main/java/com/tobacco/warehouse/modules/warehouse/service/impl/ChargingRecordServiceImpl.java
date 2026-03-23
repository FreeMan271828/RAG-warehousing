package com.tobacco.warehouse.modules.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.warehouse.entity.ChargingRecord;
import com.tobacco.warehouse.modules.warehouse.mapper.ChargingRecordMapper;
import com.tobacco.warehouse.modules.warehouse.service.ChargingRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 充电记录 服务实现类
 * 
 * @author warehouse
 */
@Service
public class ChargingRecordServiceImpl extends ServiceImpl<ChargingRecordMapper, ChargingRecord> implements ChargingRecordService {

    @Override
    public ChargingRecord getLatestChargingRecord(Long agvId) {
        return getOne(new LambdaQueryWrapper<ChargingRecord>()
                .eq(ChargingRecord::getAgvId, agvId)
                .orderByDesc(ChargingRecord::getStartTime)
                .last("LIMIT 1"));
    }

    @Override
    public List<ChargingRecord> listByAgvId(Long agvId) {
        return list(new LambdaQueryWrapper<ChargingRecord>()
                .eq(ChargingRecord::getAgvId, agvId)
                .orderByDesc(ChargingRecord::getStartTime));
    }

    @Override
    public List<ChargingRecord> listByStationId(Long stationId) {
        return list(new LambdaQueryWrapper<ChargingRecord>()
                .eq(ChargingRecord::getStationId, stationId)
                .orderByDesc(ChargingRecord::getStartTime));
    }
}
