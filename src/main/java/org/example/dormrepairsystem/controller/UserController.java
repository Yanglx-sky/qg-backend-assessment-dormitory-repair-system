package org.example.dormrepairsystem.controller;

import org.example.dormrepairsystem.entity.User;
import org.example.dormrepairsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        User existingUser = userService.getByAccount(user.getAccount());
        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("user", existingUser);
        } else {
            result.put("success", false);
            result.put("message", "账号或密码错误");
        }
        return result;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userService.register(user);
        if (success) {
            result.put("success", true);
            result.put("message", "注册成功");
        } else {
            result.put("success", false);
            result.put("message", "账号已存在");
        }
        return result;
    }
    
    /**
     * 根据角色ID查询用户列表
     */
    @GetMapping("/listByRole/{roleId}")
    public Map<String, Object> listByRole(@PathVariable Integer roleId) {
        Map<String, Object> result = new HashMap<>();
        List<User> users = userService.getByRoleId(roleId);
        result.put("success", true);
        result.put("data", users);
        return result;
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{userId}")
    public Map<String, Object> delete(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userService.deleteUser(userId);
        if (success) {
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败，可能是admin账号");
        }
        return result;
    }
}