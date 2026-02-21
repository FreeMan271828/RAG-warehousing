package com.tobacco.warehouse.modules.equipment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.equipment.entity.DeviceModel;
import com.tobacco.warehouse.modules.equipment.service.DeviceModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 设备型号管理 Controller
 *
 * @author warehouse
 */
@Tag(name = "设备型号管理")
@RestController
@RequestMapping("/api/v1/device-model")
@RequiredArgsConstructor
public class DeviceModelController {

    private final DeviceModelService deviceModelService;

    @Operation(summary = "分页查询设备型号")
    @GetMapping("/page")
    public Result<PageResult<DeviceModel>> getPage(PageRequest pageRequest, DeviceModel deviceModel) {
        LambdaQueryWrapper<DeviceModel> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(deviceModel.getModelCode())) {
            wrapper.like(DeviceModel::getModelCode, deviceModel.getModelCode());
        }
        if (StringUtils.hasText(deviceModel.getModelName())) {
            wrapper.like(DeviceModel::getModelName, deviceModel.getModelName());
        }
        if (deviceModel.getCategoryId() != null) {
            wrapper.eq(DeviceModel::getCategoryId, deviceModel.getCategoryId());
        }
        if (deviceModel.getStatus() != null) {
            wrapper.eq(DeviceModel::getStatus, deviceModel.getStatus());
        }
        
        wrapper.orderByDesc(DeviceModel::getCreateTime);
        
        Page<DeviceModel> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<DeviceModel> result = deviceModelService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取设备型号详情")
    @GetMapping("/{id}")
    public Result<DeviceModel> getById(@PathVariable(name = "id") Long id) {
        DeviceModel deviceModel = deviceModelService.getById(id);
        return Result.success(deviceModel);
    }

    @Operation(summary = "获取所有设备型号")
    @GetMapping("/list")
    public Result<java.util.List<DeviceModel>> getList(DeviceModel deviceModel) {
        LambdaQueryWrapper<DeviceModel> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(deviceModel.getModelName())) {
            wrapper.like(DeviceModel::getModelName, deviceModel.getModelName());
        }
        if (deviceModel.getCategoryId() != null) {
            wrapper.eq(DeviceModel::getCategoryId, deviceModel.getCategoryId());
        }
        if (deviceModel.getStatus() != null) {
            wrapper.eq(DeviceModel::getStatus, deviceModel.getStatus());
        }
        
        wrapper.orderByAsc(DeviceModel::getModelCode);
        
        return Result.success(deviceModelService.list(wrapper));
    }

    @Operation(summary = "创建设备型号")
    @PostMapping
    public Result<Boolean> create(@RequestBody DeviceModel deviceModel) {
        boolean success = deviceModelService.save(deviceModel);
        return success ? Result.success(true) : Result.error("创建设备型号失败");
    }

    @Operation(summary = "更新设备型号")
    @PutMapping
    public Result<Boolean> update(@RequestBody DeviceModel deviceModel) {
        boolean success = deviceModelService.updateById(deviceModel);
        return success ? Result.success(true) : Result.error("更新设备型号失败");
    }

    @Operation(summary = "删除设备型号")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = deviceModelService.removeById(id);
        return success ? Result.success(true) : Result.error("删除设备型号失败");
    }

    @Operation(summary = "根据类别ID获取设备型号")
    @GetMapping("/by-category/{categoryId}")
    public Result<java.util.List<DeviceModel>> getByCategoryId(@PathVariable(name = "categoryId") Long categoryId) {
        LambdaQueryWrapper<DeviceModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceModel::getCategoryId, categoryId);
        wrapper.eq(DeviceModel::getStatus, 1);
        wrapper.orderByAsc(DeviceModel::getModelCode);
        return Result.success(deviceModelService.list(wrapper));
    }
}
