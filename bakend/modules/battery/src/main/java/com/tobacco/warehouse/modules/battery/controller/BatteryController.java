package com.tobacco.warehouse.modules.battery.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.battery.entity.BatteryBasicInfo;
import com.tobacco.warehouse.modules.battery.service.BatteryBasicInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 电池管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "电池管理")
@RestController
@RequestMapping("/api/v1/battery")
@RequiredArgsConstructor
public class BatteryController {

    private final BatteryBasicInfoService batteryService;

    @Operation(summary = "分页查询电池")
    @GetMapping("/page")
    public Result<PageResult<BatteryBasicInfo>> getPage(PageRequest pageRequest, BatteryBasicInfo battery) {
        LambdaQueryWrapper<BatteryBasicInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(battery.getBatteryCode())) {
            wrapper.like(BatteryBasicInfo::getBatteryCode, battery.getBatteryCode());
        }
        if (StringUtils.hasText(battery.getBatteryName())) {
            wrapper.like(BatteryBasicInfo::getBatteryName, battery.getBatteryName());
        }
        if (StringUtils.hasText(battery.getBatteryType())) {
            wrapper.eq(BatteryBasicInfo::getBatteryType, battery.getBatteryType());
        }
        if (battery.getStatus() != null) {
            wrapper.eq(BatteryBasicInfo::getStatus, battery.getStatus());
        }
        
        wrapper.orderByDesc(BatteryBasicInfo::getCreateTime);
        
        Page<BatteryBasicInfo> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<BatteryBasicInfo> result = batteryService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取电池详情")
    @GetMapping("/{id}")
    public Result<BatteryBasicInfo> getById(@PathVariable(name = "id") Long id) {
        BatteryBasicInfo battery = batteryService.getById(id);
        return Result.success(battery);
    }

    @Operation(summary = "获取所有电池")
    @GetMapping("/list")
    public Result<java.util.List<BatteryBasicInfo>> getList(BatteryBasicInfo battery) {
        LambdaQueryWrapper<BatteryBasicInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (battery.getStatus() != null) {
            wrapper.eq(BatteryBasicInfo::getStatus, battery.getStatus());
        }
        
        wrapper.orderByAsc(BatteryBasicInfo::getBatteryCode);
        
        return Result.success(batteryService.list(wrapper));
    }

    @Operation(summary = "获取所有正在充电的电池")
    @GetMapping("/charging")
    public Result<java.util.List<BatteryBasicInfo>> getChargingList() {
        LambdaQueryWrapper<BatteryBasicInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BatteryBasicInfo::getStatus, 3); // 充电中
        wrapper.orderByAsc(BatteryBasicInfo::getBatteryCode);
        return Result.success(batteryService.list(wrapper));
    }

    @Operation(summary = "获取所有故障电池")
    @GetMapping("/error")
    public Result<java.util.List<BatteryBasicInfo>> getErrorList() {
        LambdaQueryWrapper<BatteryBasicInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BatteryBasicInfo::getStatus, 4); // 故障
        wrapper.orderByAsc(BatteryBasicInfo::getBatteryCode);
        return Result.success(batteryService.list(wrapper));
    }

    @Operation(summary = "创建电池")
    @PostMapping
    public Result<Boolean> create(@RequestBody BatteryBasicInfo battery) {
        boolean success = batteryService.save(battery);
        return success ? Result.success(true) : Result.error("创建电池失败");
    }

    @Operation(summary = "更新电池")
    @PutMapping
    public Result<Boolean> update(@RequestBody BatteryBasicInfo battery) {
        boolean success = batteryService.updateById(battery);
        return success ? Result.success(true) : Result.error("更新电池失败");
    }

    @Operation(summary = "删除电池")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = batteryService.removeById(id);
        return success ? Result.success(true) : Result.error("删除电池失败");
    }

    @Operation(summary = "更新电池状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable(name = "id") Long id, @RequestParam(name = "status") Integer status) {
        BatteryBasicInfo battery = new BatteryBasicInfo();
        battery.setId(id);
        battery.setStatus(status);
        boolean success = batteryService.updateById(battery);
        return success ? Result.success(true) : Result.error("更新电池状态失败");
    }
}
