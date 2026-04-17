package com.orivon.manage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orivon.manage.mapper.ApiInfoMapper;
import com.orivon.manage.model.entity.ApiInfo;
import com.orivon.manage.service.ApiInfoService;
import org.springframework.stereotype.Service;

/**
 * @description: 接口管理服务实现类
 * @author: yuowu
 * @create: 2026-04-03 21:11
 **/
@Service
public class ApiInfoServiceImpl extends ServiceImpl<ApiInfoMapper, ApiInfo> implements ApiInfoService {
}
