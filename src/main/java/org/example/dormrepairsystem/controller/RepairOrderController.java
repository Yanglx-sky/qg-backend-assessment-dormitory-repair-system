package org.example.dormrepairsystem.controller;

import org.example.dormrepairsystem.dto.OrderWithUserDTO;
import org.example.dormrepairsystem.entity.RepairOrder;
import org.example.dormrepairsystem.service.RepairOrderService;
import org.example.dormrepairsystem.service.DormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repairOrder")
public class RepairOrderController {

    @Autowired
    private RepairOrderService repairOrderService;
    
    @Autowired
    private DormitoryService dormitoryService;

    /**
     * 提交报修
     */
    @PostMapping("/add")
    public Map<String, Object> addOrder(@RequestBody RepairOrder order) {
        Map<String, Object> result = new HashMap<>();
        
        // 根据用户ID查询宿舍信息
        Long userId = order.getUserId();
        if (userId == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }
        
        // 查询用户绑定的宿舍信息
        org.example.dormrepairsystem.entity.Dormitory dormitory = dormitoryService.getByUserId(userId);
        if (dormitory == null) {
            result.put("success", false);
            result.put("message", "请先绑定宿舍");
            return result;
        }
        
        // 设置宿舍ID、楼栋和房间号
        order.setDormId(dormitory.getDormId());
        order.setBuilding(dormitory.getBuilding());
        order.setRoomNum(dormitory.getRoomNum());
        order.setOrderStatus("待处理");
        
        boolean success = repairOrderService.save(order);
        if (success) {
            result.put("success", true);
            result.put("message", "报修成功");
        } else {
            result.put("success", false);
            result.put("message", "报修失败");
        }
        return result;
    }

    /**
     * 根据用户ID查询报修单
     */
    @GetMapping("/getByUserId/{userId}")
    public Map<String, Object> getByUserId(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<RepairOrder> orders = repairOrderService.getByUserId(userId);
        result.put("success", true);
        result.put("data", orders);
        return result;
    }

    /**
     * 根据状态查询报修单
     */
    @GetMapping("/getByStatus/{status}")
    public Map<String, Object> getByStatus(@PathVariable String status) {
        Map<String, Object> result = new HashMap<>();
        List<RepairOrder> orders = repairOrderService.getByOrderStatus(status);
        result.put("success", true);
        result.put("data", orders);
        return result;
    }

    /**
     * 获取所有报修单
     */
    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        List<RepairOrder> orders = repairOrderService.list();
        result.put("success", true);
        result.put("data", orders);
        return result;
    }
    
    /**
     * 获取所有报修单（包含用户信息，管理员专属）
     */
    @GetMapping("/listWithUserInfo")
    public Map<String, Object> listWithUserInfo() {
        Map<String, Object> result = new HashMap<>();
        List<OrderWithUserDTO> orders = repairOrderService.getAllOrdersWithUserInfo();
        result.put("success", true);
        result.put("data", orders);
        return result;
    }

    /**
     * 修改报修状态
     */
    @PostMapping("/updateStatus")
    public Map<String, Object> updateStatus(@RequestBody RepairOrder order) {
        Map<String, Object> result = new HashMap<>();
        boolean success = repairOrderService.updateById(order);
        if (success) {
            result.put("success", true);
            result.put("message", "修改成功");
        } else {
            result.put("success", false);
            result.put("message", "修改失败");
        }
        return result;
    }

    /**
     * 删除报修单
     */
    @DeleteMapping("/delete/{orderId}")
    public Map<String, Object> delete(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = repairOrderService.removeById(orderId);
        if (success) {
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败");
        }
        return result;
    }
    
    /**
     * 接取订单
     */
    @PostMapping("/accept")
    public Map<String, Object> acceptOrder(@RequestBody Map<String, Long> params) {
        Map<String, Object> result = new HashMap<>();
        Long orderId = params.get("orderId");
        Long repairmanId = params.get("repairmanId");
        
        if (orderId == null || repairmanId == null) {
            result.put("success", false);
            result.put("message", "订单ID和维修人员ID不能为空");
            return result;
        }
        
        boolean success = repairOrderService.acceptOrder(orderId, repairmanId);
        if (success) {
            result.put("success", true);
            result.put("message", "接取订单成功");
        } else {
            result.put("success", false);
            result.put("message", "接取订单失败，订单可能已被其他维修人员接取");
        }
        return result;
    }
    
    /**
     * 根据维修人员ID查询订单
     */
    @GetMapping("/getByRepairmanId/{repairmanId}")
    public Map<String, Object> getByRepairmanId(@PathVariable Long repairmanId) {
        Map<String, Object> result = new HashMap<>();
        List<RepairOrder> orders = repairOrderService.getByRepairmanId(repairmanId);
        result.put("success", true);
        result.put("data", orders);
        return result;
    }
}