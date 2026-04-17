package com.orivon.manage.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

/**
 * @description: 接口基本信息
 * @author: yuowu
 * @create: 2026-04-03 16:16
 **/
@Data
@TableName("api_info")
public class ApiInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;           // 接口名称
    private String directory;      // 资源目录/分类
    private String method;         // 请求方法 (GET/POST)
    private String url;            // 市级接口原始 URL
    
    @TableField("app_key")
    private String appKey;         // 市级分配的 appKey
    
    @TableField("app_secret")
    private String appSecret;      // 市级分配的 appSecret
    
    @TableField("service_code")
    private String serviceCode;    // 市级业务代码
    
    @TableField("input_desc")
    private String inputDesc;      // 入参描述 (JSON 或文本)
    
    @TableField("output_desc")
    private String outputDesc;     // 出参描述
    
    private String description;    // 详细描述

    @TableField("request_limit")
    private Integer requestLimit = 0; // 单日调用上限

    private Integer status = 1;    // 状态: 0-禁用, 1-启用

    @TableField("white_list")
    private String whiteList;      // IP 白名单 (逗号分隔)

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

}
