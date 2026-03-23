package com.tobacco.warehouse.modules.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.equipment.entity.Equipment;
import com.tobacco.warehouse.modules.equipment.service.EquipmentService;
import com.tobacco.warehouse.modules.warehouse.entity.AgvStatus;
import com.tobacco.warehouse.modules.warehouse.entity.AgvTask;
import com.tobacco.warehouse.modules.warehouse.entity.ChargingRecord;
import com.tobacco.warehouse.modules.warehouse.entity.ChargingStation;
import com.tobacco.warehouse.modules.warehouse.enums.AgvStatusEnum;
import com.tobacco.warehouse.modules.warehouse.mapper.AgvStatusMapper;
import com.tobacco.warehouse.modules.warehouse.mapper.AgvTaskMapper;
import com.tobacco.warehouse.modules.warehouse.mapper.ChargingStationMapper;
import com.tobacco.warehouse.modules.warehouse.service.AgvStatusService;
import com.tobacco.warehouse.modules.warehouse.service.ChargingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AGV状态 服务实现类
 * 
 * @author warehouse
 */
@Service
public class AgvStatusServiceImpl extends ServiceImpl<AgvStatusMapper, AgvStatus> implements AgvStatusService {

    @Autowired
    private ChargingRecordService chargingRecordService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private AgvTaskMapper agvTaskMapper;

    @Autowired
    private ChargingStationMapper chargingStationMapper;

    // 低电量阈值
    private static final BigDecimal LOW_BATTERY_THRESHOLD = new BigDecimal("20");
    // 最低电量阈值
    private static final BigDecimal MIN_BATTERY_THRESHOLD = new BigDecimal("10");

    @Override
    public List<AgvStatus> listAgvWithEquipment() {
        // 查询所有AGV状态
        List<AgvStatus> agvStatusList = list();
        
        // 获取所有AGV的ID列表
        List<Long> agvIds = agvStatusList.stream()
                .map(AgvStatus::getAgvId)
                .collect(Collectors.toList());
        
        if (agvIds.isEmpty()) {
            return agvStatusList;
        }
        
        // 批量查询设备信息
        Map<Long, Equipment> equipmentMap = equipmentService.listByIds(agvIds).stream()
                .collect(Collectors.toMap(Equipment::getId, e -> e));
        
        // 填充设备名称和编号
        for (AgvStatus agvStatus : agvStatusList) {
            Equipment equipment = equipmentMap.get(agvStatus.getAgvId());
            if (equipment != null) {
                agvStatus.setEquipmentName(equipment.getEquipmentName());
                agvStatus.setEquipmentCode(equipment.getEquipmentCode());
            }
        }
        
        return agvStatusList;
    }

    @Override
    public AgvStatus getByAgvId(Long agvId) {
        return getOne(new LambdaQueryWrapper<AgvStatus>()
                .eq(AgvStatus::getAgvId, agvId));
    }

    @Override
    public boolean updateBatteryLevel(Long agvId, BigDecimal batteryLevel) {
        AgvStatus agvStatus = getByAgvId(agvId);
        if (agvStatus == null) {
            return false;
        }
        
        // 限制电量范围
        if (batteryLevel.compareTo(BigDecimal.ZERO) < 0) {
            batteryLevel = BigDecimal.ZERO;
        }
        if (batteryLevel.compareTo(new BigDecimal("100")) > 0) {
            batteryLevel = new BigDecimal("100");
        }
        
        agvStatus.setBatteryLevel(batteryLevel);
        agvStatus.setLastUpdateTime(LocalDateTime.now());
        
        // 检查是否需要自动充电
        if (needsCharging(agvId) && !AgvStatusEnum.CHARGING.getCode().equals(agvStatus.getAgvStatus())) {
            // 自动进入返回状态
            agvStatus.setAgvStatus(AgvStatusEnum.RETURNING.getCode());
        }
        
        return updateById(agvStatus);
    }

    @Override
    public boolean updateAgvStatus(Long agvId, String status) {
        AgvStatus agvStatus = getByAgvId(agvId);
        if (agvStatus == null) {
            return false;
        }
        
        agvStatus.setAgvStatus(status);
        agvStatus.setLastUpdateTime(LocalDateTime.now());
        return updateById(agvStatus);
    }

    @Override
    @Transactional
    public boolean startCharging(Long agvId, Long stationId) {
        AgvStatus agvStatus = getByAgvId(agvId);
        if (agvStatus == null) {
            return false;
        }

        // 获取充电站信息
        ChargingStation station = chargingStationMapper.selectById(stationId);
        if (station == null) {
            return false;
        }

        // 获取可用的充电口
        String chargingPort = getAvailableChargingPort(stationId);

        // 创建充电任务
        AgvTask chargeTask = new AgvTask();
        chargeTask.setTaskCode("CHARGE-" + System.currentTimeMillis());
        chargeTask.setTaskType("charging");
        chargeTask.setAgvId(agvId);
        chargeTask.setPriority(10);
        chargeTask.setFromLocation(agvStatus.getCurrentLocation() != null ? agvStatus.getCurrentLocation() : "unknown");
        chargeTask.setToLocation(station.getStationCode() + "-" + chargingPort);
        chargeTask.setTaskStatus("executing");
        chargeTask.setStartTime(LocalDateTime.now());
        agvTaskMapper.insert(chargeTask);

        // 创建充电记录
        ChargingRecord record = new ChargingRecord();
        record.setAgvId(agvId);
        record.setStationId(stationId);
        record.setStartTime(LocalDateTime.now());
        record.setStartBattery(agvStatus.getBatteryLevel());
        record.setChargeType("auto");
        chargingRecordService.save(record);

        // 更新小车状态为"返回中"，等到达充电站后再改为"充电中"
        agvStatus.setAgvStatus(AgvStatusEnum.RETURNING.getCode());
        agvStatus.setCurrentTaskId(chargeTask.getId());
        agvStatus.setTargetLocation(chargeTask.getToLocation());
        agvStatus.setChargingStationId(stationId);
        agvStatus.setChargingPort(chargingPort);
        agvStatus.setLastChargeTime(LocalDateTime.now());
        agvStatus.setLastUpdateTime(LocalDateTime.now());

        return updateById(agvStatus);
    }

