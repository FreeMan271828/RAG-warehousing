package com.tobacco.warehouse.modules.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.warehouse.entity.ChargingRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 充电记录 Mapper接口
 * 
 * @author warehouse
 */
@Mapper
public interface ChargingRecordMapper extends BaseMapper<ChargingRecord> {
}
