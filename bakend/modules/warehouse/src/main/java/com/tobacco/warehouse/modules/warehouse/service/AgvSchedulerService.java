package com.tobacco.warehouse.modules.warehouse.service;

import com.tobacco.warehouse.modules.warehouse.entity.AgvStatus;
import com.tobacco.warehouse.modules.warehouse.entity.AgvTask;
import com.tobacco.warehouse.modules.warehouse.entity.ChargingStation;
import com.tobacco.warehouse.modules.warehouse.entity.InOutOrder;

import java.util.List;

/**
 * AGV任务调度器 服务接口
 * 
 * @author warehouse
 */
public interface AgvSchedulerService {

    /**
     * 定时调度任务（每秒执行）
     * 负责：
     * 1. 更新小车电量
     * 2. 检查小车是否需要返回充电
     * 3. 分配任务给小车
     */
    void schedule();

    /**
     * 分配任务给小车
     * 
     * @param order 工单
     * @return 分配成功返回true，否则返回false
     */
    boolean assignTaskToAgv(InOutOrder order);

    /**
     * 小车自动返回充电
     * 
     * @param agvId 小车ID
     * @return 返回成功返回true
     */
    boolean returnToCharge(Long agvId);

    /**
     * 计算预警电量（根据返回充电站距离和每秒耗电计算）
     * 
     * @param agvStatus 小车状态
     * @return 预警电量百分比
     */
    double calculateWarningBatteryLevel(AgvStatus agvStatus);

    /**
     * 估算任务所需电量
     * 
     * @param task 任务
     * @return 所需电量百分比
     */
    double estimateTaskBatteryConsumption(AgvTask task);

    /**
     * 检查小车是否应该拒绝任务
     * 
     * @param agvId 小车ID
     * @param task 任务
     * @return 应该拒绝返回true
     */
    boolean shouldRejectTask(Long agvId, AgvTask task);

    /**
     * 获取待分配的任务列表
     * 
     * @return 待分配任务列表
     */
    List<InOutOrder> getPendingOrders();

    /**
     * 更新小车位置
     * 
     * @param agvId 小车ID
     * @param x X坐标
     * @param y Y坐标
     */
    void updateAgvPosition(Long agvId, double x, double y);

    /**
     * 获取最近的可充电站
     * 
     * @param agvStatus 小车状态
     * @return 最近的充电站
     */
    ChargingStation getNearestChargingStation(AgvStatus agvStatus);

    /**
     * 完成任务
     * 
     * @param taskId 任务ID
     */
    void completeTask(Long taskId);

    /**
     * 开始执行任务
     * 
     * @param taskId 任务ID
     */
    void startTask(Long taskId);
}
