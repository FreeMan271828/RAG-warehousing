package com.tobacco.warehouse.modules.warehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.warehouse.entity.InOutOrder;
import com.tobacco.warehouse.modules.warehouse.mapper.InOutOrderMapper;
import com.tobacco.warehouse.modules.warehouse.service.InOutOrderService;
import org.springframework.stereotype.Service;

/**
 * 出入库工单 Service 実装
 * 
 * @author warehouse
 */
@Service
public class InOutOrderServiceImpl extends ServiceImpl<InOutOrderMapper, InOutOrder> implements InOutOrderService {
}
