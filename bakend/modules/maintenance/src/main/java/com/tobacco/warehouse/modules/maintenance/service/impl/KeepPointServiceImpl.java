package com.tobacco.warehouse.modules.maintenance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.maintenance.entity.KeepPoint;
import com.tobacco.warehouse.modules.maintenance.mapper.KeepPointMapper;
import com.tobacco.warehouse.modules.maintenance.service.KeepPointService;
import org.springframework.stereotype.Service;

/**
 * 保养点 Service 実装
 * 
 * @author warehouse
 */
@Service
public class KeepPointServiceImpl extends ServiceImpl<KeepPointMapper, KeepPoint> implements KeepPointService {
}
