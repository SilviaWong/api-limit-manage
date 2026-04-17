package com.orivon.manage.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * @description: 区级使用单位信息
 * @author: yuowu
 * @create: 2026-04-03 22:00
 **/
@Data
@TableName("unit_info")
public class UnitInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("unit_name")
    private String unitName;       // 单位名称 (如: 区卫健局)

    @TableField("unit_code")
    private String unitCode;       // 单位唯一编码

    private String description;    // 备注信息
    
    private Integer status = 1;    // 状态: 0-禁用, 1-启用

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
