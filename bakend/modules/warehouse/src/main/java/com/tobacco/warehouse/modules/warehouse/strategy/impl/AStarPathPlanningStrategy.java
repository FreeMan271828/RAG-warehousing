package com.tobacco.warehouse.modules.warehouse.strategy.impl;

import com.tobacco.warehouse.modules.warehouse.strategy.PathPlanningStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * A*路径规划策略 - 考虑障碍物
 * 使用A*算法寻找最短路径，绕过障碍物
 * 
 * @author warehouse
 */
@Slf4j
@Component
public class AStarPathPlanningStrategy implements PathPlanningStrategy {

    /**
     * 网格大小
     */
    private static final double GRID_SIZE = 1.0;
    
    /**
     * 障碍物列表（坐标点）
     */
    private final Set<String> obstacles = new HashSet<>();

    public AStarPathPlanningStrategy() {
        // 初始化障碍物（例如货架区域）
        initObstacles();
    }
    
    /**
     * 初始化障碍物
     */
    private void initObstacles() {
        // 示例：添加一些障碍物区域
        // 这里可以根据实际仓库布局设置障碍物
        // obstacles.add("10,5");
    }

    @Override
    public List<double[]> planPath(double startX, double startY, double endX, double endY) {
        List<double[]> path = new ArrayList<>();
        
        // 将连续坐标转换为网格坐标
        int startGridX = (int) Math.round(startX / GRID_SIZE);
        int startGridY = (int) Math.round(startY / GRID_SIZE);
        int endGridX = (int) Math.round(endX / GRID_SIZE);
        int endGridY = (int) Math.round(endY / GRID_SIZE);
        
        // A*算法寻路
        List<int[]> gridPath = aStar(startGridX, startGridY, endGridX, endGridY);
        
        if (gridPath == null || gridPath.isEmpty()) {
            log.warn("A*路径规划失败，无法找到路径，使用直线策略");
            // 如果A*失败，使用直线策略
            DirectPathPlanningStrategy directStrategy = new DirectPathPlanningStrategy();
            return directStrategy.planPath(startX, startY, endX, endY);
        }
        
        // 将网格路径转换为连续坐标路径
        for (int[] point : gridPath) {
            path.add(new double[]{point[0] * GRID_SIZE, point[1] * GRID_SIZE});
        }
        
        // 确保终点在路径中
        double lastX = path.get(path.size() - 1)[0];
        double lastY = path.get(path.size() - 1)[1];
        if (Math.abs(lastX - endX) > 0.01 || Math.abs(lastY - endY) > 0.01) {
            path.add(new double[]{endX, endY});
        }
        
        log.debug("A*路径规划: ({}, {}) -> ({}, {}), 共{}个路径点", 
                startX, startY, endX, endY, path.size());
        
        return path;
    }
    
    /**
     * A*算法实现
     */
    private List<int[]> aStar(int startX, int startY, int endX, int endY) {
        // 优先队列：按f值排序
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getF));
        
        // 已访问节点
        Set<String> closedSet = new HashSet<>();
        
        // 起点节点
        Node startNode = new Node(startX, startY, null, 0, heuristic(startX, startY, endX, endY));
        openSet.add(startNode);
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            
            // 到达终点
            if (current.getX() == endX && current.getY() == endY) {
                return reconstructPath(current);
            }
            
            closedSet.add(current.getKey());
            
            // 遍历邻居节点（4个方向）
            int[][] neighbors = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            
            for (int[] neighbor : neighbors) {
                int nx = current.getX() + neighbor[0];
                int ny = current.getY() + neighbor[1];
                String key = nx + "," + ny;
                
                // 检查是否已访问或是否为障碍物
                if (closedSet.contains(key) || isObstacle(nx, ny)) {
                    continue;
                }
                
                double g = current.getG() + 1;
                double h = heuristic(nx, ny, endX, endY);
                Node neighborNode = new Node(nx, ny, current, g, h);
                
                // 检查是否在openSet中且有更短的路径
                boolean inOpenSet = openSet.stream().anyMatch(n -> n.getKey().equals(key) && n.getG() <= g);
                if (!inOpenSet) {
                    openSet.add(neighborNode);
                }
            }
        }
        
        return null; // 未找到路径
    }
    
    /**
     * 重建路径
     */
    private List<int[]> reconstructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(0, new int[]{node.getX(), node.getY()});
            node = node.getParent();
        }
        return path;
    }
    
    /**
     * 检查是否为障碍物
     */
    private boolean isObstacle(int x, int y) {
        return obstacles.contains(x + "," + y);
    }
    
    /**
     * 启发函数：曼哈顿距离
     */
    private double heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }

    @Override
    public String getStrategyName() {
        return "ASTAR";
    }

    @Override
    public String getDescription() {
        return "A*路径规划策略 - 使用A*算法寻找最短路径，绕过障碍物";
    }
    
    /**
     * 添加障碍物
     */
    public void addObstacle(int x, int y) {
        obstacles.add(x + "," + y);
    }
    
    /**
     * 移除障碍物
     */
    public void removeObstacle(int x, int y) {
        obstacles.remove(x + "," + y);
    }
    
    /**
     * A*节点类
     */
    private static class Node {
        private final int x;
        private final int y;
        private final Node parent;
        private final double g; // 从起点到当前节点的实际成本
        private final double h; // 从当前节点到终点的估算成本
        private final double f; // 总成本 = g + h
        
        public Node(int x, int y, Node parent, double g, double h) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        public Node getParent() { return parent; }
        public double getG() { return g; }
        public double getF() { return f; }
        
        public String getKey() {
            return x + "," + y;
        }
    }
}
