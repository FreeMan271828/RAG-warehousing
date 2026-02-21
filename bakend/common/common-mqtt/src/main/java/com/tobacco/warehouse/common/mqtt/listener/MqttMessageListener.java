package com.tobacco.warehouse.common.mqtt.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

/**
 * MQTT 消息监听器
 * 处理接收到的MQTT消息
 *
 * @author warehouse
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttMessageListener {

    private final ObjectMapper objectMapper;

    /**
     * 处理来自MQTT的消息
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        return message -> {
            try {
                String topic = message.getHeaders().get("mqtt_topic", String.class);
                String payload = message.getPayload().toString();
                
                log.info("收到MQTT消息 - Topic: {}, Payload: {}", topic, payload);
                
                // 根据topic处理不同的消息类型
                if (topic != null) {
                    handleMessage(topic, payload);
                }
            } catch (Exception e) {
                log.error("处理MQTT消息失败: {}", e.getMessage(), e);
            }
        };
    }

    /**
     * 根据Topic处理消息
     */
    private void handleMessage(String topic, String payload) {
        // 电池相关消息
        if (topic.startsWith("battery/")) {
            handleBatteryMessage(topic, payload);
        }
        // 设备相关消息
        else if (topic.startsWith("equipment/")) {
            handleEquipmentMessage(topic, payload);
        }
        // 仓库相关消息
        else if (topic.startsWith("warehouse/")) {
            handleWarehouseMessage(topic, payload);
        }
    }

    /**
     * 处理电池消息
     */
    private void handleBatteryMessage(String topic, String payload) {
        // 可以根据topic细分处理不同的电池数据
        log.debug("处理电池消息: {}", topic);
        
        // TODO: 根据消息类型更新数据库
        // 1. battery/realtime - 实时充电数据
        // 2. battery/status - 电池状态变化
        // 3. battery/error - 电池故障
    }

    /**
     * 处理设备消息
     */
    private void handleEquipmentMessage(String topic, String payload) {
        log.debug("处理设备消息: {}", topic);
        
        // TODO: 根据消息类型更新设备状态
        // 1. equipment/status - 设备状态变化
        // 2. equipment/alarm - 设备告警
    }

    /**
     * 处理仓库消息
     */
    private void handleWarehouseMessage(String topic, String payload) {
        log.debug("处理仓库消息: {}", topic);
        
        // TODO: 处理仓库相关消息
        // 1. warehouse/inout - 出入库事件
        // 2. warehouse/alarm - 仓库告警
    }
}
