package org.example.dormrepairsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dormrepairsystem.entity.User;

import java.util.List;

/**
 * 用户核心服务接口
 */
public interface UserService extends IService<User> {
    // 根据账号查询用户
    User getByAccount(String account);
    // 注册用户
    boolean register(User user);
    // 根据角色ID查询用户列表
    List<User> getByRoleId(Integer roleId);
    // 删除用户
    boolean deleteUser(Long userId);
}