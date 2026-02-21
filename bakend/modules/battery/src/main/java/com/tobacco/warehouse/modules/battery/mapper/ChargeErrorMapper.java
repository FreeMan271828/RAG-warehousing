package com.tobacco.warehouse.modules.battery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.battery.entity.ChargeError;
import org.apache.ibatis.annotations.Mapper;

/**
 * 充电故障记录 Mapper 接口
 * 
 * @author warehouse
 */
@Mapper
public interface ChargeErrorMapper extends BaseMapper<ChargeError> {
}
