package com.tobacco.warehouse.modules.maintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.maintenance.entity.MaintenanceRecord;
import com.tobacco.warehouse.modules.maintenance.service.MaintenanceRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 保养记录管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "保养记录管理")
@RestController
@RequestMapping("/api/v1/maintenance/record")
@RequiredArgsConstructor
public class MaintenanceRecordController {

    private final MaintenanceRecordService maintenanceRecordService;

    @Operation(summary = "分页查询保养记录")
    @GetMapping("/page")
    public Result<PageResult<MaintenanceRecord>> getPage(PageRequest pageRequest, MaintenanceRecord record) {
        LambdaQueryWrapper<MaintenanceRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(record.getRecordCode())) {
            wrapper.like(MaintenanceRecord::getRecordCode, record.getRecordCode());
        }
        if (record.getEquipmentId() != null) {
            wrapper.eq(MaintenanceRecord::getEquipmentId, record.getEquipmentId());
        }
        if (record.getPlanId() != null) {
            wrapper.eq(MaintenanceRecord::getPlanId, record.getPlanId());
        }
        if (StringUtils.hasText(record.getMaintenanceType())) {
            wrapper.eq(MaintenanceRecord::getMaintenanceType, record.getMaintenanceType());
        }
        if (StringUtils.hasText(record.getResult())) {
            wrapper.eq(MaintenanceRecord::getResult, record.getResult());
        }
        if (record.getStatus() != null) {
            wrapper.eq(MaintenanceRecord::getStatus, record.getStatus());
        }
        
        wrapper.orderByDesc(MaintenanceRecord::getCreateTime);
        
        Page<MaintenanceRecord> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<MaintenanceRecord> result = maintenanceRecordService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取保养记录详情")
    @GetMapping("/{id}")
    public Result<MaintenanceRecord> getById(@PathVariable(name = "id") Long id) {
        MaintenanceRecord record = maintenanceRecordService.getById(id);
        return Result.success(record);
    }

    @Operation(summary = "获取所有保养记录")
    @GetMapping("/list")
    public Result<java.util.List<MaintenanceRecord>> getList(MaintenanceRecord record) {
        LambdaQueryWrapper<MaintenanceRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (record.getEquipmentId() != null) {
            wrapper.eq(MaintenanceRecord::getEquipmentId, record.getEquipmentId());
        }
        
        wrapper.orderByDesc(MaintenanceRecord::getCreateTime);
        
        return Result.success(maintenanceRecordService.list(wrapper));
    }

    @Operation(summary = "创建保养记录")
    @PostMapping
    public Result<Boolean> create(@RequestBody MaintenanceRecord record) {
        boolean success = maintenanceRecordService.save(record);
        return success ? Result.success(true) : Result.error("创建保养记录失败");
    }

    @Operation(summary = "更新保养记录")
    @PutMapping
    public Result<Boolean> update(@RequestBody MaintenanceRecord record) {
        boolean success = maintenanceRecordService.updateById(record);
        return success ? Result.success(true) : Result.error("更新保养记录失败");
    }

    @Operation(summary = "删除保养记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = maintenanceRecordService.removeById(id);
        return success ? Result.success(true) : Result.error("删除保养记录失败");
    }

    @Operation(summary = "更新保养记录状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable(name = "id") Long id, @RequestParam(name = "status") Integer status) {
        MaintenanceRecord record = new MaintenanceRecord();
        record.setId(id);
        record.setStatus(status);
        boolean success = maintenanceRecordService.updateById(record);
        return success ? Result.success(true) : Result.error("更新保养记录状态失败");
    }
}
