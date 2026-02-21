package com.tobacco.warehouse.modules.maintenance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.maintenance.entity.MaintainRecord;
import com.tobacco.warehouse.modules.maintenance.mapper.MaintainRecordMapper;
import com.tobacco.warehouse.modules.maintenance.service.MaintainRecordService;
import org.springframework.stereotype.Service;

/**
 * 维修记录 Service 実装
 * 
 * @author warehouse
 */
@Service
public class MaintainRecordServiceImpl extends ServiceImpl<MaintainRecordMapper, MaintainRecord> implements MaintainRecordService {
}
