package com.orivon.manage.common;


/**
 * @description: 全局统一返回结果类
 * @author: yuowu
 * @create: 2026-04-03 19:56
 **/
public class Result<T> {

    private Integer code; // 状态码：200表示成功，500表示失败等
    private String msg;   // 提示信息：给前端弹窗用的文字
    private T data;       // 核心数据：真正要返回给前端的业务数据（使用泛型T支持任意类型）

    // 私有化构造函数，强制大家使用下面的静态方法来创建对象
    private Result() {}

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ============================
    // 成功时的快捷返回方法
    // ============================

    /**
     * 操作成功，但不需要返回具体数据（比如删除操作）
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 操作成功，且需要返回具体数据（比如查询操作）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 操作成功，自定义提示信息和数据
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    // ============================
    // 失败时的快捷返回方法
    // ============================

    /**
     * 操作失败，返回默认错误码 500 和自定义错误信息
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), msg, null);
    }

    /**
     * 操作失败，自定义错误码和错误信息
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
    
    /**
     * 操作失败，通过 ResultCode 快速返回
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }
    
    /**
     * 操作失败，通过 ResultCode 和自定义消息返回
     */
    public static <T> Result<T> error(ResultCode resultCode, String msg) {
        return new Result<>(resultCode.getCode(), msg, null);
    }

    // ============================
    // Getters and Setters (必须要有，否则 Spring Boot 无法将其转为 JSON)
    // ============================

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
