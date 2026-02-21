package com.tobacco.warehouse.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统角色 Mapper
 *
 * @author warehouse
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色编码查询角色
     */
    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode} AND deleted = 0")
    SysRole selectByRoleCode(@Param("roleCode") String roleCode);
}
