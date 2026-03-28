package org.example.dormrepairsystem.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT令牌操作工具类
 * 提供JWT令牌的生成和解析功能
 */
public class JwtUtil {

    /**
     * 生成JWT签名密钥
     * 使用HMAC-SHA256算法需要至少256位密钥
     * Keys.secretKeyFor()方法会自动生成安全的密钥
     */
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * 访问令牌过期时间：2小时
     * 单位：毫秒
     */
    private static final long ACCESS_TOKEN_EXPIRATION = 2 * 60 * 60 * 1000;

    /**
     * 刷新令牌过期时间：7天
     * 单位：毫秒
     */
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    /**
     * 生成访问令牌
     * @param claims 要存储在令牌中的声明信息（键值对形式）
     * @return 生成的访问令牌字符串
     */
    public static String generateAccessToken(Map<String, Object> claims) {
        // 设置令牌过期时间：当前时间 + 访问令牌过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION);

        // 使用Jwts.builder()构建JWT令牌
        String token = Jwts.builder()
                .setClaims(claims) // 设置自定义声明信息
                .setExpiration(expirationDate) // 设置令牌过期时间
                .signWith(SECRET_KEY) // 使用密钥和算法进行签名
                .compact(); // 生成最终的JWT令牌字符串

        return token;
    }

    /**
     * 生成刷新令牌
     * @param claims 要存储在令牌中的声明信息（键值对形式）
     * @return 生成的刷新令牌字符串
     */
    public static String generateRefreshToken(Map<String, Object> claims) {
        // 设置令牌过期时间：当前时间 + 刷新令牌过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION);

        // 使用Jwts.builder()构建JWT令牌
        String token = Jwts.builder()
                .setClaims(claims) // 设置自定义声明信息
                .setExpiration(expirationDate) // 设置令牌过期时间
                .signWith(SECRET_KEY) // 使用密钥和算法进行签名
                .compact(); // 生成最终的JWT令牌字符串

        return token;
    }

    /**
     * 解析JWT令牌
     * @param token 要解析的JWT令牌字符串
     * @return 令牌中的声明信息
     * @throws Exception 如果令牌无效或过期，会抛出异常
     */
    public static Claims parseToken(String token) {
        // 使用jjwt 0.12.6版本的解析方式
        Claims claims = Jwts.parser() // 创建解析器构建器
                .verifyWith(SECRET_KEY)      // 设置用于验证签名的密钥
                .build()                     // 构建解析器实例
                .parseSignedClaims(token)    // 解析已签名的JWT令牌
                .getPayload();               // 获取令牌中的声明信息

        return claims;
    }

    /**
     * 使用刷新令牌获取新的访问令牌
     * @param refreshToken 刷新令牌字符串
     * @return 新的访问令牌字符串
     * @throws Exception 如果刷新令牌无效，会抛出异常
     */
    public static String refreshAccessToken(String refreshToken) throws Exception {
        // 解析刷新令牌，获取声明信息
        Claims claims = parseToken(refreshToken);
        
        // 移除过期时间声明，重新生成访问令牌
        claims.remove("exp");
        
        // 生成新的访问令牌
        return generateAccessToken(claims);
    }

    /**
     * 检查访问令牌是否即将过期（剩余时间小于30分钟）
     * @param token 访问令牌字符串
     * @return 是否即将过期
     */
    public static boolean isAccessTokenExpiringSoon(String token) {
        try {
            Claims claims = parseToken(token);
            Date expirationDate = claims.getExpiration();
            long currentTime = System.currentTimeMillis();
            long timeUntilExpiration = expirationDate.getTime() - currentTime;
            // 如果剩余时间小于30分钟（1800000毫秒），则认为即将过期
            return timeUntilExpiration < 1800000;
        } catch (Exception e) {
            return true; // 如果解析失败，认为令牌已过期
        }
    }
}