package com.tobacco.warehouse.modules.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.warehouse.entity.*;
import com.tobacco.warehouse.modules.warehouse.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 仓库管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "仓库管理")
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseLocationService locationService;
    private final PalletService palletService;
    private final BoxService boxService;
    private final InOutOrderService inOutOrderService;

    // ==================== 货位管理 ====================
    
    @Operation(summary = "分页查询货位")
    @GetMapping("/location/page")
    public Result<PageResult<WarehouseLocation>> getLocationPage(PageRequest pageRequest, WarehouseLocation location) {
        LambdaQueryWrapper<WarehouseLocation> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(location.getLocationCode())) {
            wrapper.like(WarehouseLocation::getLocationCode, location.getLocationCode());
        }
        if (StringUtils.hasText(location.getAreaCode())) {
            wrapper.eq(WarehouseLocation::getAreaCode, location.getAreaCode());
        }
        if (location.getStatus() != null) {
            wrapper.eq(WarehouseLocation::getStatus, location.getStatus());
        }
        
        wrapper.orderByAsc(WarehouseLocation::getAreaCode, WarehouseLocation::getRowNum, WarehouseLocation::getColNum, WarehouseLocation::getLevelNum);
        
        Page<WarehouseLocation> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<WarehouseLocation> result = locationService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取所有启用的货位")
    @GetMapping("/location/list")
    public Result<java.util.List<WarehouseLocation>> getLocationList(WarehouseLocation location) {
        LambdaQueryWrapper<WarehouseLocation> wrapper = new LambdaQueryWrapper<>();
        
        if (location.getStatus() != null) {
            wrapper.eq(WarehouseLocation::getStatus, location.getStatus());
        }
        
        wrapper.orderByAsc(WarehouseLocation::getLocationCode);
        
        return Result.success(locationService.list(wrapper));
    }

    @Operation(summary = "获取所有空货位")
    @GetMapping("/location/empty")
    public Result<java.util.List<WarehouseLocation>> getEmptyLocations() {
        LambdaQueryWrapper<WarehouseLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseLocation::getStatus, 1);
        wrapper.eq(WarehouseLocation::getCurrentStatus, "empty");
        wrapper.orderByAsc(WarehouseLocation::getLocationCode);
        return Result.success(locationService.list(wrapper));
    }

    @Operation(summary = "获取货位详情")
    @GetMapping("/location/{id}")
    public Result<WarehouseLocation> getLocationById(@PathVariable Long id) {
        return Result.success(locationService.getById(id));
    }

    @Operation(summary = "创建货位")
    @PostMapping("/location")
    public Result<Boolean> createLocation(@RequestBody WarehouseLocation location) {
        return locationService.save(location) ? Result.success(true) : Result.error("创建货位失败");
    }

    @Operation(summary = "更新货位")
    @PutMapping("/location")
    public Result<Boolean> updateLocation(@RequestBody WarehouseLocation location) {
        return locationService.updateById(location) ? Result.success(true) : Result.error("更新货位失败");
    }

    @Operation(summary = "删除货位")
    @DeleteMapping("/location/{id}")
    public Result<Boolean> deleteLocation(@PathVariable Long id) {
        return locationService.removeById(id) ? Result.success(true) : Result.error("删除货位失败");
    }

    // ==================== 托盘管理 ====================
    
    @Operation(summary = "分页查询托盘")
    @GetMapping("/pallet/page")
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
    @GetMapping("/pallet/list")
    public Result<java.util.List<Pallet>> getPalletList(Pallet pallet) {
        LambdaQueryWrapper<Pallet> wrapper = new LambdaQueryWrapper<>();
        
        if (pallet.getStatus() != null) {
            wrapper.eq(Pallet::getStatus, pallet.getStatus());
        }
        
        wrapper.orderByAsc(Pallet::getPalletCode);
        
        return Result.success(palletService.list(wrapper));
    }

    @Operation(summary = "获取托盘详情")
    @GetMapping("/pallet/{id}")
    public Result<Pallet> getPalletById(@PathVariable Long id) {
        return Result.success(palletService.getById(id));
    }

    @Operation(summary = "创建托盘")
    @PostMapping("/pallet")
    public Result<Boolean> createPallet(@RequestBody Pallet pallet) {
        return palletService.save(pallet) ? Result.success(true) : Result.error("创建托盘失败");
    }

    @Operation(summary = "更新托盘")
    @PutMapping("/pallet")
    public Result<Boolean> updatePallet(@RequestBody Pallet pallet) {
        return palletService.updateById(pallet) ? Result.success(true) : Result.error("更新托盘失败");
    }

    @Operation(summary = "删除托盘")
    @DeleteMapping("/pallet/{id}")
    public Result<Boolean> deletePallet(@PathVariable Long id) {
        return palletService.removeById(id) ? Result.success(true) : Result.error("删除托盘失败");
    }

    // ==================== 箱子管理 ====================
    
    @Operation(summary = "分页查询箱子")
    @GetMapping("/box/page")
    public Result<PageResult<Box>> getBoxPage(PageRequest pageRequest, Box box) {
        LambdaQueryWrapper<Box> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(box.getBoxCode())) {
            wrapper.like(Box::getBoxCode, box.getBoxCode());
        }
        if (box.getStatus() != null) {
            wrapper.eq(Box::getStatus, box.getStatus());
        }
        
        wrapper.orderByDesc(Box::getCreateTime);
        
        Page<Box> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Box> result = boxService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取所有箱子")
    @GetMapping("/box/list")
    public Result<java.util.List<Box>> getBoxList(Box box) {
        LambdaQueryWrapper<Box> wrapper = new LambdaQueryWrapper<>();
        
        if (box.getStatus() != null) {
            wrapper.eq(Box::getStatus, box.getStatus());
        }
        
        wrapper.orderByAsc(Box::getBoxCode);
        
        return Result.success(boxService.list(wrapper));
    }

    @Operation(summary = "获取箱子详情")
    @GetMapping("/box/{id}")
    public Result<Box> getBoxById(@PathVariable Long id) {
        return Result.success(boxService.getById(id));
    }

    @Operation(summary = "创建箱子")
    @PostMapping("/box")
    public Result<Boolean> createBox(@RequestBody Box box) {
        return boxService.save(box) ? Result.success(true) : Result.error("创建箱子失败");
    }

    @Operation(summary = "更新箱子")
    @PutMapping("/box")
    public Result<Boolean> updateBox(@RequestBody Box box) {
        return boxService.updateById(box) ? Result.success(true) : Result.error("更新箱子失败");
    }

    @Operation(summary = "删除箱子")
    @DeleteMapping("/box/{id}")
    public Result<Boolean> deleteBox(@PathVariable Long id) {
        return boxService.removeById(id) ? Result.success(true) : Result.error("删除箱子失败");
    }

    // ==================== 出入库工单管理 ====================
    
    @Operation(summary = "分页查询出入库工单")
    @GetMapping("/inout/page")
    public Result<PageResult<InOutOrder>> getInOutOrderPage(PageRequest pageRequest, InOutOrder order) {
        LambdaQueryWrapper<InOutOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(order.getOrderCode())) {
            wrapper.like(InOutOrder::getOrderCode, order.getOrderCode());
        }
        if (StringUtils.hasText(order.getOrderType())) {
            wrapper.eq(InOutOrder::getOrderType, order.getOrderType());
        }
        if (StringUtils.hasText(order.getStatus())) {
            wrapper.eq(InOutOrder::getStatus, order.getStatus());
        }
        
        wrapper.orderByDesc(InOutOrder::getCreateTime);
        
        Page<InOutOrder> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<InOutOrder> result = inOutOrderService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取所有出入库工单")
    @GetMapping("/inout/list")
    public Result<java.util.List<InOutOrder>> getInOutOrderList(InOutOrder order) {
        LambdaQueryWrapper<InOutOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (order.getStatus() != null) {
            wrapper.eq(InOutOrder::getStatus, order.getStatus());
        }
        
        wrapper.orderByDesc(InOutOrder::getCreateTime);
        
        return Result.success(inOutOrderService.list(wrapper));
    }

    @Operation(summary = "获取出入库工单详情")
    @GetMapping("/inout/{id}")
    public Result<InOutOrder> getInOutOrderById(@PathVariable Long id) {
        return Result.success(inOutOrderService.getById(id));
    }

    @Operation(summary = "创建出入库工单")
    @PostMapping("/inout")
    public Result<Boolean> createInOutOrder(@RequestBody InOutOrder order) {
        return inOutOrderService.save(order) ? Result.success(true) : Result.error("创建出入库工单失败");
    }

    @Operation(summary = "更新出入库工单")
    @PutMapping("/inout")
    public Result<Boolean> updateInOutOrder(@RequestBody InOutOrder order) {
        return inOutOrderService.updateById(order) ? Result.success(true) : Result.error("更新出入库工单失败");
    }

    @Operation(summary = "删除出入库工单")
    @DeleteMapping("/inout/{id}")
    public Result<Boolean> deleteInOutOrder(@PathVariable Long id) {
        return inOutOrderService.removeById(id) ? Result.success(true) : Result.error("删除出入库工单失败");
    }

    @Operation(summary = "执行出入库工单")
    @PutMapping("/inout/{id}/execute")
    public Result<Boolean> executeInOutOrder(@PathVariable Long id) {
        InOutOrder order = new InOutOrder();
        order.setId(id);
        order.setStatus("completed");
        return inOutOrderService.updateById(order) ? Result.success(true) : Result.error("执行出入库工单失败");
    }

    // ==================== 仓库统计 ====================
    
    @Operation(summary = "获取仓库概览统计")
    @GetMapping("/statistics/overview")
    public Result<java.util.Map<String, Object>> getOverview() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // 货位统计
        LambdaQueryWrapper<WarehouseLocation> locWrapper = new LambdaQueryWrapper<>();
        locWrapper.eq(WarehouseLocation::getStatus, 1);
        long totalLocations = locationService.count(locWrapper);
        
        locWrapper.eq(WarehouseLocation::getCurrentStatus, "empty");
        long emptyLocations = locationService.count(locWrapper);
        
        stats.put("totalLocations", totalLocations);
        stats.put("emptyLocations", emptyLocations);
        stats.put("occupiedLocations", totalLocations - emptyLocations);
        
        // 托盘统计
        LambdaQueryWrapper<Pallet> palletWrapper = new LambdaQueryWrapper<>();
        palletWrapper.eq(Pallet::getStatus, 1);
        stats.put("totalPallets", palletService.count(palletWrapper));
        
        // 箱子统计
        LambdaQueryWrapper<Box> boxWrapper = new LambdaQueryWrapper<>();
        boxWrapper.eq(Box::getStatus, 1);
        stats.put("totalBoxes", boxService.count(boxWrapper));
        
        // 出入库工单统计
        LambdaQueryWrapper<InOutOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(InOutOrder::getStatus, "pending");
        stats.put("pendingOrders", inOutOrderService.count(orderWrapper));
        
        return Result.success(stats);
    }
}
