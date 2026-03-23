package com.tobacco.warehouse.modules.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.warehouse.entity.WarehouseEntryPort;
import com.tobacco.warehouse.modules.warehouse.mapper.WarehouseEntryPortMapper;
import com.tobacco.warehouse.modules.warehouse.service.WarehouseEntryPortService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 仓库入库口 服务实现类
 * 
 * @author warehouse
 */
@Service
public class WarehouseEntryPortServiceImpl extends ServiceImpl<WarehouseEntryPortMapper, WarehouseEntryPort> implements WarehouseEntryPortService {

    @Override
    public List<WarehouseEntryPort> listEnabled() {
        return list(new LambdaQueryWrapper<WarehouseEntryPort>()
                .eq(WarehouseEntryPort::getStatus, 1)
                .orderByAsc(WarehouseEntryPort::getPortPosition));
    }

    @Override
    public WarehouseEntryPort getByPosition(String position) {
        return getOne(new LambdaQueryWrapper<WarehouseEntryPort>()
                .eq(WarehouseEntryPort::getPortPosition, position)
                .eq(WarehouseEntryPort::getStatus, 1));
    }
}
