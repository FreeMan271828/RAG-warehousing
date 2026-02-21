package com.tobacco.warehouse.common.websocket.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket 端点
 * 用于实时向前端推送数据
 *
 * @author warehouse
 */
@Slf4j
@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocketEndpoint {

    /**
     * 线程安全的Set存储所有的连接会话
     */
    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    /**
     * 用户ID到会话的映射
     */
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();

    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        WebSocketEndpoint.objectMapper = objectMapper;
    }

    /**
     * 连接建立成功
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessions.add(session);
        userSessions.put(userId, session);
        log.info("WebSocket连接建立 - 用户ID: {}, 当前连接数: {}", userId, sessions.size());
        
        // 发送连接成功消息
        try {
            session.getBasicRemote().sendText("{\"type\":\"connect\",\"message\":\"连接成功\"}");
        } catch (IOException e) {
            log.error("发送连接消息失败", e);
        }
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessions.remove(session);
        userSessions.remove(userId);
        log.info("WebSocket连接关闭 - 用户ID: {}, 当前连接数: {}", userId, sessions.size());
    }

    /**
     * 收到客户端消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String userId) {
        log.info("收到WebSocket消息 - 用户ID: {}, 消息: {}", userId, message);
    }

    /**
     * 发生错误
     */
    @OnError
    public void onError(Session session, Throwable error, @PathParam("userId") String userId) {
        log.error("WebSocket发生错误 - 用户ID: {}, 错误: {}", userId, error.getMessage(), error);
    }

    /**
     * 广播消息给所有连接的用户
     */
    public static void broadcast(Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            for (Session session : sessions) {
                sendMessage(session, json);
            }
        } catch (IOException e) {
            log.error("广播消息失败", e);
        }
    }

    /**
     * 发送消息给指定用户
     */
    public static void sendToUser(String userId, Object message) {
        Session session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(message);
                sendMessage(session, json);
            } catch (IOException e) {
                log.error("发送消息给用户失败 - 用户ID: {}", userId, e);
            }
        }
    }

    /**
     * 发送消息给指定会话
     */
    private static void sendMessage(Session session, String message) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
    }

    /**
     * 获取当前连接数
     */
    public static int getConnectionCount() {
        return sessions.size();
    }
}
