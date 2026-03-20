package org.example.dormrepairsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dormrepairsystem.dto.OrderWithUserDTO;
import org.example.dormrepairsystem.entity.RepairOrder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 报修单核心服务接口
 */
public interface RepairOrderService extends IService<RepairOrder> {
    // 根据用户ID查询个人报修记录（学生专属）
    List<RepairOrder> getByUserId(Long userId);
    // 根据报修单状态查询列表（管理员/维修人员筛选）
    List<RepairOrder> getByOrderStatus(String status);
    // 分页查询报修单（支持状态筛选，管理员/维修人员通用）
    IPage<RepairOrder> getPage(Page<RepairOrder> page, String status);
    
    // 接取订单（维修人员专属）
    boolean acceptOrder(Long orderId, Long repairmanId);
    
    // 根据维修人员ID查询订单（维修人员专属）
    List<RepairOrder> getByRepairmanId(Long repairmanId);
    
    // 获取所有订单（包含用户信息，管理员专属）
    List<OrderWithUserDTO> getAllOrdersWithUserInfo();
}