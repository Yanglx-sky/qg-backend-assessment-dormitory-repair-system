package org.example.dormrepairsystem.controller;

import org.example.dormrepairsystem.entity.Role;
import org.example.dormrepairsystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // 查询所有角色
    @GetMapping("/list")
    public List<Role> list() {
        return roleService.list();
    }

    // 根据角色编码查询角色
    @GetMapping("/code/{roleCode}")
    public Role getByRoleCode(@PathVariable String roleCode) {
        return roleService.getByRoleCode(roleCode);
    }
}