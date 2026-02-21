package com.tobacco.warehouse.modules.maintenance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.maintenance.entity.MaintenanceRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 保养记录 Mapper 接口
 * 
 * @author warehouse
 */
@Mapper
public interface MaintenanceRecordMapper extends BaseMapper<MaintenanceRecord> {
}
