package com.tobacco.warehouse.modules.battery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.battery.entity.ChargeHistory;
import com.tobacco.warehouse.modules.battery.mapper.ChargeHistoryMapper;
import com.tobacco.warehouse.modules.battery.service.ChargeHistoryService;
import org.springframework.stereotype.Service;

/**
 * 充电历史记录 Service 実装
 * 
 * @author warehouse
 */
@Service
public class ChargeHistoryServiceImpl extends ServiceImpl<ChargeHistoryMapper, ChargeHistory> implements ChargeHistoryService {
}
