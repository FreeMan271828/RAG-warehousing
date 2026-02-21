package com.tobacco.warehouse.modules.warehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.warehouse.entity.Box;
import com.tobacco.warehouse.modules.warehouse.mapper.BoxMapper;
import com.tobacco.warehouse.modules.warehouse.service.BoxService;
import org.springframework.stereotype.Service;

/**
 * 箱子 Service 実装
 * 
 * @author warehouse
 */
@Service
public class BoxServiceImpl extends ServiceImpl<BoxMapper, Box> implements BoxService {
}
