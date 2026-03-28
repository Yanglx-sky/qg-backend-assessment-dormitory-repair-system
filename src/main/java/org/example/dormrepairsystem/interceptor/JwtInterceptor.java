package org.example.dormrepairsystem.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.dormrepairsystem.util.JwtUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    // 定义角色权限映射
    private static final Map<Integer, Set<String>> rolePermissions = new HashMap<>();

    static {
        // 初始化角色权限
        // 角色ID: 1 - 学生
        Set<String> studentPermissions = new HashSet<>();
        studentPermissions.add("/repair-orders"); // 提交报修
        studentPermissions.add("/repair-orders/{orderId}/images"); // 上传图片
        studentPermissions.add("/dormitories"); // 绑定宿舍
        rolePermissions.put(1, studentPermissions);

        // 角色ID: 2 - 管理员
        Set<String> adminPermissions = new HashSet<>();
        adminPermissions.add("/repair-orders"); // 管理订单
        adminPermissions.add("/repair-orders/{orderId}"); // 删除订单
        adminPermissions.add("/repair-orders/{orderId}/status"); // 修改状态
        adminPermissions.add("/users"); // 管理用户
        adminPermissions.add("/users/{userId}"); // 删除用户
        adminPermissions.add("/dormitories"); // 管理宿舍
        adminPermissions.add("/dormitories/{dormId}"); // 删除宿舍
        rolePermissions.put(2, adminPermissions);

        // 角色ID: 3 - 维修人员
        Set<String> repairmanPermissions = new HashSet<>();
        repairmanPermissions.add("/repair-orders"); // 查看订单
        repairmanPermissions.add("/repair-orders/{orderId}/accept"); // 接取订单
        rolePermissions.put(3, repairmanPermissions);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取Authorization字段
        String authorization = request.getHeader("Authorization");

        // 检查是否有Authorization头
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.info("token不存在或格式错误");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"未授权，请先登录\"}");
            return false;
        }

        // 提取令牌
        String token = authorization.substring(7);

        // 验证令牌是否有效
        Claims claims;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.info("token验证失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"令牌无效或已过期\"}");
            return false;
        }

        // 从token中获取角色ID
        Integer roleId = (Integer) claims.get("roleId");
        if (roleId == null) {
            log.info("token中缺少角色信息");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"令牌无效，缺少角色信息\"}");
            return false;
        }

        // 获取请求路径
        String requestPath = request.getRequestURI();
        log.info("用户角色ID: {}, 请求路径: {}", roleId, requestPath);

        // 检查角色是否有权限访问该路径
        if (!hasPermission(roleId, requestPath)) {
            log.info("角色 {} 无权限访问路径 {}", roleId, requestPath);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"权限不足，无法访问该资源\"}");
            return false;
        }

        log.info("token验证成功，角色 {} 有权限访问路径 {}", roleId, requestPath);
        return true;
    }

    /**
     * 检查角色是否有权限访问指定路径
     */
    private boolean hasPermission(Integer roleId, String requestPath) {
        // 获取角色对应的权限集合
        Set<String> permissions = rolePermissions.get(roleId);
        if (permissions == null) {
            return false;
        }

        // 检查路径是否匹配权限
        for (String permission : permissions) {
            // 处理路径参数，如 /repair-orders/{orderId}
            if (permission.contains("{")) {
                // 转换为正则表达式
                String regex = permission.replaceAll("\\{[^}]+\\}", "[\\d]+");
                if (requestPath.matches(regex)) {
                    return true;
                }
            } else {
                // 精确匹配
                if (requestPath.startsWith(permission)) {
                    return true;
                }
            }
        }

        return false;
    }
}