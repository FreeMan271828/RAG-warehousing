package com.tobacco.warehouse.modules.maintenance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.maintenance.entity.MaintenanceRecord;
import com.tobacco.warehouse.modules.maintenance.mapper.MaintenanceRecordMapper;
import com.tobacco.warehouse.modules.maintenance.service.MaintenanceRecordService;
import org.springframework.stereotype.Service;

/**
 * 保养记录 Service 実装
 * 
 * @author warehouse
 */
@Service
public class MaintenanceRecordServiceImpl extends ServiceImpl<MaintenanceRecordMapper, MaintenanceRecord> implements MaintenanceRecordService {
}
