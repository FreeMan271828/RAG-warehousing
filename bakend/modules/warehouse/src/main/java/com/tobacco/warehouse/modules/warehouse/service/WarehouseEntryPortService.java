package com.tobacco.warehouse.modules.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tobacco.warehouse.modules.warehouse.entity.WarehouseEntryPort;

import java.util.List;

/**
 * 仓库入库口 服务接口
 * 
 * @author warehouse
 */
public interface WarehouseEntryPortService extends IService<WarehouseEntryPort> {

    /**
     * 获取所有启用的入库口
     */
    List<WarehouseEntryPort> listEnabled();

    /**
     * 根据位置获取入库口
     */
    WarehouseEntryPort getByPosition(String position);
}
