package com.tobacco.warehouse.modules.battery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.battery.entity.ChargeHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 充电历史记录 Mapper 接口
 * 
 * @author warehouse
 */
@Mapper
public interface ChargeHistoryMapper extends BaseMapper<ChargeHistory> {
}
