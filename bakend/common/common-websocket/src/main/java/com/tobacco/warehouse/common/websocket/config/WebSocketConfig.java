package com.tobacco.warehouse.common.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 配置类
 *
 * @author warehouse
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig {

    /**
     * 注入ServerEndpointExporter
     * 该Bean会自动注册使用@ServerEndpoint注解的websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
