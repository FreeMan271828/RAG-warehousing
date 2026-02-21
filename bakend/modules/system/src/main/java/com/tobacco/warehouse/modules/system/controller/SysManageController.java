package com.tobacco.warehouse.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.system.entity.SysPermission;
import com.tobacco.warehouse.modules.system.entity.SysDept;
import com.tobacco.warehouse.modules.system.entity.SysOperationLog;
import com.tobacco.warehouse.modules.system.mapper.SysPermissionMapper;
import com.tobacco.warehouse.modules.system.mapper.SysDeptMapper;
import com.tobacco.warehouse.modules.system.mapper.SysOperationLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统管理 Controller
 * 包含：权限管理、部门管理、操作日志
 *
 * @author warehouse
 */
@Tag(name = "系统管理")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SysManageController {

    private final SysPermissionMapper sysPermissionMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysOperationLogMapper sysOperationLogMapper;

    // ==================== 权限管理 ====================

    @Operation(summary = "分页查询权限")
    @GetMapping("/permission/page")
    public Result<PageResult<SysPermission>> getPermissionPage(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            SysPermission query) {
        Page<SysPermission> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getPermissionCode())) {
            wrapper.like(SysPermission::getPermissionCode, query.getPermissionCode());
        }
        if (StringUtils.hasText(query.getPermissionName())) {
            wrapper.like(SysPermission::getPermissionName, query.getPermissionName());
        }
        if (StringUtils.hasText(query.getPermissionType())) {
            wrapper.eq(SysPermission::getPermissionType, query.getPermissionType());
        }
        wrapper.orderByAsc(SysPermission::getSortOrder);
        IPage<SysPermission> result = sysPermissionMapper.selectPage(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取权限树形列表")
    @GetMapping("/permission/tree")
    public Result<List<SysPermission>> getPermissionTree() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getStatus, 1);
        wrapper.orderByAsc(SysPermission::getSortOrder);
        return Result.success(sysPermissionMapper.selectList(wrapper));
    }

    @Operation(summary = "创建权限")
    @PostMapping("/permission")
    public Result<Boolean> createPermission(@RequestBody SysPermission permission) {
        permission.setStatus(1);
        return Result.success(sysPermissionMapper.insert(permission) > 0);
    }

    @Operation(summary = "更新权限")
    @PutMapping("/permission")
    public Result<Boolean> updatePermission(@RequestBody SysPermission permission) {
        return Result.success(sysPermissionMapper.updateById(permission) > 0);
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/permission/{id}")
    public Result<Boolean> deletePermission(@PathVariable Long id) {
        return Result.success(sysPermissionMapper.deleteById(id) > 0);
    }

    // ==================== 部门管理 ====================

    @Operation(summary = "分页查询部门")
    @GetMapping("/dept/page")
    public Result<PageResult<SysDept>> getDeptPage(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            SysDept query) {
        Page<SysDept> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getDeptName())) {
            wrapper.like(SysDept::getDeptName, query.getDeptName());
        }
        wrapper.orderByAsc(SysDept::getSortOrder);
        IPage<SysDept> result = sysDeptMapper.selectPage(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取部门树形列表")
    @GetMapping("/dept/tree")
    public Result<List<SysDept>> getDeptTree() {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getStatus, 1);
        wrapper.orderByAsc(SysDept::getSortOrder);
        return Result.success(sysDeptMapper.selectList(wrapper));
    }

    @Operation(summary = "创建部门")
    @PostMapping("/dept")
    public Result<Boolean> createDept(@RequestBody SysDept dept) {
        dept.setStatus(1);
        return Result.success(sysDeptMapper.insert(dept) > 0);
    }

    @Operation(summary = "更新部门")
    @PutMapping("/dept")
    public Result<Boolean> updateDept(@RequestBody SysDept dept) {
        return Result.success(sysDeptMapper.updateById(dept) > 0);
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/dept/{id}")
    public Result<Boolean> deleteDept(@PathVariable Long id) {
        return Result.success(sysDeptMapper.deleteById(id) > 0);
    }

    // ==================== 操作日志 ====================

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/operation-log/page")
    public Result<PageResult<SysOperationLog>> getLogPage(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            SysOperationLog query) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getUsername())) {
            wrapper.like(SysOperationLog::getUsername, query.getUsername());
        }
        if (StringUtils.hasText(query.getModule())) {
            wrapper.like(SysOperationLog::getModule, query.getModule());
        }
        if (StringUtils.hasText(query.getOperation())) {
            wrapper.like(SysOperationLog::getOperation, query.getOperation());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysOperationLog::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysOperationLog::getCreateTime);
        IPage<SysOperationLog> result = sysOperationLogMapper.selectPage(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取操作日志详情")
    @GetMapping("/operation-log/{id}")
    public Result<SysOperationLog> getLogById(@PathVariable Long id) {
        return Result.success(sysOperationLogMapper.selectById(id));
    }

    @Operation(summary = "清空操作日志")
    @DeleteMapping("/operation-log/clear")
    public Result<Boolean> clearLogs() {
        // 实际生产环境应使用物理删除
        return Result.success(true);
    }
}
