package com.tobacco.warehouse.modules.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tobacco.warehouse.modules.warehouse.entity.*;
import com.tobacco.warehouse.modules.warehouse.enums.AgvStatusEnum;
import com.tobacco.warehouse.modules.warehouse.entity.ChargingRecord;
import com.tobacco.warehouse.modules.warehouse.entity.ChargingStation;
import com.tobacco.warehouse.modules.battery.entity.ChargingNow;
import com.tobacco.warehouse.modules.warehouse.mapper.AgvStatusMapper;
import com.tobacco.warehouse.modules.warehouse.mapper.AgvTaskMapper;
import com.tobacco.warehouse.modules.warehouse.mapper.ChargingRecordMapper;
import com.tobacco.warehouse.modules.warehouse.mapper.ChargingStationMapper;
import com.tobacco.warehouse.modules.battery.mapper.ChargingNowMapper;
import com.tobacco.warehouse.modules.warehouse.service.AgvSchedulerService;
import com.tobacco.warehouse.modules.warehouse.service.InOutOrderService;
import com.tobacco.warehouse.modules.warehouse.strategy.PathPlanningStrategy;
import com.tobacco.warehouse.modules.warehouse.strategy.PathPlanningStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * AGV任务调度器 服务实现类
 * 
 * @author warehouse
 */
@Slf4j
@Service
public class AgvSchedulerServiceImpl implements AgvSchedulerService {

    @Autowired
    private AgvStatusMapper agvStatusMapper;

    @Autowired
    private AgvTaskMapper agvTaskMapper;

    @Autowired
    private ChargingStationMapper chargingStationMapper;
    
    @Autowired
    private ChargingRecordMapper chargingRecordMapper;
    
    @Autowired
    private ChargingNowMapper chargingNowMapper;

    @Autowired
    private InOutOrderService inOutOrderService;

    @Autowired
    private PathPlanningStrategyFactory pathPlanningStrategyFactory;

    /**
     * 默认移动速度 m/s
     */
    private static final BigDecimal DEFAULT_MOVE_SPEED = new BigDecimal("1.0");

    /**
     * 默认每秒电量消耗百分比
     */
    private static final BigDecimal DEFAULT_BATTERY_CONSUMPTION = new BigDecimal("0.1");

    /**
     * 默认转弯额外耗时(秒)
     */
    private static final BigDecimal DEFAULT_TURN_OVERHEAD = new BigDecimal("0.5");

    /**
     * 默认预警电量
     */
    private static final BigDecimal DEFAULT_WARNING_BATTERY = new BigDecimal("20.00");

    /**
     * 仓库中心点坐标（用于计算距离）
     */
    private static final double WAREHOUSE_CENTER_X = 0;
    private static final double WAREHOUSE_CENTER_Y = 0;

    @Override
    public void schedule() {
        try {
            // 1. 更新小车电量
            updateBatteryLevels();

            // 2. 检查小车是否需要返回充电
            checkAndReturnToCharge();

            // 3. 分配任务给小车
            assignTasksToAgvs();

            // 4. 执行任务 - 让小车移动
            executeTasks();
        } catch (Exception e) {
            log.error("AGV调度执行失败", e);
        }
    }

    /**
     * 执行任务 - 让小车向目标位置移动
     */
    private void executeTasks() {
        // 获取所有正在执行任务的小车（状态为 working, returning, charging, fault 或有任务ID的）
        // 注意：故障状态下的小车如果有返回充电任务也要执行
        List<AgvStatus> executingAgvs = agvStatusMapper.selectList(
            new LambdaQueryWrapper<AgvStatus>()
                .and(wrapper -> wrapper
                        .eq(AgvStatus::getAgvStatus, AgvStatusEnum.WORKING.getCode())
                        .or()
                        .eq(AgvStatus::getAgvStatus, AgvStatusEnum.RETURNING.getCode())
                        .or()
                        .eq(AgvStatus::getAgvStatus, AgvStatusEnum.CHARGING.getCode())
                        .or()
                        .eq(AgvStatus::getAgvStatus, AgvStatusEnum.FAULT.getCode())
                        .or()
                        .isNotNull(AgvStatus::getCurrentTaskId)
                )
        );

        if (executingAgvs.isEmpty()) {
            log.debug("没有正在执行任务的小车");
            return;
        }

        log.debug("有{}个小车正在执行任务", executingAgvs.size());

        for (AgvStatus agvStatus : executingAgvs) {
            if (agvStatus.getCurrentTaskId() == null) {
                continue;
            }

            AgvTask task = agvTaskMapper.selectById(agvStatus.getCurrentTaskId());
            if (task == null || !"executing".equals(task.getTaskStatus())) {
                continue;
            }

            // 解析目标位置
            String targetLocation = task.getToLocation();
            if (targetLocation == null) {
                continue;
            }

            // 计算目标坐标
            double[] targetCoords = parseLocationCode(targetLocation);
            double targetX = targetCoords[0];
            double targetY = targetCoords[1];

            // 获取当前位置（如果坐标为0或null，尝试从currentLocation解析）
            double currentX = agvStatus.getXPosition() != null ? agvStatus.getXPosition().doubleValue() : 0;
            double currentY = agvStatus.getYPosition() != null ? agvStatus.getYPosition().doubleValue() : 0;
            
            // 如果当前位置为0，尝试从currentLocation解析
            if (currentX == 0 && currentY == 0 && agvStatus.getCurrentLocation() != null) {
                double[] currentCoords = parseLocationCode(agvStatus.getCurrentLocation());
                currentX = currentCoords[0];
                currentY = currentCoords[1];
            }

            // 计算距离
            double distance = calculateDistance(currentX, currentY, targetX, targetY);

            log.info("小车{} 执行任务: 当前位置({:.2f},{:.2f}) 目标{} ({:.2f},{:.2f}) 距离{:.2f}", 
                agvStatus.getAgvId(), currentX, currentY, targetLocation, targetX, targetY, distance);

            // 如果已经到达目标位置
            if (distance < 0.5) {
                // 完成任务
                completeTask(agvStatus, task);
                continue;
            }

            // 使用路径策略移动小车
            moveAgvWithStrategy(agvStatus, currentX, currentY, targetX, targetY);
        }
    }
    
