package com.tobacco.warehouse.modules.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tobacco.warehouse.modules.warehouse.entity.ChargingRecord;

import java.util.List;

/**
 * 充电记录 服务接口
 * 
 * @author warehouse
 */
public interface ChargingRecordService extends IService<ChargingRecord> {

    /**
     * 获取最近的充电记录
     */
    ChargingRecord getLatestChargingRecord(Long agvId);

    /**
     * 获取小车的充电历史
     */
    List<ChargingRecord> listByAgvId(Long agvId);

    /**
     * 获取充电站的充电记录
     */
    List<ChargingRecord> listByStationId(Long stationId);
}
