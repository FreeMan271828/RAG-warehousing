package com.tobacco.warehouse.modules.equipment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.equipment.entity.Element;
import com.tobacco.warehouse.modules.equipment.mapper.ElementMapper;
import com.tobacco.warehouse.modules.equipment.service.ElementService;
import org.springframework.stereotype.Service;

/**
 * 零件 Service实现类
 *
 * @author warehouse
 */
@Service
public class ElementServiceImpl extends ServiceImpl<ElementMapper, Element> implements ElementService {
}
