package com.orivon.manage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orivon.manage.mapper.SysUserMapper;
import com.orivon.manage.model.entity.SysUser;
import com.orivon.manage.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
