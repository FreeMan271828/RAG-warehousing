package com.tobacco.warehouse.modules.warehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.warehouse.entity.WarehouseLocation;
import com.tobacco.warehouse.modules.warehouse.mapper.WarehouseLocationMapper;
import com.tobacco.warehouse.modules.warehouse.service.WarehouseLocationService;
import org.springframework.stereotype.Service;

/**
 * 货位 Service 実装
 * 
 * @author warehouse
 */
@Service
public class WarehouseLocationServiceImpl extends ServiceImpl<WarehouseLocationMapper, WarehouseLocation> implements WarehouseLocationService {
}
