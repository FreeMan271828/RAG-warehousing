package com.tobacco.warehouse.modules.battery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.battery.entity.ChargeError;
import com.tobacco.warehouse.modules.battery.mapper.ChargeErrorMapper;
import com.tobacco.warehouse.modules.battery.service.ChargeErrorService;
import org.springframework.stereotype.Service;

/**
 * 充电故障记录 Service 実装
 * 
 * @author warehouse
 */
@Service
public class ChargeErrorServiceImpl extends ServiceImpl<ChargeErrorMapper, ChargeError> implements ChargeErrorService {
}
