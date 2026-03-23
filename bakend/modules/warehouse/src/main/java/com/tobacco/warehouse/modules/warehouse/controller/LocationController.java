package com.tobacco.warehouse.modules.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.warehouse.entity.WarehouseLocation;
import com.tobacco.warehouse.modules.warehouse.service.WarehouseLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 货位管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "货位管理")
@RestController
@RequestMapping("/api/v1/warehouse/location")
@RequiredArgsConstructor
public class LocationController {

    private final WarehouseLocationService locationService;

    @Operation(summary = "分页查询货位")
    @GetMapping("/page")
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
    @GetMapping("/list")
    public Result<java.util.List<WarehouseLocation>> getLocationList(WarehouseLocation location) {
        LambdaQueryWrapper<WarehouseLocation> wrapper = new LambdaQueryWrapper<>();
        
        if (location.getStatus() != null) {
            wrapper.eq(WarehouseLocation::getStatus, location.getStatus());
        }
        
        wrapper.orderByAsc(WarehouseLocation::getLocationCode);
        
        return Result.success(locationService.list(wrapper));
    }

    @Operation(summary = "获取所有空货位")
    @GetMapping("/empty")
    public Result<java.util.List<WarehouseLocation>> getEmptyLocations() {
        LambdaQueryWrapper<WarehouseLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseLocation::getStatus, 1);
        wrapper.eq(WarehouseLocation::getCurrentStatus, "empty");
        wrapper.orderByAsc(WarehouseLocation::getLocationCode);
        return Result.success(locationService.list(wrapper));
    }

    @Operation(summary = "获取货位详情")
    @GetMapping("/{id}")
    public Result<WarehouseLocation> getLocationById(@PathVariable Long id) {
        return Result.success(locationService.getById(id));
    }

    @Operation(summary = "创建货位")
    @PostMapping
    public Result<Boolean> createLocation(@RequestBody WarehouseLocation location) {
        return locationService.save(location) ? Result.success(true) : Result.error("创建货位失败");
    }

    @Operation(summary = "更新货位")
    @PutMapping
    public Result<Boolean> updateLocation(@RequestBody WarehouseLocation location) {
        return locationService.updateById(location) ? Result.success(true) : Result.error("更新货位失败");
    }

    @Operation(summary = "删除货位")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteLocation(@PathVariable Long id) {
        return locationService.removeById(id) ? Result.success(true) : Result.error("删除货位失败");
    }

    @Operation(summary = "获取仓库概览统计")
    @GetMapping("/statistics/overview")
    public Result<java.util.Map<String, Object>> getOverview() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // 货位统计 - 总数
        LambdaQueryWrapper<WarehouseLocation> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(WarehouseLocation::getStatus, 1);
        long totalLocations = locationService.count(totalWrapper);
        
        // 货位统计 - 空闲
        LambdaQueryWrapper<WarehouseLocation> emptyWrapper = new LambdaQueryWrapper<>();
        emptyWrapper.eq(WarehouseLocation::getStatus, 1);
        emptyWrapper.eq(WarehouseLocation::getCurrentStatus, "empty");
        long emptyLocations = locationService.count(emptyWrapper);
        
        stats.put("totalLocations", totalLocations);
        stats.put("emptyLocations", emptyLocations);
        stats.put("occupiedLocations", totalLocations - emptyLocations);
        
        return Result.success(stats);
    }
}
