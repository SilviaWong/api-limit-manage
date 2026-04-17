package com.orivon.manage.service;

import com.orivon.manage.common.CryptoUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @description: 数据加解密服务 (支持 SM4, AES) 业务包装层
 * @author: yuowu
 * @create: 2026-04-03 23:10
 **/
@Service
public class CryptoService {

    /**
     * 根据策略加密内容
     * @param content 原始文本
     * @param key 密钥 (对应 mKey)
     * @param policy NONE, SM4, AES
     * @return 加密后的 Base64 字符串
     */
    public String encrypt(String content, String key, String policy) {
        if (!StringUtils.hasText(content) || "NONE".equalsIgnoreCase(policy)) {
            return content;
        }

        try {
            if ("SM4".equalsIgnoreCase(policy)) {
                return CryptoUtils.encryptSM4(content, key);
            } else if ("AES".equalsIgnoreCase(policy)) {
                return CryptoUtils.encryptAES(content, key);
            }
        } catch (Exception e) {
            System.err.println("加密异常 (" + policy + "): " + e.getMessage());
        }

        return content;
    }

    /**
     * 根据策略解密内容 
     * @param base64Content 密文
     * @param key 密钥 (对应 mKey)
     * @param policy NONE, SM4, AES
     * @return 解密后的原始文本
     */
    public String decrypt(String base64Content, String key, String policy) {
        if (!StringUtils.hasText(base64Content) || "NONE".equalsIgnoreCase(policy)) {
            return base64Content;
        }

        try {
            if ("SM4".equalsIgnoreCase(policy)) {
                return CryptoUtils.decryptSM4(base64Content, key);
            } else if ("AES".equalsIgnoreCase(policy)) {
                return CryptoUtils.decryptAES(base64Content, key);
            }
        } catch (Exception e) {
            System.err.println("解密异常 (" + policy + "): " + e.getMessage());
        }

        return base64Content;
    }
}
