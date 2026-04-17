package com.orivon.manage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orivon.manage.mapper.ApiAuthMapper;
import com.orivon.manage.model.entity.ApiAuth;
import com.orivon.manage.service.ApiAuthService;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class ApiAuthServiceImpl extends ServiceImpl<ApiAuthMapper, ApiAuth> implements ApiAuthService {

    @Override
    public boolean createAuth(ApiAuth apiAuth) {
        // 业务逻辑集中在这里处理
        if (apiAuth.getUKey() == null) {
            apiAuth.setUKey(generateKey());
        }
        if (apiAuth.getMKey() == null) {
            apiAuth.setMKey(generateMKey());
        }
        
        // 设置初始状态并保存
        apiAuth.setStatus(1); // 默认启用
        
        return this.save(apiAuth);
    }

    private String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generateMKey() {
        String key = "JK"+System.currentTimeMillis();
        return getMD5Str(key);
    }

    /**
     * MD5加密
     * @param str
     * @return
     */
    public static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest  = md5.digest(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        String md5Str = new BigInteger(1, digest).toString(16);
        return md5Str;
    }
}
