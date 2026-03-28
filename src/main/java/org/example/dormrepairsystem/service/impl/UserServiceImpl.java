package org.example.dormrepairsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dormrepairsystem.entity.Dormitory;
import org.example.dormrepairsystem.entity.User;
import org.example.dormrepairsystem.mapper.UserMapper;
import org.example.dormrepairsystem.service.DormitoryService;
import org.example.dormrepairsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private DormitoryService dormitoryService;

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
        
        // 验证账号格式
        String account = user.getAccount();
        
        // 不允许使用admin作为账号
        if ("admin".equals(account)) {
            return false;
        }
        
        // 根据账号设置角色
        if (account.startsWith("3")) {
            // 3开头的账号是学生
            user.setRoleId(1);
        } else if (account.startsWith("2")) {
            // 2开头的账号是维修人员
            user.setRoleId(3);
        } else {
            // 其他账号格式错误
            return false;
        }
        
        // 加密密码
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        
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

    // 根据角色ID查询用户列表（包含宿舍信息）
    @Override
    public List<Map<String, Object>> getUsersWithDormitory(Integer roleId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRoleId, roleId);
        List<User> users = this.list(wrapper);
        return convertUsersToMapList(users);
    }

    // 查询所有用户列表（包含宿舍信息）
    @Override
    public List<Map<String, Object>> getAllUsersWithDormitory() {
        List<User> users = this.list();
        return convertUsersToMapList(users);
    }

    // 将用户列表转换为包含宿舍信息的Map列表
    private List<Map<String, Object>> convertUsersToMapList(List<User> users) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userId", user.getUserId());
            userMap.put("account", user.getAccount());
            userMap.put("userName", user.getUserName());
            userMap.put("roleId", user.getRoleId());
            userMap.put("createTime", user.getCreateTime());
            userMap.put("updateTime", user.getUpdateTime());
            
            // 只有学生角色才查询宿舍信息
            if (user.getRoleId() == 1) {
                Dormitory dormitory = dormitoryService.getByUserId(user.getUserId());
                if (dormitory != null) {
                    userMap.put("building", dormitory.getBuilding());
                    userMap.put("roomNum", dormitory.getRoomNum());
                }
            }
            
            result.add(userMap);
        }
        return result;
    }
}