package org.example.dormrepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.dormrepairsystem.dto.OrderWithUserDTO;
import org.example.dormrepairsystem.entity.RepairOrder;
import org.example.dormrepairsystem.entity.OrderImage;
import org.example.dormrepairsystem.service.RepairOrderService;
import org.example.dormrepairsystem.service.DormitoryService;
import org.example.dormrepairsystem.service.OrderImageService;
import org.example.dormrepairsystem.util.OSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repair-orders")
@Slf4j
@Tag(name = "维修订单管理", description = "维修订单相关接口")
public class RepairOrderController {

    @Autowired
    private RepairOrderService repairOrderService;
    
    @Autowired
    private DormitoryService dormitoryService;
    
    @Autowired
    private OrderImageService orderImageService;
    
    @Autowired
    private OSSUtil ossUtil;

    /**
     * 提交报修
     */
    @PostMapping
    @Operation(summary = "提交报修", description = "学生提交新的报修申请")
    public ResponseEntity<Map<String, Object>> createOrder(
            @Parameter(description = "报修订单信息", required = true) @RequestBody RepairOrder order) {
        log.info("提交报修：用户ID={}, 报修内容={}", order.getUserId(), order.getProblemDesc());
        Map<String, Object> result = new HashMap<>();
        
        // 根据用户ID查询宿舍信息
        Long userId = order.getUserId();
        if (userId == null) {
            log.warn("提交报修失败：用户ID不能为空");
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        
        // 查询用户绑定的宿舍信息
        org.example.dormrepairsystem.entity.Dormitory dormitory = dormitoryService.getByUserId(userId);
        if (dormitory == null) {
            log.warn("提交报修失败：用户未绑定宿舍，用户ID={}", userId);
            result.put("success", false);
            result.put("message", "请先绑定宿舍");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        
        // 设置宿舍ID、楼栋和房间号
        order.setDormId(dormitory.getDormId());
        order.setBuilding(dormitory.getBuilding());
        order.setRoomNum(dormitory.getRoomNum());
        order.setOrderStatus("待处理");
        
        boolean success = repairOrderService.save(order);
        if (success) {
            log.info("提交报修成功：订单ID={}", order.getOrderId());
            result.put("success", true);
            result.put("message", "报修成功");
            result.put("data", order);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } else {
            log.warn("提交报修失败：用户ID={}", userId);
            result.put("success", false);
            result.put("message", "报修失败");
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 查询报修单列表
     */
    @GetMapping
    @Operation(summary = "查询报修单列表", description = "根据条件查询报修单列表，支持分页")
    public ResponseEntity<Map<String, Object>> listOrders(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "订单状态") @RequestParam(required = false) String status,
            @Parameter(description = "维修人员ID") @RequestParam(required = false) Long repairmanId,
            @Parameter(description = "是否包含用户信息") @RequestParam(required = false) Boolean includeUserInfo,
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小，默认5") @RequestParam(defaultValue = "5") Integer size
    ) {
        log.info("查询报修单列表：用户ID={}, 状态={}, 维修人员ID={}, 包含用户信息={}, 页码={}, 每页大小={}", 
                userId, status, repairmanId, includeUserInfo, page, size);
        Map<String, Object> result = new HashMap<>();
        
        // 如果指定了includeUserInfo且为true，返回包含用户信息的订单
        if (Boolean.TRUE.equals(includeUserInfo)) {
            List<OrderWithUserDTO> orders = repairOrderService.getAllOrdersWithUserInfo();
            log.info("查询成功：总记录数={}", orders.size());
            result.put("success", true);
            result.put("data", orders);
        } else {
            // 普通查询
            if (userId != null) {
                // 根据用户ID查询
                List<RepairOrder> orders = repairOrderService.getByUserId(userId);
                log.info("查询成功：用户ID={}, 订单数量={}", userId, orders.size());
                result.put("success", true);
                result.put("data", orders);
            } else if (repairmanId != null) {
                // 根据维修人员ID查询
                List<RepairOrder> orders = repairOrderService.getByRepairmanId(repairmanId);
                log.info("查询成功：维修人员ID={}, 订单数量={}", repairmanId, orders.size());
                result.put("success", true);
                result.put("data", orders);
            } else if (status != null) {
                // 根据状态查询
                List<RepairOrder> orders = repairOrderService.getByOrderStatus(status);
                log.info("查询成功：状态={}, 订单数量={}", status, orders.size());
                result.put("success", true);
                result.put("data", orders);
            } else {
                // 分页查询所有
                com.baomidou.mybatisplus.extension.plugins.pagination.Page<RepairOrder> pageObj = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
                com.baomidou.mybatisplus.core.metadata.IPage<RepairOrder> pageResult = repairOrderService.getPage(pageObj, status);
                log.info("查询成功：总记录数={}, 总页数={}", pageResult.getTotal(), pageResult.getPages());
                result.put("success", true);
                result.put("data", pageResult);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 修改报修状态
     */
    @PutMapping("/{orderId}/status")
    @Operation(summary = "修改报修状态", description = "更新报修订单的状态")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "状态更新信息，包含status字段", required = true) @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        log.info("修改报修状态：订单ID={}, 新状态={}", orderId, newStatus);
        Map<String, Object> result = new HashMap<>();
        
        if (newStatus == null) {
            result.put("success", false);
            result.put("message", "状态不能为空");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        
        RepairOrder order = new RepairOrder();
        order.setOrderId(orderId);
        order.setOrderStatus(newStatus);
        
        boolean success = repairOrderService.updateById(order);
        if (success) {
            log.info("修改成功：订单ID={}", orderId);
            result.put("success", true);
            result.put("message", "修改成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            log.warn("修改失败：订单ID={}", orderId);
            result.put("success", false);
            result.put("message", "修改失败");
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除报修单
     */
    @DeleteMapping("/{orderId}")
    @Operation(summary = "删除报修单", description = "根据订单ID删除报修单")
    public ResponseEntity<Map<String, Object>> deleteOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId) {
        log.info("删除报修单：订单ID={}", orderId);
        Map<String, Object> result = new HashMap<>();
        boolean success = repairOrderService.removeById(orderId);
        if (success) {
            log.info("删除成功：订单ID={}", orderId);
            result.put("success", true);
            result.put("message", "删除成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            log.warn("删除失败：订单ID={}", orderId);
            result.put("success", false);
            result.put("message", "删除失败");
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 接取订单
     */
    @PostMapping("/{orderId}/accept")
    @Operation(summary = "接取订单", description = "维修人员接取报修订单")
    public ResponseEntity<Map<String, Object>> acceptOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "接取信息，包含repairmanId字段", required = true) @RequestBody Map<String, Long> params) {
        Long repairmanId = params.get("repairmanId");
        log.info("接取订单：订单ID={}, 维修人员ID={}", orderId, repairmanId);
        Map<String, Object> result = new HashMap<>();
        
        if (repairmanId == null) {
            log.warn("接取订单失败：维修人员ID不能为空");
            result.put("success", false);
            result.put("message", "维修人员ID不能为空");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        
        boolean success = repairOrderService.acceptOrder(orderId, repairmanId);
        if (success) {
            log.info("接取订单成功：订单ID={}", orderId);
            result.put("success", true);
            result.put("message", "接取订单成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            log.warn("接取订单失败：订单ID={}", orderId);
            result.put("success", false);
            result.put("message", "接取订单失败，订单可能已被其他维修人员接取");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 上传报修图片
     */
    @PostMapping("/{orderId}/images")
    @Operation(summary = "上传报修图片", description = "为报修订单上传图片")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "图片文件", required = true) @RequestParam("file") MultipartFile file) {
        log.info("上传报修图片：订单ID={}, 文件名={}", orderId, file.getOriginalFilename());
        Map<String, Object> result = new HashMap<>();
        
        if (file.isEmpty()) {
            log.warn("上传失败：文件为空");
            result.put("success", false);
            result.put("message", "文件不能为空");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        
        // 检查订单是否存在
        RepairOrder order = repairOrderService.getById(orderId);
        if (order == null) {
            log.warn("上传失败：订单不存在，订单ID={}", orderId);
            result.put("success", false);
            result.put("message", "订单不存在");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        
        try {
            // 上传图片到阿里云OSS
            String imageUrl = ossUtil.uploadImage(file, "repair-orders");
            
            // 保存图片信息到数据库
            OrderImage orderImage = new OrderImage();
            orderImage.setOrderId(orderId);
            orderImage.setImageUrl(imageUrl);
            orderImage.setCreateTime(LocalDateTime.now());
            
            boolean saveSuccess = orderImageService.saveOrderImage(orderImage);
            if (saveSuccess) {
                log.info("上传成功：订单ID={}, 图片URL={}", orderId, orderImage.getImageUrl());
                result.put("success", true);
                result.put("message", "上传成功");
                result.put("data", orderImage);
                return new ResponseEntity<>(result, HttpStatus.CREATED);
            } else {
                log.warn("上传失败：保存图片信息失败，订单ID={}", orderId);
                result.put("success", false);
                result.put("message", "上传失败，请稍后重试");
                return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            log.error("上传失败：文件保存失败", e);
            result.put("success", false);
            result.put("message", "上传失败：" + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取订单图片列表
     */
    @GetMapping("/{orderId}/images")
    @Operation(summary = "获取订单图片列表", description = "获取指定订单的图片列表")
    public ResponseEntity<Map<String, Object>> getOrderImages(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId) {
        log.info("获取订单图片列表：订单ID={}", orderId);
        Map<String, Object> result = new HashMap<>();
        
        List<OrderImage> images = orderImageService.getByOrderId(orderId);
        log.info("查询成功：订单ID={}, 图片数量={}", orderId, images.size());
        result.put("success", true);
        result.put("data", images);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    /**
     * 删除订单图片
     */
    @DeleteMapping("/images/{imageId}")
    @Operation(summary = "删除订单图片", description = "删除指定的订单图片")
    public ResponseEntity<Map<String, Object>> deleteImage(
            @Parameter(description = "图片ID", required = true) @PathVariable Long imageId) {
        log.info("删除订单图片：图片ID={}", imageId);
        Map<String, Object> result = new HashMap<>();
        
        // 获取图片信息
        OrderImage orderImage = orderImageService.getById(imageId);
        if (orderImage == null) {
            log.warn("删除失败：图片不存在，图片ID={}", imageId);
            result.put("success", false);
            result.put("message", "图片不存在");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        
        try {
            // 从OSS删除图片
            ossUtil.deleteImage(orderImage.getImageUrl());
            
            // 从数据库删除记录
            boolean success = orderImageService.removeById(imageId);
            if (success) {
                log.info("删除成功：图片ID={}, 图片URL={}", imageId, orderImage.getImageUrl());
                result.put("success", true);
                result.put("message", "删除成功");
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                log.warn("删除失败：图片ID={}", imageId);
                result.put("success", false);
                result.put("message", "删除失败");
                return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("删除失败：OSS删除失败", e);
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}