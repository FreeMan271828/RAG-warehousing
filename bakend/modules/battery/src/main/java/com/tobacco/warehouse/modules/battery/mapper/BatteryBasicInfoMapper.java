package com.tobacco.warehouse.modules.battery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.battery.entity.BatteryBasicInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电池基本信息 Mapper 接口
 * 
 * @author warehouse
 */
@Mapper
public interface BatteryBasicInfoMapper extends BaseMapper<BatteryBasicInfo> {
}
