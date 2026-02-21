package com.tobacco.warehouse.modules.maintenance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.maintenance.entity.MaintainRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 维修记录 Mapper 接口
 * 
 * @author warehouse
 */
@Mapper
public interface MaintainRecordMapper extends BaseMapper<MaintainRecord> {
}
