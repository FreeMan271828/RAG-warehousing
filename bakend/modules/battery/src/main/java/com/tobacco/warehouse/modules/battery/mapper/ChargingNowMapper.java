package com.tobacco.warehouse.modules.battery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.battery.entity.ChargingNow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 充电实时记录 Mapper 接口
 * 
 * @author warehouse
 */
@Mapper
public interface ChargingNowMapper extends BaseMapper<ChargingNow> {
}