    /**
     * 使用路径策略移动小车
     */
    private void moveAgvWithStrategy(AgvStatus agvStatus, double currentX, double currentY, double targetX, double targetY) {
        // 获取路径规划策略
        PathPlanningStrategy strategy = pathPlanningStrategyFactory.getCurrentStrategy();
        
        // 规划路径
        List<double[]> path = strategy.planPath(currentX, currentY, targetX, targetY);
        
        if (path == null || path.isEmpty()) {
            log.warn("路径规划失败，小车{}无法移动", agvStatus.getAgvId());
            return;
        }
        
        // 获取下一步位置（第一个点或最后一个点）
        double nextX, nextY;
        if (path.size() == 1) {
            nextX = targetX;
            nextY = targetY;
        } else {
            // 第二个点作为下一步（避免原地踏步）
            nextX = path.get(1)[0];
            nextY = path.get(1)[1];
        }
        
        // 更新小车位置
        agvStatus.setXPosition(BigDecimal.valueOf(nextX));
        agvStatus.setYPosition(BigDecimal.valueOf(nextY));
        agvStatus.setLastUpdateTime(LocalDateTime.now());
        agvStatus.setCurrentLocation(String.format("%.0f-%.0f", nextX, nextY));
        
        agvStatusMapper.updateById(agvStatus);
        
        log.debug("小车{}从({:.2f},{:.2f})移动到({:.2f},{:.2f})", 
            agvStatus.getAgvId(), currentX, currentY, nextX, nextY);
    }

    /**
     * 解析位置代码为坐标
     * 例如: A-01-003 -> (0, 2), CS-001 -> (0, 0), B-03 -> (1, 2)
     */
    private double[] parseLocationCode(String locationCode) {
        if (locationCode == null || locationCode.isEmpty()) {
            log.warn("位置代码为空，返回默认坐标(0,0)");
            return new double[]{0, 0};
        }
        
        // 优先检查绝对直角坐标映射（最精确）
        double[] absoluteCoords = getAbsoluteCoordinate(locationCode);
        if (absoluteCoords != null) {
            log.debug("通过绝对坐标映射解析 {} -> ({}, {})", locationCode, absoluteCoords[0], absoluteCoords[1]);
            return absoluteCoords;
        }
        
        // 充电站位置解析 - 支持 CS-001, CS-001-top, CS-001-bottom 等格式
        if (locationCode.startsWith("CS-")) {
            // 提取充电站代码，如 "CS-001" 从 "CS-001-top" 中提取
            String stationCode = locationCode;
            if (locationCode.contains("-")) {
                // 提取前缀部分，如 CS-001 从 CS-001-top
                String[] parts = locationCode.split("-");
                if (parts.length >= 2) {
                    stationCode = parts[0] + "-" + parts[1];
                }
            }
            
            ChargingStation station = chargingStationMapper.selectOne(
                new LambdaQueryWrapper<ChargingStation>()
                    .eq(ChargingStation::getStationCode, stationCode)
            );
            if (station != null) {
                double baseX = station.getXPosition() != null ? station.getXPosition().doubleValue() : 0;
                double baseY = station.getYPosition() != null ? station.getYPosition().doubleValue() : 0;
                
                // 根据充电口类型调整坐标
                if (locationCode.contains("-top")) {
                    return new double[]{baseX, baseY + 2};    // 上充电口
                } else if (locationCode.contains("-bottom")) {
                    return new double[]{baseX, baseY - 2};    // 下充电口
                } else if (locationCode.contains("-left")) {
                    return new double[]{baseX - 2, baseY};    // 左充电口
                } else if (locationCode.contains("-right")) {
                    return new double[]{baseX + 2, baseY};   // 右充电口
                }
                
                return new double[]{baseX, baseY};
            }
            return new double[]{0, 0};
        }

        // 货位位置解析 A-01-003 格式: 区域-排-列
        try {
            String[] parts = locationCode.split("-");
            if (parts.length >= 3) {
                char area = parts[0].charAt(0); // A=0, B=1, C=2...
                int row = Integer.parseInt(parts[1]) - 1; // 01 -> 0
                int col = Integer.parseInt(parts[2]) - 1; // 003 -> 2

                // 每个区域间隔 10 个单位
                double x = (area - 'A') * 10 + row;
                double y = col;
                return new double[]{x, y};
            }
        } catch (Exception e) {
            log.warn("无法解析位置代码: {}", locationCode);
        }

        return new double[]{0, 0};
    }
    
    /**
     * 获取绝对直角坐标映射
     * 用于精确的位置解析，不依赖于动态计算
     */
    private double[] getAbsoluteCoordinate(String locationCode) {
        // 使用静态Map缓存绝对坐标，避免重复查询
        if (absoluteCoordinateMap == null) {
            initializeAbsoluteCoordinates();
        }
        
        // 优先精确匹配（如 CS-001-top）
        if (absoluteCoordinateMap.containsKey(locationCode)) {
            return absoluteCoordinateMap.get(locationCode);
        }
        
        // 前缀匹配 - 需要先匹配更长的key（如 CS-001-top 优先于 CS-001）
        // 将map的key按长度降序排序
        List<String> keys = new ArrayList<>(absoluteCoordinateMap.keySet());
        keys.sort((a, b) -> Integer.compare(b.length(), a.length()));
        
        for (String key : keys) {
            if (locationCode.startsWith(key)) {
                return absoluteCoordinateMap.get(key);
            }
        }
        
        return null;
    }
    
