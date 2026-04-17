package com.orivon.manage.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * @description: 接口授权申请信息
 * @author: yuowu
 * @create: 2026-04-03 22:05
 **/
@Data
@TableName("api_auth")
public class ApiAuth {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("api_id")
    private Integer apiId;         // 关联的 ApiInfo ID

    @TableField("unit_id")
    private Integer unitId;        // 关联的 UnitInfo ID (申请单位)

    @TableField("u_key")
    private String uKey;           // 颁发的 uKey (调用凭证)

    @TableField("m_key")
    private String mKey;           // 颁发的 mKey (调用凭证)

    @TableField("white_list")
    private String whiteList;      // 该授权对应的调用方 IP 白名单

    @TableField("crypto_policy")
    private String cryptoPolicy;   // 加密策略: NONE-不加密, SM4, AES

    private Integer status = 1;    // 状态: 0-停用, 1-启用

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
