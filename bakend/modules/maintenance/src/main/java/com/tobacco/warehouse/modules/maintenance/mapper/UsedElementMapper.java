package com.tobacco.warehouse.modules.maintenance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tobacco.warehouse.modules.maintenance.entity.UsedElement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 零件更换记录 Mapper 接口
 * 
 * @author warehouse
 */
@Mapper
public interface UsedElementMapper extends BaseMapper<UsedElement> {
}
