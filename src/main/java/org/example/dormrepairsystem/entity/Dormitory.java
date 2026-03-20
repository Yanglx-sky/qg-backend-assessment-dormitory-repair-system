package org.example.dormrepairsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 宿舍绑定实体类
 */
@TableName("dormitory")
@Data
public class Dormitory {
    /**
     * 宿舍主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long dormId;

    /**
     * 关联用户表主键ID（仅学生）
     */
    private Long userId;

    /**
     * 宿舍楼栋：如1栋/东区5栋
     */
    private String building;

    /**
     * 房间号：如502/301-2
     */
    private String roomNum;

    /**
     * 绑定时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
