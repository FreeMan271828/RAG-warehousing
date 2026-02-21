package com.tobacco.warehouse.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.system.entity.SysRole;
import com.tobacco.warehouse.modules.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统角色管理 Controller
 *
 * @author warehouse
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<IPage<SysRole>> getPage(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            SysRole query) {
        IPage<SysRole> page = sysRoleService.pageRoles(pageNum, pageSize, query);
        return Result.success(page);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable(name = "id") Long id) {
        SysRole role = sysRoleService.getById(id);
        return Result.success(role);
    }

    @Operation(summary = "获取所有启用状态的角色列表")
    @GetMapping("/list")
    public Result<List<SysRole>> getList() {
        List<SysRole> list = sysRoleService.listEnabledRoles();
        return Result.success(list);
    }

    @Operation(summary = "创建角色")
    @PostMapping
    public Result<Boolean> create(@RequestBody SysRole role) {
        sysRoleService.addRole(role);
        return Result.success(true);
    }

    @Operation(summary = "更新角色")
    @PutMapping
    public Result<Boolean> update(@RequestBody SysRole role) {
        sysRoleService.updateRole(role);
        return Result.success(true);
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        sysRoleService.deleteRole(id);
        return Result.success(true);
    }

    @Operation(summary = "分配角色权限")
    @PutMapping("/{id}/permissions")
    public Result<Boolean> assignPermissions(
            @PathVariable(name = "id") Long id,
            @RequestBody List<Long> permissionIds) {
        sysRoleService.assignPermissions(id, permissionIds);
        return Result.success(true);
    }

    @Operation(summary = "获取角色权限ID列表")
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissions(@PathVariable(name = "id") Long id) {
        List<Long> permissionIds = sysRoleService.getPermissionIds(id);
        return Result.success(permissionIds);
    }

    @Operation(summary = "修改角色状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setStatus(status);
        sysRoleService.updateById(role);
        return Result.success(true);
    }
}
