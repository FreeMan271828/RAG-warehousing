package com.tobacco.warehouse.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author warehouse
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),
    TOKEN_EXPIRED(1004, "Token已过期"),
    TOKEN_INVALID(1005, "Token无效"),
    ROLE_NOT_FOUND(2001, "角色不存在"),
    PERMISSION_DENIED(3001, "权限不足"),
    DATA_NOT_FOUND(4001, "数据不存在"),
    DATA_ALREADY_EXISTS(4002, "数据已存在"),
    EQUIPMENT_NOT_FOUND(5001, "设备不存在"),
    EQUIPMENT_OFFLINE(5002, "设备离线"),
    BATTERY_NOT_FOUND(6001, "电池不存在"),
    BATTERY_CHARGING(6002, "电池正在充电中"),
    BATTERY_FAULT(6003, "电池故障");

    private final Integer code;
    private final String message;
}
