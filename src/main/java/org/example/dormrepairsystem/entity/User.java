package org.example.dormrepairsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类（学生/维修人员/管理员）
 */
@TableName("user")
@Data
public class User {
    /**
     * 用户主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 账号：学生学号（3125/3225前缀）/工号（0025前缀）
     */
    private String account;

    /**
     * 密码（预留加密存储，建议BCrypt）
     */
    private String password;

    /**
     * 关联角色表主键ID
     */
    private Integer roleId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}