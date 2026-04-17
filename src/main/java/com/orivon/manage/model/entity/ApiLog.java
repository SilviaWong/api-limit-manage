package com.orivon.manage.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * @description: 接口调用审计日志
 * @author: yuowu
 * @create: 2026-04-03 22:10
 **/
@Data
@TableName("api_log")
public class ApiLog {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("api_id")
    private Integer apiId;         // 调用的 Api ID

    @TableField("auth_id")
    private Integer authId;        // 对应的 ApiAuth ID

    @TableField("u_key")
    private String uKey;           // 调用时透传的 uKey

    private String ip;             // 调用方 IP

    @TableField("request_params")
    private String requestParams;  // 原始请求参数 (JSON)

    @TableField("result_summary")
    private String resultSummary;  // 结果摘要 (成功标识/部分返回)

    @TableField("error_code")
    private String errorCode;      // 错误码 (200成功, 其余失败)

    @TableField("cost_time")
    private Long costTime;         // 耗时 (毫秒)

    @TableField("create_time")
    private Date createTime;       // 调用发生时间
}
