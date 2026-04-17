package com.orivon.manage.common;

/**
 * @description: 统一的返回状态码枚举
 * @author: yuowu
 * @create: 2026-04-14
 **/
public enum ResultCode {

    // 成功状态码
    SUCCESS(200, "操作成功"),

    // 客户端/权限错误类状态码
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，缺少调用凭证或凭证无效"),
    FORBIDDEN(403, "当前访问被拒绝，例如IP不在白名单内"),
    NOT_FOUND(404, "资源不存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，已达到限流上限"),

    // 服务器错误类状态码
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),

    // 10xx: 代理鉴权与安全类错误
    PROXY_MISSING_CREDENTIALS(1001, "缺少调用凭证 (uKey/mKey/apiId)"),
    PROXY_AUTH_INVALID(1002, "授权信息无效或已被禁用"),
    PROXY_IP_FORBIDDEN(1003, "调用方IP不在授权白名单内"),
    
    // 11xx: 代理接口状态与限流类错误
    PROXY_API_OFFLINE(1101, "接口不存在或已正式下线"),
    PROXY_LIMIT_EXCEEDED(1102, "已达到该接口今日调用上限"),
    
    // 12xx: 转发与系统元数据类错误
    PROXY_METADATA_LOST(1201, "中间件元数据丢失，请联系管理员"),
    PROXY_UPSTREAM_ERROR(1202, "调用市级原始接口发生异常");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
