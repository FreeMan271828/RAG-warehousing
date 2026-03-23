package com.tobacco.warehouse.modules.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.warehouse.entity.Pallet;
import com.tobacco.warehouse.modules.warehouse.service.PalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 托盘管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "托盘管理")
@RestController
@RequestMapping("/api/v1/warehouse/pallet")
@RequiredArgsConstructor
public class PalletController {

    private final PalletService palletService;

    @Operation(summary = "分页查询托盘")
    @GetMapping("/page")
    public Result<PageResult<Pallet>> getPalletPage(PageRequest pageRequest, Pallet pallet) {
        LambdaQueryWrapper<Pallet> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(pallet.getPalletCode())) {
            wrapper.like(Pallet::getPalletCode, pallet.getPalletCode());
        }
        if (pallet.getStatus() != null) {
            wrapper.eq(Pallet::getStatus, pallet.getStatus());
        }
        
        wrapper.orderByDesc(Pallet::getCreateTime);
        
        Page<Pallet> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Pallet> result = palletService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取所有托盘")
    @GetMapping("/list")
    public Result<java.util.List<Pallet>> getPalletList(Pallet pallet) {
        LambdaQueryWrapper<Pallet> wrapper = new LambdaQueryWrapper<>();
        
        if (pallet.getStatus() != null) {
            wrapper.eq(Pallet::getStatus, pallet.getStatus());
        }
        
        wrapper.orderByAsc(Pallet::getPalletCode);
        
        return Result.success(palletService.list(wrapper));
    }

    @Operation(summary = "获取托盘详情")
    @GetMapping("/{id}")
    public Result<Pallet> getPalletById(@PathVariable Long id) {
        return Result.success(palletService.getById(id));
    }

    @Operation(summary = "创建托盘")
    @PostMapping
    public Result<Boolean> createPallet(@RequestBody Pallet pallet) {
        return palletService.save(pallet) ? Result.success(true) : Result.error("创建托盘失败");
    }

    @Operation(summary = "更新托盘")
    @PutMapping
    public Result<Boolean> updatePallet(@RequestBody Pallet pallet) {
        return palletService.updateById(pallet) ? Result.success(true) : Result.error("更新托盘失败");
    }

    @Operation(summary = "删除托盘")
    @DeleteMapping("/{id}")
    public Result<Boolean> deletePallet(@PathVariable Long id) {
        return palletService.removeById(id) ? Result.success(true) : Result.error("删除托盘失败");
    }
}
