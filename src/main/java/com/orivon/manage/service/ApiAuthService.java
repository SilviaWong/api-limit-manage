package com.orivon.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.orivon.manage.model.entity.ApiAuth;

public interface ApiAuthService extends IService<ApiAuth> {
    /**
     * 创建新的接口授权，包含 uKey/mKey 生成等业务逻辑
     */
    boolean createAuth(ApiAuth apiAuth);
}
