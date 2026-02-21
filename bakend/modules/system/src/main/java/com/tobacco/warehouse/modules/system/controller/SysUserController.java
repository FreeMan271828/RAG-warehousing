package com.tobacco.warehouse.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.system.entity.SysUser;
import com.tobacco.warehouse.modules.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户管理 Controller
 *
 * @author warehouse
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<IPage<SysUser>> getPage(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            SysUser query) {
        IPage<SysUser> page = sysUserService.pageUsers(pageNum, pageSize, query);
        return Result.success(page);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable(name = "id") Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return Result.success(user);
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public Result<SysUser> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Result.error(401, "未登录");
        }
        String username = auth.getName();
        SysUser user = sysUserService.getByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Boolean> create(@RequestBody SysUser user) {
        sysUserService.addUser(user);
        return Result.success(true);
    }

    @Operation(summary = "更新用户")
    @PutMapping
    public Result<Boolean> update(@RequestBody SysUser user) {
        sysUserService.updateUser(user);
        return Result.success(true);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        sysUserService.deleteUser(id);
        return Result.success(true);
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/reset-password")
    public Result<Boolean> resetPassword(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "newPassword", defaultValue = "123456") String newPassword) {
        sysUserService.resetPassword(id, newPassword);
        return Result.success(true);
    }

    @Operation(summary = "修改当前用户密码")
    @PutMapping("/change-password")
    public Result<Boolean> changePassword(
            @RequestParam(name = "oldPassword") String oldPassword,
            @RequestParam(name = "newPassword") String newPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        SysUser user = sysUserService.getByUsername(username);
        sysUserService.changePassword(user.getId(), oldPassword, newPassword);
        return Result.success(true);
    }

    @Operation(summary = "修改用户状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "status") Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserService.updateById(user);
        return Result.success(true);
    }

    @Operation(summary = "获取所有用户列表")
    @GetMapping("/list")
    public Result<List<SysUser>> getList(SysUser query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        wrapper.eq(SysUser::getStatus, 1);
        wrapper.select(SysUser.class, c -> !"password".equals(c.getColumn()));
        wrapper.orderByAsc(SysUser::getUsername);
        return Result.success(sysUserService.list(wrapper));
    }
}
