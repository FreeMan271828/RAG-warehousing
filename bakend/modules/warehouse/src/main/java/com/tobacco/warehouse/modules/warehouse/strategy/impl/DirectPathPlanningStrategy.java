package com.tobacco.warehouse.modules.warehouse.strategy.impl;

import com.tobacco.warehouse.modules.warehouse.strategy.PathPlanningStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认路径规划策略 - 直线移动
 * 小车直接从起点移动到终点，不考虑障碍物
 * 
 * @author warehouse
 */
@Slf4j
@Component
public class DirectPathPlanningStrategy implements PathPlanningStrategy {

    /**
     * 默认步长（每次移动的距离）
     */
    private static final double DEFAULT_STEP = 1.0;

    @Override
    public List<double[]> planPath(double startX, double startY, double endX, double endY) {
        List<double[]> path = new ArrayList<>();
        
        // 计算总距离
        double distance = calculateDistance(startX, startY, endX, endY);
        
        if (distance < 0.01) {
            // 起点和终点非常接近，无需移动
            path.add(new double[]{endX, endY});
            return path;
        }
        
        // 计算方向向量
        double dx = (endX - startX) / distance;
        double dy = (endY - startY) / distance;
        
        // 按步长生成路径点
        double currentX = startX;
        double currentY = startY;
        int steps = (int) Math.ceil(distance / DEFAULT_STEP);
        
        for (int i = 0; i < steps; i++) {
            // 最后一步确保到达终点
            if (i == steps - 1) {
                path.add(new double[]{endX, endY});
            } else {
                currentX += dx * DEFAULT_STEP;
                currentY += dy * DEFAULT_STEP;
                path.add(new double[]{currentX, currentY});
            }
        }
        
        log.debug("直线路径规划: ({}, {}) -> ({}, {}), 共{}个路径点", 
                startX, startY, endX, endY, path.size());
        
        return path;
    }

    @Override
    public String getStrategyName() {
        return "DIRECT";
    }

    @Override
    public String getDescription() {
        return "直线移动策略 - 小车直接从起点移动到终点，不考虑障碍物";
    }

    /**
     * 计算两点之间的距离
     */
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
