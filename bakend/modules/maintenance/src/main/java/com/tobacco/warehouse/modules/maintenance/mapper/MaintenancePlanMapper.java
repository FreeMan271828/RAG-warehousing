package com.tobacco.warehouse.modules.maintenance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.maintenance.entity.MaintenancePlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 保养计划 Mapper 接口
 * 
 * @author warehouse
 */
@Mapper
public interface MaintenancePlanMapper extends BaseMapper<MaintenancePlan> {
}
