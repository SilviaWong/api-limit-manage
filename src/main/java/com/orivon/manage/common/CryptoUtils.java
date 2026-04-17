package com.orivon.manage.common;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * @description: 通用加密解密工具类，包含 AES, SM4, MD5 等
 * @author: yuowu
 * @create: 2026-04-14
 **/
public class CryptoUtils {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 确保密钥长度为 16 字节
     */
    private static byte[] get16ByteKey(String key) {
        if (key == null) key = "";
        return Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), 16);
    }

    // ================================= AES =================================

    /**
     * AES 加密 (AES/ECB/PKCS5Padding)
     * @param content 待加密文本
     * @param key 密钥
     * @return Base64 编码的密文
     */
    public static String encryptAES(String content, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(get16ByteKey(key), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * AES 解密 (AES/ECB/PKCS5Padding)
     * @param base64Content Base64 编码的密文
     * @param key 密钥
     * @return 解密后的原文
     */
    public static String decryptAES(String base64Content, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(get16ByteKey(key), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(base64Content));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ================================= SM4 =================================

    /**
     * SM4 加密 (SM4/ECB/PKCS5Padding)
     * @param content 待加密文本
     * @param key 密钥
     * @return Base64 编码的密文
     */
    public static String encryptSM4(String content, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", BouncyCastleProvider.PROVIDER_NAME);
        SecretKeySpec keySpec = new SecretKeySpec(get16ByteKey(key), "SM4");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * SM4 解密 (SM4/ECB/PKCS5Padding)
     * @param base64Content Base64 编码的密文
     * @param key 密钥
     * @return 解密后的原文
     */
    public static String decryptSM4(String base64Content, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", BouncyCastleProvider.PROVIDER_NAME);
        SecretKeySpec keySpec = new SecretKeySpec(get16ByteKey(key), "SM4");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(base64Content));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ================================= MD5 =================================

    /**
     * MD5 摘要签名
     * @param content 待签名文本
     * @return 签名字符串 (Hex小写)
     */
    public static String md5Hex(String content) {
        if (content == null) return null;
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

    // ================================= RSA =================================

    /**
     * 生成 RSA 密钥对 (2048位)
     * @return 包含公钥和私钥 Base64 字符串的数组: [publicKeyBase64, privateKeyBase64]
     */
    public static String[] generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        return new String[]{publicKey, privateKey};
    }

    /**
     * RSA 公钥加密
     * @param content 待加密文本
     * @param publicKeyBase64 Base64 编码的公钥 (X509格式)
     * @return Base64 编码的密文
     */
    public static String encryptRSA(String content, String publicKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * RSA 私钥解密
     * @param base64Content Base64 编码的密文
     * @param privateKeyBase64 Base64 编码的私钥 (PKCS8格式)
     * @return 解密后的原文
     */
    public static String decryptRSA(String base64Content, String privateKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(base64Content));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
