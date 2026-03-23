package com.tobacco.warehouse.modules.warehouse.strategy;

import com.tobacco.warehouse.modules.warehouse.strategy.impl.AStarPathPlanningStrategy;
import com.tobacco.warehouse.modules.warehouse.strategy.impl.DirectPathPlanningStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路径规划策略工厂
 * 用于管理和选择路径规划策略
 * 
 * @author warehouse
 */
@Slf4j
@Component
public class PathPlanningStrategyFactory {

    @Autowired
    private DirectPathPlanningStrategy directPathPlanningStrategy;

    @Autowired
    private AStarPathPlanningStrategy aStarPathPlanningStrategy;

    /**
     * 策略缓存
     */
    private final Map<String, PathPlanningStrategy> strategyMap = new HashMap<>();

    /**
     * 当前使用的策略名称
     */
    private String currentStrategyName = "DIRECT";

    @PostConstruct
    public void init() {
        // 注册所有策略
        registerStrategy(directPathPlanningStrategy);
        registerStrategy(aStarPathPlanningStrategy);
        
        log.info("路径规划策略工厂初始化完成，可用策略: {}", getAvailableStrategies());
    }

    /**
     * 注册策略
     */
    public void registerStrategy(PathPlanningStrategy strategy) {
        strategyMap.put(strategy.getStrategyName(), strategy);
        log.info("注册路径规划策略: {} - {}", strategy.getStrategyName(), strategy.getDescription());
    }

    /**
     * 获取策略
     */
    public PathPlanningStrategy getStrategy(String strategyName) {
        PathPlanningStrategy strategy = strategyMap.get(strategyName);
        if (strategy == null) {
            log.warn("未找到策略: {}，使用默认策略DIRECT", strategyName);
            return strategyMap.get("DIRECT");
        }
        return strategy;
    }

    /**
     * 获取当前策略
     */
    public PathPlanningStrategy getCurrentStrategy() {
        return getStrategy(currentStrategyName);
    }

    /**
     * 设置当前策略
     */
    public void setCurrentStrategy(String strategyName) {
        if (strategyMap.containsKey(strategyName)) {
            this.currentStrategyName = strategyName;
            log.info("切换路径规划策略为: {}", strategyName);
        } else {
            log.warn("策略不存在: {}，保持当前策略: {}", strategyName, currentStrategyName);
        }
    }

    /**
     * 获取所有可用策略
     */
    public List<String> getAvailableStrategies() {
        return List.copyOf(strategyMap.keySet());
    }

    /**
     * 获取当前策略名称
     */
    public String getCurrentStrategyName() {
        return currentStrategyName;
    }

    /**
     * 规划路径（使用当前策略）
     */
    public List<double[]> planPath(double startX, double startY, double endX, double endY) {
        return getCurrentStrategy().planPath(startX, startY, endX, endY);
    }
}
