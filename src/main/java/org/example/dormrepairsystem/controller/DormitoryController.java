package org.example.dormrepairsystem.controller;

import org.example.dormrepairsystem.entity.Dormitory;
import org.example.dormrepairsystem.service.DormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dormitory")
public class DormitoryController {

    @Autowired
    private DormitoryService dormitoryService;

    /**
     * 绑定宿舍
     */
    @PostMapping("/bind")
    public Map<String, Object> bindDormitory(@RequestBody Dormitory dormitory) {
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
                    result.put("success", true);
                    result.put("message", "宿舍信息更新成功");
                } else {
                    result.put("success", false);
                    result.put("message", "宿舍信息更新失败");
                }
            } else {
                // 如果未绑定，新增宿舍信息
                dormitory.setCreateTime(java.time.LocalDateTime.now());
                dormitory.setUpdateTime(java.time.LocalDateTime.now());
                boolean success = dormitoryService.save(dormitory);
                if (success) {
                    result.put("success", true);
                    result.put("message", "宿舍绑定成功");
                } else {
                    result.put("success", false);
                    result.put("message", "宿舍绑定失败");
                }
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 根据用户ID查询宿舍
     */
    @GetMapping("/getByUserId/{userId}")
    public Map<String, Object> getByUserId(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        Dormitory dormitory = dormitoryService.getByUserId(userId);
        result.put("success", true);
        result.put("data", dormitory);
        return result;
    }
    
    /**
     * 获取所有宿舍
     */
    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        java.util.List<Dormitory> dormitories = dormitoryService.list();
        result.put("success", true);
        result.put("data", dormitories);
        return result;
    }
    
    /**
     * 删除宿舍
     */
    @DeleteMapping("/delete/{dormId}")
    public Map<String, Object> delete(@PathVariable Long dormId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = dormitoryService.removeById(dormId);
        if (success) {
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败");
        }
        return result;
    }
}