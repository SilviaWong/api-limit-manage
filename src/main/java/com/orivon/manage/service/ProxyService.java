package com.orivon.manage.service;

import com.orivon.manage.common.CryptoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orivon.manage.model.entity.ApiAuth;
import com.orivon.manage.model.entity.ApiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 接口转发逻辑处理服务
 * @author: yuowu
 * @create: 2026-04-03 23:15
 **/
@Service
public class ProxyService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 执行转发调用
     * @param apiInfo 市级接口元数据
     * @param apiAuth 授权配置 (包含 uKey/mKey)
     * @param businessParams 业务请求参数
     * @return 最终响应结果 (已加密，根据策略)
     */
    public Object doForward(ApiInfo apiInfo, ApiAuth apiAuth, Map<String, Object> businessParams) {
        // 1. 组装市级接口最终入参 (补全固定参数)
        Map<String, Object> finalParams = new HashMap<>();
        if (businessParams != null) {
            finalParams.putAll(businessParams);
        }
        
        long timestamp = System.currentTimeMillis();
        finalParams.put("appKey", apiInfo.getAppKey());
        finalParams.put("serviceCode", apiInfo.getServiceCode());
        finalParams.put("timestamp", timestamp);
        
        // 简单模拟市级接口签名校验 (MD5: appKey + serviceCode + timestamp + appSecret)
        String dataToHash = apiInfo.getAppKey() + apiInfo.getServiceCode() + timestamp + apiInfo.getAppSecret();
        String signature = CryptoUtils.md5Hex(dataToHash);
        finalParams.put("signature", signature);

        // 2. 执行转发 (目前以 POST JSON 为主)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(finalParams, headers);

        String resultStr;
        try {
            // 注意: 这里应该根据 apiInfo.getUrl() 执行请求，如果 url 为空则调模拟地址
            String url = apiInfo.getUrl();
            if (url == null || url.isEmpty()) {
                url = "http://localhost:8080/api/test"; // 容错/测试地址
            }
            resultStr = restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            return "调用市级接口异常: " + e.getMessage();
        }

        // 3. 处理结果 (根据授权策略执行加密)
        if (resultStr != null && !"NONE".equalsIgnoreCase(apiAuth.getCryptoPolicy())) {
            return cryptoService.encrypt(resultStr, apiAuth.getMKey(), apiAuth.getCryptoPolicy());
        }

        return resultStr;
    }
}
