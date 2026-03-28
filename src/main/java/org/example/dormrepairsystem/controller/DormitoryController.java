package org.example.dormrepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.dormrepairsystem.entity.Dormitory;
import org.example.dormrepairsystem.service.DormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dormitories")
@Slf4j
@Tag(name = "宿舍管理", description = "宿舍相关接口")
public class DormitoryController {

    @Autowired
    private DormitoryService dormitoryService;

    /**
     * 绑定宿舍
     */
    @PostMapping
    @Operation(summary = "绑定宿舍", description = "为用户绑定或更新宿舍信息")
    public ResponseEntity<Map<String, Object>> bindDormitory(
            @Parameter(description = "宿舍信息，包含用户ID、楼栋和房间号", required = true) @RequestBody Dormitory dormitory) {
        log.info("绑定宿舍：用户ID={}, 楼栋={}, 房间号={}", dormitory.getUserId(), dormitory.getBuilding(), dormitory.getRoomNum());
        Map<String, Object> result = new HashMap<>();
        try {
            // 先查询用户是否已绑定宿舍
            Dormitory existingDorm = dormitoryService.getByUserId(dormitory.getUserId());
            if (existingDorm != null) {
                // 如果已绑定，更新宿舍信息
                existingDorm.setBuilding(dormitory.getBuilding());
                existingDorm.setRoomNum(dormitory.getRoomNum());
                existingDorm.setUpdateTime(java.time.LocalDateTime.now());
                boolean success = dormitoryService.updateById(existingDorm);
                if (success) {
                    log.info("宿舍信息更新成功：用户ID={}", dormitory.getUserId());
                    result.put("success", true);
                    result.put("message", "宿舍信息更新成功");
                    result.put("data", existingDorm);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                } else {
                    log.warn("宿舍信息更新失败：用户ID={}", dormitory.getUserId());
                    result.put("success", false);
                    result.put("message", "宿舍信息更新失败");
                    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                // 如果未绑定，新增宿舍信息
                dormitory.setCreateTime(java.time.LocalDateTime.now());
                dormitory.setUpdateTime(java.time.LocalDateTime.now());
                boolean success = dormitoryService.save(dormitory);
                if (success) {
                    log.info("宿舍绑定成功：用户ID={}", dormitory.getUserId());
                    result.put("success", true);
                    result.put("message", "宿舍绑定成功");
                    result.put("data", dormitory);
                    return new ResponseEntity<>(result, HttpStatus.CREATED);
                } else {
                    log.warn("宿舍绑定失败：用户ID={}", dormitory.getUserId());
                    result.put("success", false);
                    result.put("message", "宿舍绑定失败");
                    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            log.error("操作失败：", e);
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 查询宿舍
     */
    @GetMapping
    @Operation(summary = "查询宿舍", description = "根据用户ID查询宿舍信息，或获取所有宿舍（支持分页）")
    public ResponseEntity<Map<String, Object>> listDormitories(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "页码，从1开始") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(required = false, defaultValue = "5") Integer size) {
        log.info("查询宿舍：用户ID={}, 页码={}, 每页大小={}", userId, page, size);
        Map<String, Object> result = new HashMap<>();
        
        if (userId != null) {
            // 根据用户ID查询
            Dormitory dormitory = dormitoryService.getByUserId(userId);
            log.info("查询成功：用户ID={}, 宿舍信息={}", userId, dormitory);
            result.put("success", true);
            result.put("data", dormitory);
        } else {
            // 使用MyBatis-Plus的分页功能
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Dormitory> pageObj = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
            com.baomidou.mybatisplus.core.metadata.IPage<Dormitory> pageResult;
            
            // 获取所有宿舍并分页
            List<Dormitory> dormitories = dormitoryService.list();
            // 手动分页
            int total = dormitories.size();
            int start = (page - 1) * size;
            int end = Math.min(start + size, total);
            List<Dormitory> pageList = dormitories.subList(start, end);
            
            // 构建分页响应
            pageResult = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
            pageResult.setRecords(pageList);
            pageResult.setTotal(total);
            pageResult.setCurrent(page);
            pageResult.setSize(size);
            pageResult.setPages((total + size - 1) / size);
            
            log.info("查询成功：宿舍总数量={}, 总页数={}", total, pageResult.getPages());
            result.put("success", true);
            result.put("data", pageResult);
        }
        
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    /**
     * 删除宿舍
     */
    @DeleteMapping("/{dormId}")
    @Operation(summary = "删除宿舍", description = "根据宿舍ID删除宿舍信息")
    public ResponseEntity<Map<String, Object>> deleteDormitory(
            @Parameter(description = "宿舍ID", required = true) @PathVariable Long dormId) {
        log.info("删除宿舍：宿舍ID={}", dormId);
        Map<String, Object> result = new HashMap<>();
        boolean success = dormitoryService.removeById(dormId);
        if (success) {
            log.info("删除成功：宿舍ID={}", dormId);
            result.put("success", true);
            result.put("message", "删除成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            log.warn("删除失败：宿舍ID={}", dormId);
            result.put("success", false);
            result.put("message", "删除失败");
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}