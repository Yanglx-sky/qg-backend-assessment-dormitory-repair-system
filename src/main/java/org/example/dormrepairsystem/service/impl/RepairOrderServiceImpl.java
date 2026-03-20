package org.example.dormrepairsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dormrepairsystem.dto.OrderWithUserDTO;
import org.example.dormrepairsystem.entity.RepairOrder;
import org.example.dormrepairsystem.entity.User;
import org.example.dormrepairsystem.mapper.RepairOrderMapper;
import org.example.dormrepairsystem.service.RepairOrderService;
import org.example.dormrepairsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 报修单核心服务实现类
 */
@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService {

    @Autowired
    private UserService userService;

    // 根据用户ID查询个人报修记录
    @Override
    public List<RepairOrder> getByUserId(Long userId) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getUserId, userId);
        wrapper.orderByDesc(RepairOrder::getCreateTime); // 按创建时间倒序
        return this.list(wrapper);
    }

    // 根据状态查询报修单列表
    @Override
    public List<RepairOrder> getByOrderStatus(String status) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getOrderStatus, status);
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        return this.list(wrapper);
    }

    // 分页查询（支持状态筛选，status为null则查全部）
    @Override
    public IPage<RepairOrder> getPage(Page<RepairOrder> page, String status) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(RepairOrder::getOrderStatus, status);
        }
        wrapper.orderByDesc(RepairOrder::getUpdateTime); // 按最后修改时间倒序
        return this.page(page, wrapper);
    }
    
    // 接取订单
    @Override
    public boolean acceptOrder(Long orderId, Long repairmanId) {
        RepairOrder order = this.getById(orderId);
        if (order != null && "待处理".equals(order.getOrderStatus())) {
            order.setRepairmanId(repairmanId);
            order.setOrderStatus("维修中");
            order.setUpdateTime(java.time.LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }
    
    // 根据维修人员ID查询订单
    @Override
    public List<RepairOrder> getByRepairmanId(Long repairmanId) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getRepairmanId, repairmanId);
        wrapper.orderByDesc(RepairOrder::getUpdateTime); // 按最后修改时间倒序
        return this.list(wrapper);
    }
    
    // 获取所有订单（包含用户信息，管理员专属）
    @Override
    public List<OrderWithUserDTO> getAllOrdersWithUserInfo() {
        // 查询所有订单
        List<RepairOrder> orders = this.list();
        List<OrderWithUserDTO> dtos = new ArrayList<>();
        
        // 遍历订单，查询用户信息
        for (RepairOrder order : orders) {
            OrderWithUserDTO dto = new OrderWithUserDTO();
            // 复制订单基本信息
            dto.setOrderId(order.getOrderId());
            dto.setUserId(order.getUserId());
            dto.setDormId(order.getDormId());
            dto.setBuilding(order.getBuilding());
            dto.setRoomNum(order.getRoomNum());
            dto.setRepairmanId(order.getRepairmanId());
            dto.setDeviceType(order.getDeviceType());
            dto.setProblemDesc(order.getProblemDesc());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setCreateTime(order.getCreateTime());
            dto.setUpdateTime(order.getUpdateTime());
            
            // 查询学生信息
            if (order.getUserId() != null) {
                User student = userService.getById(order.getUserId());
                if (student != null) {
                    dto.setUserName(student.getUserName());
                }
            }
            
            // 查询维修人员信息
            if (order.getRepairmanId() != null) {
                User repairman = userService.getById(order.getRepairmanId());
                if (repairman != null) {
                    dto.setRepairmanName(repairman.getUserName());
                }
            }
            
            dtos.add(dto);
        }
        
        return dtos;
    }
}