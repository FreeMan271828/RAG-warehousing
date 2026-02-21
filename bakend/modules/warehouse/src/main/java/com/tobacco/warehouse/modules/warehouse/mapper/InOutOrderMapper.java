package com.tobacco.warehouse.modules.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.warehouse.entity.InOutOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出入库工单 Mapper 接口
 * 
 * @author warehouse
 */
@Mapper
public interface InOutOrderMapper extends BaseMapper<InOutOrder> {
}
