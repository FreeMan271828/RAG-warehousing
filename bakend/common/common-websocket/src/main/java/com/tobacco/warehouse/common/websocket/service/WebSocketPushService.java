package com.tobacco.warehouse.common.websocket.service;

import com.tobacco.warehouse.common.websocket.endpoint.WebSocketEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 消息推送服务
 * 向前端推送实时数据
 *
 * @author warehouse
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketPushService {

    /**
     * 推送电池实时数据
     */
    public void pushBatteryRealTimeData(Object data) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "battery_realtime");
        message.put("data", data);
        message.put("timestamp", System.currentTimeMillis());
        
        WebSocketEndpoint.broadcast(message);
        log.debug("推送电池实时数据: {}", data);
    }

    /**
     * 推送设备状态变化
     */
    public void pushEquipmentStatus(Object data) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "equipment_status");
        message.put("data", data);
        message.put("timestamp", System.currentTimeMillis());
        
        WebSocketEndpoint.broadcast(message);
        log.debug("推送设备状态: {}", data);
    }

    /**
     * 推送告警信息
     */
    public void pushAlarm(Object data) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "alarm");
        message.put("data", data);
        message.put("timestamp", System.currentTimeMillis());
        
        WebSocketEndpoint.broadcast(message);
        log.info("推送告警信息: {}", data);
    }

    /**
     * 推送仓库状态
     */
    public void pushWarehouseStatus(Object data) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "warehouse_status");
        message.put("data", data);
        message.put("timestamp", System.currentTimeMillis());
        
        WebSocketEndpoint.broadcast(message);
        log.debug("推送仓库状态: {}", data);
    }

    /**
     * 推送给指定用户
     */
    public void pushToUser(String userId, String type, Object data) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("data", data);
        message.put("timestamp", System.currentTimeMillis());
        
        WebSocketEndpoint.sendToUser(userId, message);
        log.debug("推送消息给用户: {}, 类型: {}", userId, type);
    }

    /**
     * 推送3D场景更新
     */
    public void pushSceneUpdate(Object data) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "scene_update");
        message.put("data", data);
        message.put("timestamp", System.currentTimeMillis());
        
        WebSocketEndpoint.broadcast(message);
        log.debug("推送3D场景更新: {}", data);
    }
}
