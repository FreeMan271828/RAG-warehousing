package com.tobacco.warehouse.modules.warehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobacco.warehouse.modules.warehouse.entity.Pallet;
import com.tobacco.warehouse.modules.warehouse.mapper.PalletMapper;
import com.tobacco.warehouse.modules.warehouse.service.PalletService;
import org.springframework.stereotype.Service;

/**
 * 托盘 Service 実装
 * 
 * @author warehouse
 */
@Service
public class PalletServiceImpl extends ServiceImpl<PalletMapper, Pallet> implements PalletService {
}