    /**
     * 初始化绝对直角坐标映射表
     * 这些是仓库中所有关键位置的绝对坐标
     */
    private void initializeAbsoluteCoordinates() {
        absoluteCoordinateMap = new HashMap<>();
        
        // ==================== 充电站坐标 ====================
        // CS-001 及其四个充电口
        absoluteCoordinateMap.put("CS-001", new double[]{0.0, 0.0});
        absoluteCoordinateMap.put("CS-001-top", new double[]{0.0, 2.0});
        absoluteCoordinateMap.put("CS-001-bottom", new double[]{0.0, -2.0});
        absoluteCoordinateMap.put("CS-001-left", new double[]{-2.0, 0.0});
        absoluteCoordinateMap.put("CS-001-right", new double[]{2.0, 0.0});
        
        // CS-002 及其四个充电口
        absoluteCoordinateMap.put("CS-002", new double[]{20.0, 0.0});
        absoluteCoordinateMap.put("CS-002-top", new double[]{20.0, 2.0});
        absoluteCoordinateMap.put("CS-002-bottom", new double[]{20.0, -2.0});
        absoluteCoordinateMap.put("CS-002-left", new double[]{18.0, 0.0});
        absoluteCoordinateMap.put("CS-002-right", new double[]{22.0, 0.0});
        
        // ==================== 入库口坐标 ====================
        absoluteCoordinateMap.put("ENTRY-TOP", new double[]{0.0, -25.0});
        absoluteCoordinateMap.put("ENTRY-BOTTOM", new double[]{0.0, 25.0});
        absoluteCoordinateMap.put("ENTRY-LEFT", new double[]{-35.0, 0.0});
        absoluteCoordinateMap.put("ENTRY-RIGHT", new double[]{35.0, 0.0});
        
        // ==================== A区货位坐标 (x: 0-9, y: 0-20) ====================
        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 20; col++) {
                String code = String.format("A-%02d-%03d", row, col);
                double x = (row - 1);      // 0-9
                double y = (col - 1) * 1.5; // 0-28.5，每列间隔1.5
                absoluteCoordinateMap.put(code, new double[]{x, y});
            }
        }
        
        // ==================== B区货位坐标 (x: 10-19, y: 0-20) ====================
        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 20; col++) {
                String code = String.format("B-%02d-%03d", row, col);
                double x = 10 + (row - 1);      // 10-19
                double y = (col - 1) * 1.5;     // 0-28.5
                absoluteCoordinateMap.put(code, new double[]{x, y});
            }
        }
        
        // ==================== C区货位坐标 (x: 20-29, y: 0-20) ====================
        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 20; col++) {
                String code = String.format("C-%02d-%03d", row, col);
                double x = 20 + (row - 1);      // 20-29
                double y = (col - 1) * 1.5;     // 0-28.5
                absoluteCoordinateMap.put(code, new double[]{x, y});
            }
        }
        
        // ==================== D区货位坐标 (x: 30-39, y: 0-20) ====================
        for (int row = 1; row <= 10; row++) {
            for (int col = 1; col <= 20; col++) {
                String code = String.format("D-%02d-%03d", row, col);
                double x = 30 + (row - 1);      // 30-39
                double y = (col - 1) * 1.5;     // 0-28.5
                absoluteCoordinateMap.put(code, new double[]{x, y});
            }
        }
        
        log.info("绝对坐标映射表已初始化，共{}个位置", absoluteCoordinateMap.size());
    }
    
    // 绝对坐标映射表
    private Map<String, double[]> absoluteCoordinateMap;

    /**
     * 完成任务
     */
    private void completeTask(AgvStatus agvStatus, AgvTask task) {
        task.setTaskStatus("completed");
        task.setEndTime(LocalDateTime.now());
        if (task.getStartTime() != null) {
            task.setActualDuration((int) java.time.Duration.between(task.getStartTime(), task.getEndTime()).getSeconds());
        }
        agvTaskMapper.updateById(task);

        // 如果是充电任务，完成后进入充电状态
        if ("charging".equals(task.getTaskType())) {
            agvStatus.setAgvStatus(AgvStatusEnum.CHARGING.getCode());
            // 开始实时充电监控
            startChargingNow(agvStatus);
        } else {
            agvStatus.setAgvStatus(AgvStatusEnum.IDLE.getCode());
        }

        agvStatus.setCurrentTaskId(null);
        agvStatus.setTargetLocation(null);
        agvStatus.setLastUpdateTime(LocalDateTime.now());
        agvStatusMapper.updateById(agvStatus);

        log.info("小车{}完成任务{}，状态变为{}",
            agvStatus.getAgvId(), task.getTaskCode(), agvStatus.getAgvStatus());
    }

    /**
     * 更新小车电量
     * - 非充电状态：每秒消耗电量
     * - 充电状态：每秒增加电量
     * - 故障状态（电量耗尽）：电量慢慢恢复
     */
    private void updateBatteryLevels() {
        List<AgvStatus> agvStatuses = agvStatusMapper.selectList(null);
        
        for (AgvStatus agvStatus : agvStatuses) {
            BigDecimal currentBattery = agvStatus.getBatteryLevel();
            if (currentBattery == null) {
                currentBattery = new BigDecimal("100.00");
            }
            
            // 充电状态：电量增加
            if (AgvStatusEnum.CHARGING.getCode().equals(agvStatus.getAgvStatus())) {
                // 充电速度：每秒增加 2%（比消耗快）
                BigDecimal chargingRate = new BigDecimal("2.00");
                BigDecimal newBattery = currentBattery.add(chargingRate);
                
                if (newBattery.compareTo(new BigDecimal("100.00")) >= 0) {
                    // 电量已充满
                    newBattery = new BigDecimal("100.00");
                    finishCharging(agvStatus);
                }
                
                agvStatus.setBatteryLevel(newBattery);
                agvStatus.setLastUpdateTime(LocalDateTime.now());
                agvStatusMapper.updateById(agvStatus);
                
                // 更新实时充电监控记录
                updateChargingNow(agvStatus, newBattery);
                
                log.debug("小车{}正在充电，电量: {}%", agvStatus.getAgvId(), newBattery);
                continue;
            }
            
            // 非充电状态：电量消耗
            BigDecimal consumptionRate = agvStatus.getBatteryConsumptionRate();
            if (consumptionRate == null) {
                consumptionRate = DEFAULT_BATTERY_CONSUMPTION;
            }

            // 如果是故障状态且是电量耗尽故障，电量会慢慢恢复（模拟慢充）
            if (AgvStatusEnum.FAULT.getCode().equals(agvStatus.getAgvStatus()) 
                && "BATTERY_EMPTY".equals(agvStatus.getFaultCode())) {
                // 电量耗尽故障状态下，电量会慢慢恢复（模拟充电中）
                // 当电量恢复到一定值时，自动恢复正常状态
                BigDecimal newBattery = currentBattery.add(consumptionRate.multiply(new BigDecimal("0.5"))); // 充电速度是消耗的0.5倍
                if (newBattery.compareTo(BigDecimal.valueOf(100)) > 0) {
                    newBattery = new BigDecimal("100.00");
                }
                agvStatus.setBatteryLevel(newBattery);
                
                // 电量恢复到50%以上时，恢复为空闲状态
                if (newBattery.compareTo(new BigDecimal("50.00")) >= 0) {
                    agvStatus.setAgvStatus(AgvStatusEnum.IDLE.getCode());
                    agvStatus.setFaultCode(null);
                    agvStatus.setFaultMessage(null);
                    log.info("小车{}电量恢复到{}%，状态恢复正常", agvStatus.getAgvId(), newBattery);
                }
                
                agvStatus.setLastUpdateTime(LocalDateTime.now());
                agvStatusMapper.updateById(agvStatus);
                continue;
            }

            // 计算新电量
            BigDecimal newBattery = currentBattery.subtract(consumptionRate);
            if (newBattery.compareTo(BigDecimal.ZERO) < 0) {
                newBattery = BigDecimal.ZERO;
            }

            // 更新电量
            agvStatus.setBatteryLevel(newBattery);
            agvStatus.setLastUpdateTime(LocalDateTime.now());
            agvStatusMapper.updateById(agvStatus);

            // 如果电量耗尽，标记为故障
            if (newBattery.compareTo(BigDecimal.ZERO) == 0) {
                agvStatus.setAgvStatus(AgvStatusEnum.FAULT.getCode());
                agvStatus.setFaultCode("BATTERY_EMPTY");
                agvStatus.setFaultMessage("电量耗尽");
                agvStatusMapper.updateById(agvStatus);
            }
        }
    }
    
    /**
     * 完成充电
     * - 记录充电历史
     * - 更新小车状态为空闲
     * - 释放充电口
     */
    private void finishCharging(AgvStatus agvStatus) {
        log.info("小车{}电量已充满，完成充电", agvStatus.getAgvId());
        
        // 结束实时充电监控
        endChargingNow(agvStatus);
        
        // 记录充电历史
        ChargingRecord record = new ChargingRecord();
        record.setAgvId(agvStatus.getAgvId());
        record.setStationId(agvStatus.getChargingStationId());
        record.setStartTime(agvStatus.getLastChargeTime() != null ? agvStatus.getLastChargeTime() : LocalDateTime.now().minusMinutes(5));
        record.setEndTime(LocalDateTime.now());
        record.setStartBattery(agvStatus.getBatteryLevel().subtract(new BigDecimal("100.00")).negate()); // 充电前电量
        record.setEndBattery(new BigDecimal("100.00"));
        
        // 计算充电时长
        if (record.getStartTime() != null) {
            record.setChargingDuration((int) java.time.Duration.between(record.getStartTime(), record.getEndTime()).getSeconds());
        }
        
        // 计算用电量（假设每度电可以充10%）
        if (record.getChargingDuration() != null) {
            record.setElectricityUsed(new BigDecimal(record.getChargingDuration()).divide(new BigDecimal("3600"), 2, BigDecimal.ROUND_HALF_UP));
        }
        
        record.setChargeType("auto");
        record.setResult("success");
        record.setRemark("自动充电完成");
        
        chargingRecordMapper.insert(record);
        
        // 更新小车状态为空闲
        agvStatus.setAgvStatus(AgvStatusEnum.IDLE.getCode());
        agvStatus.setCurrentTaskId(null);
        agvStatus.setTargetLocation(null);
        agvStatus.setLastChargeTime(LocalDateTime.now());
        agvStatus.setLastUpdateTime(LocalDateTime.now());
        
        // 释放充电口（更新充电站状态）
        if (agvStatus.getChargingStationId() != null && agvStatus.getChargingPort() != null) {
            ChargingStation station = chargingStationMapper.selectById(agvStatus.getChargingStationId());
            if (station != null) {
                // 释放充电口
                if ("top".equals(agvStatus.getChargingPort())) {
                    station.setTopPort(null);
                } else if ("bottom".equals(agvStatus.getChargingPort())) {
                    station.setBottomPort(null);
                } else if ("left".equals(agvStatus.getChargingPort())) {
                    station.setLeftPort(null);
                } else if ("right".equals(agvStatus.getChargingPort())) {
                    station.setRightPort(null);
                }
                chargingStationMapper.updateById(station);
            }
        }
        
        agvStatus.setChargingStationId(null);
        agvStatus.setChargingPort(null);
        
        agvStatusMapper.updateById(agvStatus);
        
        log.info("小车{}充电完成，状态变为空闲，充电记录ID: {}", agvStatus.getAgvId(), record.getId());
    }
    
    /**
     * 开始实时充电监控
     */
    private void startChargingNow(AgvStatus agvStatus) {
        // 查询是否已有实时充电记录
        ChargingNow existing = chargingNowMapper.selectOne(
            new LambdaQueryWrapper<ChargingNow>()
                .eq(ChargingNow::getBatteryId, agvStatus.getAgvId())
                .eq(ChargingNow::getIsCharging, 1)
        );
        
        if (existing != null) {
            // 已有记录，无需重复创建
            return;
        }
        
        ChargingNow chargingNow = new ChargingNow();
        chargingNow.setBatteryId(agvStatus.getAgvId());
        chargingNow.setBatteryCode("AGV-" + agvStatus.getAgvId());
        chargingNow.setChargeStartTime(LocalDateTime.now());
        chargingNow.setVoltage(new BigDecimal("48.00")); // 模拟电压
        chargingNow.setCurrent(new BigDecimal("10.00")); // 模拟电流
        chargingNow.setTemperature(new BigDecimal("25.00")); // 模拟温度
        chargingNow.setChargePower(new BigDecimal("0.48")); // 模拟功率 48V * 10A
        chargingNow.setChargeDuration(0);
        chargingNow.setChargedCapacity(BigDecimal.ZERO);
        chargingNow.setSoc(agvStatus.getBatteryLevel() != null ? agvStatus.getBatteryLevel().intValue() : 0);
        chargingNow.setIsCharging(1);
        
        chargingNowMapper.insert(chargingNow);
        
        log.info("小车{}开始实时充电监控，记录ID: {}", agvStatus.getAgvId(), chargingNow.getId());
    }
    
    /**
     * 更新实时充电监控
     */
    private void updateChargingNow(AgvStatus agvStatus, BigDecimal newBattery) {
        ChargingNow chargingNow = chargingNowMapper.selectOne(
                new LambdaQueryWrapper<ChargingNow>()
                        .eq(ChargingNow::getBatteryId, agvStatus.getAgvId())
                        .eq(ChargingNow::getIsCharging, 1)
        );

        if (chargingNow == null) {
            return;
        }

        // 计算充电时长
        int duration = 0;
        if (chargingNow.getChargeStartTime() != null) {
            duration = (int) java.time.Duration.between(chargingNow.getChargeStartTime(), LocalDateTime.now()).getSeconds();
            chargingNow.setChargeDuration(duration);
        }

        // 更新充电数据
        chargingNow.setSoc(newBattery.intValue());
        chargingNow.setChargedCapacity(newBattery.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))); // 模拟已充电量

        // 模拟更新电压和温度（随着电量增加，电压上升，温度下降）
        BigDecimal voltage = new BigDecimal("48.00").add(newBattery.multiply(new BigDecimal("0.12"))); // 48V + 电量 * 0.12
        BigDecimal temperature = new BigDecimal("25.00").subtract(duration > 60 ? new BigDecimal("0.01").multiply(new BigDecimal(duration - 60)) : BigDecimal.ZERO);
        chargingNow.setVoltage(voltage);
        chargingNow.setTemperature(temperature.compareTo(new BigDecimal("40")) > 0 ? new BigDecimal("40") : temperature);

        chargingNowMapper.updateById(chargingNow);
    }
    
    /**
     * 结束实时充电监控
     */
    private void endChargingNow(AgvStatus agvStatus) {
        ChargingNow chargingNow = chargingNowMapper.selectOne(
            new LambdaQueryWrapper<ChargingNow>()
                .eq(ChargingNow::getBatteryId, agvStatus.getAgvId())
                .eq(ChargingNow::getIsCharging, 1)
        );
        
        if (chargingNow != null) {
            chargingNow.setIsCharging(0);
            chargingNowMapper.updateById(chargingNow);
            
            log.info("小车{}结束实时充电监控，记录ID: {}", agvStatus.getAgvId(), chargingNow.getId());
        }
    }

    /**
     * 检查小车是否需要返回充电
     */
    private void checkAndReturnToCharge() {
        // 1. 检查空闲状态的小车
        List<AgvStatus> idleAgvs = agvStatusMapper.selectList(
            new LambdaQueryWrapper<AgvStatus>()
                .eq(AgvStatus::getAgvStatus, AgvStatusEnum.IDLE.getCode())
        );

        log.debug("检查空闲小车数量: {}", idleAgvs.size());
        
        for (AgvStatus agvStatus : idleAgvs) {
            checkAndReturnForAgv(agvStatus);
        }
        
        // 2. 检查工作中和返回中的小车电量
        checkWorkingAgvBattery();
    }
    
    /**
     * 检查单个小车是否需要返回充电
     */
    private void checkAndReturnForAgv(AgvStatus agvStatus) {
        // 计算预警电量
        double warningLevel = calculateWarningBatteryLevel(agvStatus);
        agvStatus.setWarningBatteryLevel(BigDecimal.valueOf(warningLevel));
        
        BigDecimal currentBattery = agvStatus.getBatteryLevel();
        if (currentBattery == null) {
            currentBattery = BigDecimal.ZERO;
        }

        log.debug("小车{} 电量: {}%, 预警电量: {}%", 
            agvStatus.getAgvId(), currentBattery, warningLevel);
        
        // 如果当前电量低于预警电量，自动返回充电
        if (currentBattery.doubleValue() <= warningLevel) {
            log.info("小车{}电量{}低于预警电量{}，自动返回充电", 
                agvStatus.getAgvId(), currentBattery, warningLevel);
            returnToCharge(agvStatus.getAgvId());
        }
    }
    
    /**
     * 检查工作中的小车电量，如果电量过低则取消任务并返回充电
     */
    private void checkWorkingAgvBattery() {
        List<AgvStatus> workingAgvs = agvStatusMapper.selectList(
            new LambdaQueryWrapper<AgvStatus>()
                .eq(AgvStatus::getAgvStatus, AgvStatusEnum.WORKING.getCode())
        );
        
        for (AgvStatus agvStatus : workingAgvs) {
            BigDecimal currentBattery = agvStatus.getBatteryLevel();
            if (currentBattery == null) {
                currentBattery = BigDecimal.ZERO;
            }
            
            // 如果电量低于10%，强制停止任务返回充电
            if (currentBattery.doubleValue() < 10) {
                log.warn("小车{}电量{}过低，强制取消任务并返回充电", 
                    agvStatus.getAgvId(), currentBattery);
                
                // 取消当前任务
                if (agvStatus.getCurrentTaskId() != null) {
                    AgvTask task = agvTaskMapper.selectById(agvStatus.getCurrentTaskId());
                    if (task != null) {
                        task.setTaskStatus("cancelled");
                        task.setEndTime(LocalDateTime.now());
                        task.setErrorMessage("电量不足，任务取消");
                        agvTaskMapper.updateById(task);
                    }
                }
                
                // 返回充电
                returnToCharge(agvStatus.getAgvId());
            }
        }
    }

    /**
     * 分配任务给小车
     */
    private void assignTasksToAgvs() {
        // 获取待入库、待出库、待调拨的工单
        List<InOutOrder> pendingOrders = getPendingOrders();
        
        for (InOutOrder order : pendingOrders) {
            assignTaskToAgv(order);
        }
    }

    @Override
    public boolean assignTaskToAgv(InOutOrder order) {
        // 获取所有可用的小车（空闲状态）
        List<AgvStatus> availableAgvs = agvStatusMapper.selectList(
            new LambdaQueryWrapper<AgvStatus>()
                .eq(AgvStatus::getAgvStatus, AgvStatusEnum.IDLE.getCode())
        );

        // 按电量排序，电量高的优先
        availableAgvs.sort((a, b) -> {
            BigDecimal batteryA = a.getBatteryLevel() != null ? a.getBatteryLevel() : BigDecimal.ZERO;
            BigDecimal batteryB = b.getBatteryLevel() != null ? b.getBatteryLevel() : BigDecimal.ZERO;
            return batteryB.compareTo(batteryA);
        });

        for (AgvStatus agv : availableAgvs) {
            // 检查小车是否应该拒绝任务
            if (shouldRejectTaskForOrder(agv, order)) {
                log.info("小车{}预计完成任务后电量低于预警电量，拒绝任务{}", agv.getAgvId(), order.getId());
                continue;
            }

            // 分配任务
            return assignTaskToAgv(agv, order);
        }

        return false;
    }

    /**
     * 检查小车是否应该拒绝该订单任务
     */
    private boolean shouldRejectTaskForOrder(AgvStatus agv, InOutOrder order) {
        // 创建临时任务来估算电量消耗
        AgvTask task = new AgvTask();
        task.setFromLocation(order.getSourceLocationCode());
        task.setToLocation(order.getTargetLocationCode());
        
        // 估算任务电量消耗
        double estimatedConsumption = estimateTaskBatteryConsumption(task);
        
        // 计算完成任务后的电量
        BigDecimal currentBattery = agv.getBatteryLevel() != null ? agv.getBatteryLevel() : BigDecimal.ZERO;
        double batteryAfterTask = currentBattery.doubleValue() - estimatedConsumption;
        
        // 计算返回充电站需要的电量
        ChargingStation station = getNearestChargingStation(agv);
        if (station != null) {
            double returnDistance = calculateDistance(
                agv.getXPosition() != null ? agv.getXPosition().doubleValue() : 0,
                agv.getYPosition() != null ? agv.getYPosition().doubleValue() : 0,
                station.getXPosition() != null ? station.getXPosition().doubleValue() : 0,
                station.getYPosition() != null ? station.getYPosition().doubleValue() : 0
            );
            
            BigDecimal speed = agv.getMoveSpeed() != null ? agv.getMoveSpeed() : DEFAULT_MOVE_SPEED;
            double returnTime = returnDistance / speed.doubleValue();
            double returnBattery = returnTime * DEFAULT_BATTERY_CONSUMPTION.doubleValue();
            
            // 如果完成任务后电量低于返回所需电量，拒绝任务
            double warningLevel = calculateWarningBatteryLevel(agv);
            return batteryAfterTask < (returnBattery + warningLevel);
        }
        
        return false;
    }

    /**
     * 实际分配任务给小车
     */
    @Transactional
    protected boolean assignTaskToAgv(AgvStatus agv, InOutOrder order) {
        try {
            // 创建AGV任务
            AgvTask task = new AgvTask();
            task.setTaskCode("TASK-" + System.currentTimeMillis());
            task.setTaskType(order.getOrderType());
            task.setOrderId(order.getId());
            task.setOrderCode(order.getOrderCode());
            task.setAgvId(agv.getAgvId());
            task.setPriority(order.getPriority() != null ? order.getPriority() : 5);
            task.setFromLocation(order.getSourceLocationCode());
            task.setToLocation(order.getTargetLocationCode());
            task.setCargoInfo(order.getItemName());
            task.setTaskStatus("executing");
            task.setStartTime(LocalDateTime.now());

            // 估算距离和耗时
            double distance = estimateDistance(order.getSourceLocationCode(), order.getTargetLocationCode());
            BigDecimal speed = agv.getMoveSpeed() != null ? agv.getMoveSpeed() : DEFAULT_MOVE_SPEED;
            int estimatedDuration = (int) (distance / speed.doubleValue()) + 
                                   DEFAULT_TURN_OVERHEAD.intValue();
            
            task.setEstimatedDistance(BigDecimal.valueOf(distance));
            task.setEstimatedDuration(estimatedDuration);
            
            // 估算电量消耗
            double batteryConsumption = estimateTaskBatteryConsumption(task);
            task.setEstimatedBatteryConsumption(BigDecimal.valueOf(batteryConsumption));

            // 计算返回充电站所需电量
            ChargingStation station = getNearestChargingStation(agv);
            if (station != null) {
                double returnDistance = calculateDistance(
                    getLocationX(order.getTargetLocationCode()),
                    getLocationY(order.getTargetLocationCode()),
                    station.getXPosition() != null ? station.getXPosition().doubleValue() : 0,
                    station.getYPosition() != null ? station.getYPosition().doubleValue() : 0
                );
                double returnTime = returnDistance / speed.doubleValue();
                task.setReturnDistance(BigDecimal.valueOf(returnDistance));
                task.setReturnBatteryNeeded(BigDecimal.valueOf(returnTime * DEFAULT_BATTERY_CONSUMPTION.doubleValue()));
            }

            agvTaskMapper.insert(task);

            // 更新小车状态
            agv.setAgvStatus(AgvStatusEnum.WORKING.getCode());
            agv.setCurrentTaskId(task.getId());
            agv.setCurrentLocation(order.getSourceLocationCode());
            agv.setTargetLocation(order.getTargetLocationCode());
            agv.setLastUpdateTime(LocalDateTime.now());
            
            // 根据当前位置代码设置坐标（如果没有坐标或坐标为0）
            double[] coords = parseLocationCode(order.getSourceLocationCode());
            // 使用原生SQL更新坐标字段
            updateAgvPosition(agv.getAgvId(), coords[0], coords[1]);
            
            log.info("成功分配任务{}给小车{}", task.getId(), agv.getAgvId());

            // 更新工单状态
            if ("in".equals(order.getOrderType())) {
                inOutOrderService.transitionStatus(order.getId(), "in_progress", 0L, "系统", "AGV开始执行入库任务");
            } else if ("out".equals(order.getOrderType())) {
                inOutOrderService.transitionStatus(order.getId(), "out_progress", 0L, "系统", "AGV开始执行出库任务");
            }

            log.info("成功分配任务{}给小车{}", task.getId(), agv.getAgvId());
            return true;
        } catch (Exception e) {
            log.error("分配任务失败", e);
            return false;
        }
    }

    @Override
    public boolean returnToCharge(Long agvId) {
        AgvStatus agvStatus = agvStatusMapper.selectOne(
            new LambdaQueryWrapper<AgvStatus>().eq(AgvStatus::getAgvId, agvId)
        );

        if (agvStatus == null) {
            return false;
        }

        // 如果正在充电，不返回
        if (AgvStatusEnum.CHARGING.getCode().equals(agvStatus.getAgvStatus())) {
            return true;
        }

        // 获取最近的充电站
        ChargingStation station = getNearestChargingStation(agvStatus);
        if (station == null) {
            log.warn("未找到可用的充电站，小车{}无法返回充电", agvId);
            return false;
        }

        // 创建返回充电的任务
        AgvTask returnTask = new AgvTask();
        returnTask.setTaskCode("RETURN-" + System.currentTimeMillis());
        returnTask.setTaskType("transfer");
        returnTask.setAgvId(agvId);
        returnTask.setPriority(10); // 最高优先级
        returnTask.setFromLocation(agvStatus.getCurrentLocation() != null ? agvStatus.getCurrentLocation() : "unknown");
        returnTask.setToLocation(station.getStationCode() + "-" + getAvailablePort(station));
        returnTask.setTaskStatus("executing");
        returnTask.setStartTime(LocalDateTime.now());
        agvTaskMapper.insert(returnTask);

        log.info("小车{}创建返回充电任务: from={}, to={}, taskId={}", 
            agvId, returnTask.getFromLocation(), returnTask.getToLocation(), returnTask.getId());

        // 更新小车状态
        agvStatus.setAgvStatus(AgvStatusEnum.RETURNING.getCode());
        agvStatus.setCurrentTaskId(returnTask.getId());
        agvStatus.setTargetLocation(returnTask.getToLocation());
        agvStatus.setLastUpdateTime(LocalDateTime.now());
        
        // 设置充电站信息
        agvStatus.setChargingStationId(station.getId());
        agvStatus.setChargingPort(getAvailablePort(station));
        
        // 更新坐标（如果没有坐标，从当前位置解析；如果有坐标则保持不变）
        double currentX = agvStatus.getXPosition() != null ? agvStatus.getXPosition().doubleValue() : 0;
        double currentY = agvStatus.getYPosition() != null ? agvStatus.getYPosition().doubleValue() : 0;
        
        // 如果当前坐标为0，尝试从当前位置解析
        if (currentX == 0 && currentY == 0 && agvStatus.getCurrentLocation() != null) {
            double[] coords = parseLocationCode(agvStatus.getCurrentLocation());
            agvStatus.setXPosition(BigDecimal.valueOf(coords[0]));
            agvStatus.setYPosition(BigDecimal.valueOf(coords[1]));
        }
        
        agvStatusMapper.updateById(agvStatus);

        log.info("小车{}开始返回充电站{}, 当前位置: ({}, {}), 目标: {}", 
            agvId, station.getStationCode(), currentX, currentY, returnTask.getToLocation());
        return true;
    }

    /**
     * 获取可用的充电口
     */
    private String getAvailablePort(ChargingStation station) {
        // 简单策略：按顺序尝试每个充电口
        if (station.getTopPort() != null) return "top";
        if (station.getBottomPort() != null) return "bottom";
        if (station.getLeftPort() != null) return "left";
        if (station.getRightPort() != null) return "right";
        return "default";
    }

    @Override
    public double calculateWarningBatteryLevel(AgvStatus agvStatus) {
        // 获取最近的充电站
        ChargingStation station = getNearestChargingStation(agvStatus);
        if (station == null) {
            return DEFAULT_WARNING_BATTERY.doubleValue();
        }

        // 计算到充电站的距离
        double currentX = agvStatus.getXPosition() != null ? agvStatus.getXPosition().doubleValue() : 0;
        double currentY = agvStatus.getYPosition() != null ? agvStatus.getYPosition().doubleValue() : 0;
        double stationX = station.getXPosition() != null ? station.getXPosition().doubleValue() : 0;
        double stationY = station.getYPosition() != null ? station.getYPosition().doubleValue() : 0;
        
        double distance = calculateDistance(currentX, currentY, stationX, stationY);
        
        // 获取速度
        BigDecimal speed = agvStatus.getMoveSpeed();
        if (speed == null || speed.compareTo(BigDecimal.ZERO) == 0) {
            speed = DEFAULT_MOVE_SPEED;
        }

        // 计算返回所需时间
        double returnTime = distance / speed.doubleValue();
        
        // 计算所需电量（考虑转弯开销）
        BigDecimal turnOverhead = agvStatus.getTurnOverhead();
        if (turnOverhead == null) {
            turnOverhead = DEFAULT_TURN_OVERHEAD;
        }
        
        double totalTime = returnTime + turnOverhead.doubleValue();
        
        // 获取电量消耗率
        BigDecimal consumptionRate = agvStatus.getBatteryConsumptionRate();
        if (consumptionRate == null) {
            consumptionRate = DEFAULT_BATTERY_CONSUMPTION;
        }
        
        // 计算预警电量 = 返回所需电量 + 额外安全余量(5%)
        double warningBattery = totalTime * consumptionRate.doubleValue() + 5;
        
        // 不超过100
        return Math.min(warningBattery, 100);
    }

    @Override
    public double estimateTaskBatteryConsumption(AgvTask task) {
        // 估算距离
        double distance = estimateDistance(task.getFromLocation(), task.getToLocation());
        
        // 获取默认速度
        double speed = DEFAULT_MOVE_SPEED.doubleValue();
        
        // 计算时间
        double time = distance / speed + DEFAULT_TURN_OVERHEAD.doubleValue();
        
        // 计算电量消耗
        return time * DEFAULT_BATTERY_CONSUMPTION.doubleValue();
    }

    @Override
    public boolean shouldRejectTask(Long agvId, AgvTask task) {
        AgvStatus agvStatus = agvStatusMapper.selectOne(
            new LambdaQueryWrapper<AgvStatus>().eq(AgvStatus::getAgvId, agvId)
        );
        
        if (agvStatus == null) {
            return true;
        }

        // 估算任务电量消耗
        double consumption = estimateTaskBatteryConsumption(task);
        
        // 计算完成任务后的电量
        BigDecimal currentBattery = agvStatus.getBatteryLevel();
        if (currentBattery == null) {
            currentBattery = BigDecimal.ZERO;
        }
        
        double batteryAfterTask = currentBattery.doubleValue() - consumption;
        
        // 获取预警电量
        double warningLevel = calculateWarningBatteryLevel(agvStatus);
        
        // 如果完成后电量低于预警电量，应该拒绝
        return batteryAfterTask < warningLevel;
    }

    @Override
    public List<InOutOrder> getPendingOrders() {
        // 获取待入库、待出库、待调拨的工单
        return inOutOrderService.list(
            new LambdaQueryWrapper<InOutOrder>()
                .in(InOutOrder::getOrderStatus, 
                    "pending_in",   // 待入库
                    "out_progress", // 待出库
                    "stored"        // 待调拨（存储中可调拨）
                )
                .orderByAsc(InOutOrder::getPriority)
                .orderByAsc(InOutOrder::getCreateTime)
        );
    }

    @Override
    public void updateAgvPosition(Long agvId, double x, double y) {
        AgvStatus agvStatus = agvStatusMapper.selectOne(
            new LambdaQueryWrapper<AgvStatus>().eq(AgvStatus::getAgvId, agvId)
        );
        
        if (agvStatus != null) {
            agvStatus.setXPosition(BigDecimal.valueOf(x));
            agvStatus.setYPosition(BigDecimal.valueOf(y));
            agvStatus.setLastUpdateTime(LocalDateTime.now());
            agvStatusMapper.updateById(agvStatus);
        }
    }

    @Override
    public ChargingStation getNearestChargingStation(AgvStatus agvStatus) {
        List<ChargingStation> stations = chargingStationMapper.selectList(
            new LambdaQueryWrapper<ChargingStation>()
                .eq(ChargingStation::getStatus, 1) // 启用的充电站
        );

        if (stations.isEmpty()) {
            return null;
        }

        // 找到最近的充电站
        double currentX = agvStatus.getXPosition() != null ? agvStatus.getXPosition().doubleValue() : 0;
        double currentY = agvStatus.getYPosition() != null ? agvStatus.getYPosition().doubleValue() : 0;

        ChargingStation nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (ChargingStation station : stations) {
            double stationX = station.getXPosition() != null ? station.getXPosition().doubleValue() : 0;
            double stationY = station.getYPosition() != null ? station.getYPosition().doubleValue() : 0;
            
            double distance = calculateDistance(currentX, currentY, stationX, stationY);
            
            if (distance < minDistance) {
                minDistance = distance;
                nearest = station;
            }
        }

        return nearest;
    }

    @Override
    @Transactional
    public void completeTask(Long taskId) {
        AgvTask task = agvTaskMapper.selectById(taskId);
        if (task == null) {
            return;
        }

        // 更新任务状态
        task.setTaskStatus("completed");
        task.setEndTime(LocalDateTime.now());
        
        // 计算实际耗时
        if (task.getStartTime() != null) {
            long seconds = java.time.Duration.between(task.getStartTime(), task.getEndTime()).getSeconds();
            task.setActualDuration((int) seconds);
        }
        
        agvTaskMapper.updateById(task);

        // 更新小车状态为空闲
        if (task.getAgvId() != null) {
            AgvStatus agvStatus = agvStatusMapper.selectOne(
                new LambdaQueryWrapper<AgvStatus>().eq(AgvStatus::getAgvId, task.getAgvId())
            );
            
            if (agvStatus != null) {
                agvStatus.setAgvStatus(AgvStatusEnum.IDLE.getCode());
                agvStatus.setCurrentTaskId(null);
                agvStatus.setCurrentLocation(task.getToLocation());
                agvStatus.setTargetLocation(null);
                agvStatus.setLastUpdateTime(LocalDateTime.now());
                agvStatusMapper.updateById(agvStatus);
            }
        }

        // 更新工单状态
        if (task.getOrderId() != null) {
            InOutOrder order = inOutOrderService.getById(task.getOrderId());
            if (order != null) {
                if ("in".equals(order.getOrderType())) {
                    inOutOrderService.transitionStatus(order.getId(), "stored", 0L, "系统", "入库任务完成");
                } else if ("out".equals(order.getOrderType())) {
                    inOutOrderService.transitionStatus(order.getId(), "completed", 0L, "系统", "出库任务完成");
                }
            }
        }

        log.info("任务{}已完成", taskId);
    }

    @Override
    @Transactional
    public void startTask(Long taskId) {
        AgvTask task = agvTaskMapper.selectById(taskId);
        if (task == null) {
            return;
        }

        task.setTaskStatus("executing");
        task.setStartTime(LocalDateTime.now());
        agvTaskMapper.updateById(task);

        // 更新小车状态
        if (task.getAgvId() != null) {
            AgvStatus agvStatus = agvStatusMapper.selectOne(
                new LambdaQueryWrapper<AgvStatus>().eq(AgvStatus::getAgvId, task.getAgvId())
            );
            
            if (agvStatus != null) {
                agvStatus.setAgvStatus(AgvStatusEnum.WORKING.getCode());
                agvStatus.setCurrentTaskId(task.getId());
                agvStatus.setCurrentLocation(task.getFromLocation());
                agvStatus.setTargetLocation(task.getToLocation());
                agvStatus.setLastUpdateTime(LocalDateTime.now());
                agvStatusMapper.updateById(agvStatus);
            }
        }

        log.info("任务{}已开始执行", taskId);
    }

    /**
     * 计算两点之间的距离
     */
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * 估算两个位置之间的距离
     */
    private double estimateDistance(String fromLocation, String toLocation) {
        double fromX = getLocationX(fromLocation);
        double fromY = getLocationY(fromLocation);
        double toX = getLocationX(toLocation);
        double toY = getLocationY(toLocation);
        
        return calculateDistance(fromX, fromY, toX, toY);
    }

    /**
     * 从位置编码获取X坐标
     * 优先使用绝对坐标映射，确保与parseLocationCode结果一致
     */
    private double getLocationX(String location) {
        if (location == null || location.isEmpty()) {
            return 0;
        }
        
        // 优先使用绝对坐标映射（与parseLocationCode保持一致）
        double[] coords = getAbsoluteCoordinateOrParse(location);
        if (coords != null) {
            return coords[0];
        }
        
        return 0;
    }
    
    /**
     * 从位置编码获取Y坐标
     * 优先使用绝对坐标映射，确保与parseLocationCode结果一致
     */
    private double getLocationY(String location) {
        if (location == null || location.isEmpty()) {
            return 0;
        }
        
        // 优先使用绝对坐标映射（与parseLocationCode保持一致）
        double[] coords = getAbsoluteCoordinateOrParse(location);
        if (coords != null) {
            return coords[1];
        }
        
        return 0;
    }
    
    /**
     * 获取绝对坐标或使用parseLocationCode解析
     * 统一坐标解析逻辑
     */
    private double[] getAbsoluteCoordinateOrParse(String locationCode) {
        // 先尝试绝对坐标映射
        double[] coords = getAbsoluteCoordinate(locationCode);
        if (coords != null) {
            return coords;
        }
        
        // 如果没有绝对坐标，使用parseLocationCode逻辑
        return parseLocationCodeWithoutAbsolute(locationCode);
    }
    
    /**
     * 不使用绝对坐标的解析逻辑（作为备用）
     */
    private double[] parseLocationCodeWithoutAbsolute(String locationCode) {
        if (locationCode == null || locationCode.isEmpty()) {
            return new double[]{0, 0};
        }
        
        // 充电站位置解析
        if (locationCode.startsWith("CS-")) {
            String stationCode = locationCode;
            if (locationCode.contains("-")) {
                String[] parts = locationCode.split("-");
                if (parts.length >= 2) {
                    stationCode = parts[0] + "-" + parts[1];
                }
            }
            
            ChargingStation station = chargingStationMapper.selectOne(
                new LambdaQueryWrapper<ChargingStation>()
                    .eq(ChargingStation::getStationCode, stationCode)
            );
            if (station != null) {
                double baseX = station.getXPosition() != null ? station.getXPosition().doubleValue() : 0;
                double baseY = station.getYPosition() != null ? station.getYPosition().doubleValue() : 0;
                
                if (locationCode.contains("-top")) {
                    return new double[]{baseX, baseY + 2};
                } else if (locationCode.contains("-bottom")) {
                    return new double[]{baseX, baseY - 2};
                } else if (locationCode.contains("-left")) {
                    return new double[]{baseX - 2, baseY};
                } else if (locationCode.contains("-right")) {
                    return new double[]{baseX + 2, baseY};
                }
                
                return new double[]{baseX, baseY};
            }
            return new double[]{0, 0};
        }

        // 入库口位置解析
        if (locationCode.startsWith("ENTRY-")) {
            if (locationCode.contains("TOP")) return new double[]{0, -25};
            if (locationCode.contains("BOTTOM")) return new double[]{0, 25};
            if (locationCode.contains("LEFT")) return new double[]{-35, 0};
            if (locationCode.contains("RIGHT")) return new double[]{35, 0};
        }
        
        // 货位位置解析 A-01-003 格式
        try {
            String[] parts = locationCode.split("-");
            if (parts.length >= 3) {
                char area = parts[0].charAt(0);
                int row = Integer.parseInt(parts[1]) - 1;
                int col = Integer.parseInt(parts[2]) - 1;

                double x = (area - 'A') * 10 + row;
                double y = col;
                return new double[]{x, y};
            }
        } catch (Exception e) {
            log.warn("无法解析位置代码: {}", locationCode);
        }

        return new double[]{0, 0};
    }
}
