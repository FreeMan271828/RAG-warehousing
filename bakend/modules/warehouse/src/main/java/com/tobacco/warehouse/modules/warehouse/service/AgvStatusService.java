package com.tobacco.warehouse.modules.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tobacco.warehouse.modules.warehouse.entity.AgvStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * AGV状态 服务接口
 * 
 * @author warehouse
 */
public interface AgvStatusService extends IService<AgvStatus> {

    /**
     * 获取所有AGV列表（含设备信息）
     */
    List<AgvStatus> listAgvWithEquipment();

    /**
     * 根据设备ID获取AGV状态
     */
    AgvStatus getByAgvId(Long agvId);

    /**
     * 更新电量
     */
    boolean updateBatteryLevel(Long agvId, BigDecimal batteryLevel);

    /**
     * 更新小车状态
     */
    boolean updateAgvStatus(Long agvId, String status);

    /**
     * 开始充电
     */
    boolean startCharging(Long agvId, Long stationId);

    /**
     * 结束充电
     */
    boolean endCharging(Long agvId, String result, String interruptReason);

    /**
     * 获取可用的小车（电量充足的空闲小车）
     */
    List<AgvStatus> getAvailableAgvs();

    /**
     * 检查小车是否需要充电
     */
    boolean needsCharging(Long agvId);
}
