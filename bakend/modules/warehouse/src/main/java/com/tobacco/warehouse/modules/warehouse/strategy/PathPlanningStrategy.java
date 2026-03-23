package com.tobacco.warehouse.modules.warehouse.strategy;

import java.util.List;

/**
 * 路径规划策略接口
 * 采用策略模式，允许自定义不同的路径规划算法
 * 
 * @author warehouse
 */
public interface PathPlanningStrategy {

    /**
     * 规划从起点到终点的路径
     * 
     * @param startX 起点X坐标
     * @param startY 起点Y坐标
     * @param endX   终点X坐标
     * @param endY   终点Y坐标
     * @return 路径点列表，每个元素是一个坐标对 [x, y]
     */
    List<double[]> planPath(double startX, double startY, double endX, double endY);

    /**
     * 获取策略名称
     * 
     * @return 策略名称
     */
    String getStrategyName();

    /**
     * 获取策略描述
     * 
     * @return 策略描述
     */
    String getDescription();
}
