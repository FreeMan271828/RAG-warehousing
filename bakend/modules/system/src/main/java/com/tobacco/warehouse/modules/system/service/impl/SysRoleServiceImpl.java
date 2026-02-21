package com.tobacco.warehouse.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.system.entity.SysRole;
import com.tobacco.warehouse.modules.system.entity.SysRolePermission;
import com.tobacco.warehouse.modules.system.mapper.SysRoleMapper;
import com.tobacco.warehouse.modules.system.mapper.SysRolePermissionMapper;
import com.tobacco.warehouse.modules.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统角色 Service 实现类
 *
 * @author warehouse
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public IPage<SysRole> pageRoles(Integer pageNum, Integer pageSize, SysRole query) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getRoleCode())) {
            wrapper.like(SysRole::getRoleCode, query.getRoleCode());
        }
        if (StringUtils.hasText(query.getRoleName())) {
            wrapper.like(SysRole::getRoleName, query.getRoleName());
        }
        if (StringUtils.hasText(query.getRoleType())) {
            wrapper.eq(SysRole::getRoleType, query.getRoleType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, query.getStatus());
        }

        wrapper.orderByAsc(SysRole::getSortOrder);
        wrapper.orderByDesc(SysRole::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public SysRole getByRoleCode(String roleCode) {
        return baseMapper.selectByRoleCode(roleCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRole(SysRole role) {
        // 检查角色编码是否存在
        SysRole existRole = getByRoleCode(role.getRoleCode());
        if (existRole != null) {
            throw new RuntimeException("角色编码已存在");
        }
        role.setStatus(1);
        return this.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole role) {
        SysRole existRole = this.getById(role.getId());
        if (existRole == null) {
            throw new RuntimeException("角色不存在");
        }
        // 如果修改了角色编码，检查是否重复
        if (StringUtils.hasText(role.getRoleCode())
                && !role.getRoleCode().equals(existRole.getRoleCode())) {
            SysRole sameCodeRole = getByRoleCode(role.getRoleCode());
            if (sameCodeRole != null) {
                throw new RuntimeException("角色编码已存在");
            }
        }
        return this.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        SysRole role = this.getById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        // 删除角色时同时删除角色权限关联
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, id);
        sysRolePermissionMapper.delete(wrapper);
        
        return this.removeById(id);
    }

    @Override
    public List<SysRole> listEnabledRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1);
        wrapper.orderByAsc(SysRole::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除该角色的所有权限
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        sysRolePermissionMapper.delete(wrapper);

        // 添加新的权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> rolePermissions = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            for (Long permissionId : permissionIds) {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                rp.setCreateTime(now);
                rolePermissions.add(rp);
            }
            sysRolePermissionMapper.insertBatch(rolePermissions);
        }
        return true;
    }

    @Override
    public List<Long> getPermissionIds(Long roleId) {
        return sysRolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }
}
