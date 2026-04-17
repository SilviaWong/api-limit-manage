package com.orivon.manage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orivon.manage.mapper.UnitInfoMapper;
import com.orivon.manage.model.entity.UnitInfo;
import com.orivon.manage.service.UnitInfoService;
import org.springframework.stereotype.Service;

@Service
public class UnitInfoServiceImpl extends ServiceImpl<UnitInfoMapper, UnitInfo> implements UnitInfoService {
}
