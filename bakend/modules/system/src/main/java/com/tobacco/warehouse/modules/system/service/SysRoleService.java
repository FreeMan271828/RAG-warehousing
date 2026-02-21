package com.tobacco.warehouse.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tobacco.warehouse.modules.system.entity.SysRole;

import java.util.List;

/**
 * 系统角色 Service 接口
 *
 * @author warehouse
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色
     */
    IPage<SysRole> pageRoles(Integer pageNum, Integer pageSize, SysRole query);

    /**
     * 根据角色编码查询角色
     */
    SysRole getByRoleCode(String roleCode);

    /**
     * 新增角色
     */
    boolean addRole(SysRole role);

    /**
     * 修改角色
     */
    boolean updateRole(SysRole role);

    /**
     * 删除角色
     */
    boolean deleteRole(Long id);

    /**
     * 获取所有启用状态的角色列表
     */
    List<SysRole> listEnabledRoles();

    /**
     * 分配角色权限
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色权限ID列表
     */
    List<Long> getPermissionIds(Long roleId);
}