    /**
     * 获取可用的充电口
     */
    private String getAvailableChargingPort(Long stationId) {
        ChargingStation station = chargingStationMapper.selectById(stationId);
        if (station == null) {
            return "top_port";
        }

        // 获取当前正在使用的端口
        AgvStatus agvStatus = getOne(new LambdaQueryWrapper<AgvStatus>()
                .eq(AgvStatus::getChargingStationId, stationId)
                .ne(AgvStatus::getAgvStatus, "charging")
                .orderByDesc(AgvStatus::getLastUpdateTime)
                .last("LIMIT 1"));

        // 从数据库字段中获取可用的端口
        String[] ports = new String[]{};
        if (station.getTopPort() != null) ports = addPort(ports, station.getTopPort());
        if (station.getBottomPort() != null) ports = addPort(ports, station.getBottomPort());
        if (station.getLeftPort() != null) ports = addPort(ports, station.getLeftPort());
        if (station.getRightPort() != null) ports = addPort(ports, station.getRightPort());

        if (ports.length == 0) {
            return "top_port";
        }

        // 轮转到下一个可用端口
        if (agvStatus != null && agvStatus.getChargingPort() != null) {
            for (int i = 0; i < ports.length; i++) {
                if (ports[i].equals(agvStatus.getChargingPort())) {
                    return ports[(i + 1) % ports.length];
                }
            }
        }
        return ports[0];
    }

    private String[] addPort(String[] arr, String port) {
        String[] result = new String[arr.length + 1];
        System.arraycopy(arr, 0, result, 0, arr.length);
        result[arr.length] = port;
        return result;
    }

    @Override
    @Transactional
    public boolean endCharging(Long agvId, String result, String interruptReason) {
        AgvStatus agvStatus = getByAgvId(agvId);
        if (agvStatus == null) {
            return false;
        }
        
        // 查找最近的充电记录
        ChargingRecord record = chargingRecordService.getLatestChargingRecord(agvId);
        if (record != null && record.getEndTime() == null) {
            record.setEndTime(LocalDateTime.now());
            record.setEndBattery(agvStatus.getBatteryLevel());
            record.setResult(result);
            record.setInterruptReason(interruptReason);
            
            // 计算充电时长
            if (record.getStartTime() != null) {
                long seconds = java.time.Duration.between(record.getStartTime(), record.getEndTime()).getSeconds();
                record.setChargingDuration((int) seconds);
                
                // 计算用电量 (功率2kW)
                BigDecimal hours = new BigDecimal(seconds).divide(new BigDecimal("3600"), 4, java.math.RoundingMode.HALF_UP);
                record.setElectricityUsed(hours.multiply(new BigDecimal("2")));
            }
            
            chargingRecordService.updateById(record);
        }
        
        // 更新小车状态为空闲
        agvStatus.setAgvStatus(AgvStatusEnum.IDLE.getCode());
        agvStatus.setLastUpdateTime(LocalDateTime.now());

        // 更新充电任务为已完成
        if (agvStatus.getCurrentTaskId() != null) {
            AgvTask task = agvTaskMapper.selectById(agvStatus.getCurrentTaskId());
            if (task != null && "charging".equals(task.getTaskType())) {
                task.setTaskStatus("completed");
                task.setEndTime(LocalDateTime.now());
                if (task.getStartTime() != null) {
                    task.setActualDuration((int) java.time.Duration.between(task.getStartTime(), task.getEndTime()).getSeconds());
                }
                agvTaskMapper.updateById(task);
            }
        }

        return updateById(agvStatus);
    }

    @Override
    public List<AgvStatus> getAvailableAgvs() {
        // 电量 >= 20% 且状态为空闲的小车
        return list(new LambdaQueryWrapper<AgvStatus>()
                .ge(AgvStatus::getBatteryLevel, LOW_BATTERY_THRESHOLD)
                .eq(AgvStatus::getAgvStatus, AgvStatusEnum.IDLE.getCode()));
    }

    @Override
    public boolean needsCharging(Long agvId) {
        AgvStatus agvStatus = getByAgvId(agvId);
        if (agvStatus == null) {
            return false;
        }
        
        // 电量低于20%需要充电，低于10%强制停止工作
        return agvStatus.getBatteryLevel().compareTo(LOW_BATTERY_THRESHOLD) < 0;
    }
}
