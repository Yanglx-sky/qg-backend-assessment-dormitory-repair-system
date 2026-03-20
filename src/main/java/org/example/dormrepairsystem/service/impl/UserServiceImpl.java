package org.example.dormrepairsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dormrepairsystem.entity.User;
import org.example.dormrepairsystem.mapper.UserMapper;
import org.example.dormrepairsystem.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 根据账号查询用户
    @Override
    public User getByAccount(String account) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, account);
        return this.getOne(wrapper);
    }

    // 注册用户
    @Override
    public boolean register(User user) {
        // 检查账号是否已存在
        if (getByAccount(user.getAccount()) != null) {
            return false;
        }
        
        // 验证账号格式：admin账号不能注册
        String account = user.getAccount();
        if (account.equals("admin")) {
            return false;
        }
        
        // 根据账号前缀自动判断角色
        if (account.startsWith("3")) {
            // 3开头的账号是学生
            user.setRoleId(1);
        } else if (account.startsWith("2")) {
            // 2开头的账号是维修人员
            user.setRoleId(3);
        } else {
            // 其他账号默认为学生
            user.setRoleId(1);
        }
        
        // 保存用户
        return this.save(user);
    }
    
    // 根据角色ID查询用户列表
    @Override
    public List<User> getByRoleId(Integer roleId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRoleId, roleId);
        return this.list(wrapper);
    }
    
    // 删除用户
    @Override
    public boolean deleteUser(Long userId) {
        // 不允许删除admin账号
        User user = this.getById(userId);
        if (user != null && "admin".equals(user.getAccount())) {
            return false;
        }
        return this.removeById(userId);
    }
}