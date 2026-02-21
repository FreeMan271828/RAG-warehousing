package com.tobacco.warehouse.modules.maintenance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.maintenance.entity.MaintenancePlan;
import com.tobacco.warehouse.modules.maintenance.mapper.MaintenancePlanMapper;
import com.tobacco.warehouse.modules.maintenance.service.MaintenancePlanService;
import org.springframework.stereotype.Service;

/**
 * 保养计划 Service 実装
 * 
 * @author warehouse
 */
@Service
public class MaintenancePlanServiceImpl extends ServiceImpl<MaintenancePlanMapper, MaintenancePlan> implements MaintenancePlanService {
}
