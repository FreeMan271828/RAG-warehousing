package com.tobacco.warehouse.modules.equipment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.equipment.entity.DeviceCategory;
import com.tobacco.warehouse.modules.equipment.service.DeviceCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 设备类别管理 Controller
 *
 * @author warehouse
 */
@Tag(name = "设备类别管理")
@RestController
@RequestMapping("/api/v1/device-category")
@RequiredArgsConstructor
public class DeviceCategoryController {

    private final DeviceCategoryService deviceCategoryService;

    @Operation(summary = "分页查询设备类别")
    @GetMapping("/page")
    public Result<PageResult<DeviceCategory>> getPage(PageRequest pageRequest, DeviceCategory deviceCategory) {
        LambdaQueryWrapper<DeviceCategory> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(deviceCategory.getCategoryCode())) {
            wrapper.like(DeviceCategory::getCategoryCode, deviceCategory.getCategoryCode());
        }
        if (StringUtils.hasText(deviceCategory.getCategoryName())) {
            wrapper.like(DeviceCategory::getCategoryName, deviceCategory.getCategoryName());
        }
        if (deviceCategory.getParentId() != null) {
            wrapper.eq(DeviceCategory::getParentId, deviceCategory.getParentId());
        }
        if (deviceCategory.getStatus() != null) {
            wrapper.eq(DeviceCategory::getStatus, deviceCategory.getStatus());
        }
        
        wrapper.orderByAsc(DeviceCategory::getSortOrder);
        wrapper.orderByDesc(DeviceCategory::getCreateTime);
        
        Page<DeviceCategory> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<DeviceCategory> result = deviceCategoryService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取设备类别详情")
    @GetMapping("/{id}")
    public Result<DeviceCategory> getById(@PathVariable(name = "id") Long id) {
        DeviceCategory deviceCategory = deviceCategoryService.getById(id);
        return Result.success(deviceCategory);
    }

    @Operation(summary = "获取所有设备类别")
    @GetMapping("/list")
    public Result<java.util.List<DeviceCategory>> getList(DeviceCategory deviceCategory) {
        LambdaQueryWrapper<DeviceCategory> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(deviceCategory.getCategoryName())) {
            wrapper.like(DeviceCategory::getCategoryName, deviceCategory.getCategoryName());
        }
        if (deviceCategory.getParentId() != null) {
            wrapper.eq(DeviceCategory::getParentId, deviceCategory.getParentId());
        }
        if (deviceCategory.getStatus() != null) {
            wrapper.eq(DeviceCategory::getStatus, deviceCategory.getStatus());
        }
        
        wrapper.orderByAsc(DeviceCategory::getSortOrder);
        wrapper.orderByAsc(DeviceCategory::getCategoryCode);
        
        return Result.success(deviceCategoryService.list(wrapper));
    }

    @Operation(summary = "获取顶级设备类别")
    @GetMapping("/top")
    public Result<java.util.List<DeviceCategory>> getTopList() {
        LambdaQueryWrapper<DeviceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceCategory::getParentId, 0);
        wrapper.eq(DeviceCategory::getStatus, 1);
        wrapper.orderByAsc(DeviceCategory::getSortOrder);
        wrapper.orderByAsc(DeviceCategory::getCategoryCode);
        return Result.success(deviceCategoryService.list(wrapper));
    }

    @Operation(summary = "创建设备类别")
    @PostMapping
    public Result<Boolean> create(@RequestBody DeviceCategory deviceCategory) {
        boolean success = deviceCategoryService.save(deviceCategory);
        return success ? Result.success(true) : Result.error("创建设备类别失败");
    }

    @Operation(summary = "更新设备类别")
    @PutMapping
    public Result<Boolean> update(@RequestBody DeviceCategory deviceCategory) {
        boolean success = deviceCategoryService.updateById(deviceCategory);
        return success ? Result.success(true) : Result.error("更新设备类别失败");
    }

    @Operation(summary = "删除设备类别")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = deviceCategoryService.removeById(id);
        return success ? Result.success(true) : Result.error("删除设备类别失败");
    }
}
