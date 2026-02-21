package com.tobacco.warehouse.modules.maintenance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.maintenance.entity.UsedElement;
import com.tobacco.warehouse.modules.maintenance.mapper.UsedElementMapper;
import com.tobacco.warehouse.modules.maintenance.service.UsedElementService;
import org.springframework.stereotype.Service;

/**
 * 零件更换记录 Service 実装
 * 
 * @author warehouse
 */
@Service
public class UsedElementServiceImpl extends ServiceImpl<UsedElementMapper, UsedElement> implements UsedElementService {
}
