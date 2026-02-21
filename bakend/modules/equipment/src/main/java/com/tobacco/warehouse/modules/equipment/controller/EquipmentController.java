package com.tobacco.warehouse.modules.equipment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.equipment.entity.Equipment;
import com.tobacco.warehouse.modules.equipment.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 设备管理 Controller
 *
 * @author warehouse
 */
@Tag(name = "设备管理")
@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(summary = "分页查询设备")
    @GetMapping("/page")
    public Result<PageResult<Equipment>> getPage(PageRequest pageRequest, Equipment equipment) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(equipment.getEquipmentCode())) {
            wrapper.like(Equipment::getEquipmentCode, equipment.getEquipmentCode());
        }
        if (StringUtils.hasText(equipment.getEquipmentName())) {
            wrapper.like(Equipment::getEquipmentName, equipment.getEquipmentName());
        }
        if (equipment.getStatus() != null) {
            wrapper.eq(Equipment::getStatus, equipment.getStatus());
        }
        if (equipment.getModelId() != null) {
            wrapper.eq(Equipment::getModelId, equipment.getModelId());
        }
        
        wrapper.orderByDesc(Equipment::getCreateTime);
        
        Page<Equipment> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Equipment> result = equipmentService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取设备详情")
    @GetMapping("/{id}")
    public Result<Equipment> getById(@PathVariable(name = "id") Long id) {
        Equipment equipment = equipmentService.getById(id);
        return Result.success(equipment);
    }

    @Operation(summary = "获取所有启用的设备")
    @GetMapping("/list")
    public Result<java.util.List<Equipment>> getList(Equipment equipment) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(equipment.getEquipmentName())) {
            wrapper.like(Equipment::getEquipmentName, equipment.getEquipmentName());
        }
        if (equipment.getStatus() != null) {
            wrapper.eq(Equipment::getStatus, equipment.getStatus());
        }
        
        wrapper.eq(Equipment::getIsEnabled, 1);
        wrapper.orderByAsc(Equipment::getEquipmentCode);
        
        return Result.success(equipmentService.list(wrapper));
    }

    @Operation(summary = "获取所有设备(用于3D可视化)")
    @GetMapping("/all")
    public Result<java.util.List<Equipment>> getAll() {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Equipment::getIsEnabled, 1);
        wrapper.orderByAsc(Equipment::getEquipmentCode);
        return Result.success(equipmentService.list(wrapper));
    }

    @Operation(summary = "创建设备")
    @PostMapping
    public Result<Boolean> create(@RequestBody Equipment equipment) {
        boolean success = equipmentService.save(equipment);
        return success ? Result.success(true) : Result.error("创建设备失败");
    }

    @Operation(summary = "更新设备")
    @PutMapping
    public Result<Boolean> update(@RequestBody Equipment equipment) {
        boolean success = equipmentService.updateById(equipment);
        return success ? Result.success(true) : Result.error("更新设备失败");
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = equipmentService.removeById(id);
        return success ? Result.success(true) : Result.error("删除设备失败");
    }

    @Operation(summary = "更新设备状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable(name = "id") Long id, @RequestParam(name = "status") Integer status) {
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setStatus(status);
        boolean success = equipmentService.updateById(equipment);
        return success ? Result.success(true) : Result.error("更新设备状态失败");
    }
}
