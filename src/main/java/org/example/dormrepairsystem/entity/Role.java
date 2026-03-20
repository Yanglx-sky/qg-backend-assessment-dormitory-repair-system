package org.example.dormrepairsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@TableName("role")
@Data
public class Role {
    /**
     * 角色主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer roleId;

    /**
     * 角色名称：学生/维修人员/管理员
     */
    private String roleName;

    /**
     * 角色编码：student/repairman/admin
     */
    private String roleCode;

    /**
     * 创建时间（数据库默认当前时间）
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（数据库自动更新）
     */
    private LocalDateTime updateTime;
}