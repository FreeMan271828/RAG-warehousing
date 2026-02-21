package com.tobacco.warehouse.modules.maintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.maintenance.entity.MaintainRecord;
import com.tobacco.warehouse.modules.maintenance.service.MaintainRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 维修记录管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "维修记录管理")
@RestController
@RequestMapping("/api/v1/maintenance/maintain")
@RequiredArgsConstructor
@Validated
public class MaintainRecordController {

    private final MaintainRecordService maintainRecordService;

    @Operation(summary = "分页查询维修记录")
    @GetMapping("/page")
    public Result<PageResult<MaintainRecord>> getPage(PageRequest pageRequest, MaintainRecord record) {
        LambdaQueryWrapper<MaintainRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(record.getRecordCode())) {
            wrapper.like(MaintainRecord::getRecordCode, record.getRecordCode());
        }
        if (record.getEquipmentId() != null) {
            wrapper.eq(MaintainRecord::getEquipmentId, record.getEquipmentId());
        }
        if (StringUtils.hasText(record.getFaultType())) {
            wrapper.eq(MaintainRecord::getFaultType, record.getFaultType());
        }
        if (record.getFaultLevel() != null) {
            wrapper.eq(MaintainRecord::getFaultLevel, record.getFaultLevel());
        }
        if (StringUtils.hasText(record.getResult())) {
            wrapper.eq(MaintainRecord::getResult, record.getResult());
        }
        if (record.getStatus() != null) {
            wrapper.eq(MaintainRecord::getStatus, record.getStatus());
        }
        
        wrapper.orderByDesc(MaintainRecord::getCreateTime);
        
        Page<MaintainRecord> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<MaintainRecord> result = maintainRecordService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取维修记录详情")
    @GetMapping("/{id}")
    public Result<MaintainRecord> getById(@PathVariable(name = "id") Long id) {
        MaintainRecord record = maintainRecordService.getById(id);
        return Result.success(record);
    }

    @Operation(summary = "获取所有维修记录")
    @GetMapping("/list")
    public Result<java.util.List<MaintainRecord>> getList(MaintainRecord record) {
        LambdaQueryWrapper<MaintainRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (record.getEquipmentId() != null) {
            wrapper.eq(MaintainRecord::getEquipmentId, record.getEquipmentId());
        }
        if (record.getStatus() != null) {
            wrapper.eq(MaintainRecord::getStatus, record.getStatus());
        }
        
        wrapper.orderByDesc(MaintainRecord::getCreateTime);
        
        return Result.success(maintainRecordService.list(wrapper));
    }

    @Operation(summary = "创建维修记录")
    @PostMapping
    public Result<Boolean> create(@Valid @RequestBody MaintainRecord record) {
        boolean success = maintainRecordService.save(record);
        return success ? Result.success(true) : Result.error("创建维修记录失败");
    }

    @Operation(summary = "更新维修记录")
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody MaintainRecord record) {
        boolean success = maintainRecordService.updateById(record);
        return success ? Result.success(true) : Result.error("更新维修记录失败");
    }

    @Operation(summary = "删除维修记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = maintainRecordService.removeById(id);
        return success ? Result.success(true) : Result.error("删除维修记录失败");
    }

    @Operation(summary = "更新维修记录状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable(name = "id") Long id, @RequestParam(name = "status") Integer status) {
        MaintainRecord record = new MaintainRecord();
        record.setId(id);
        record.setStatus(status);
        boolean success = maintainRecordService.updateById(record);
        return success ? Result.success(true) : Result.error("更新维修记录状态失败");
    }

    @Operation(summary = "分配维修人员")
    @PutMapping("/{id}/assign")
    public Result<Boolean> assign(@PathVariable(name = "id") Long id, @RequestParam(name = "maintainUser") Long maintainUser) {
        MaintainRecord record = new MaintainRecord();
        record.setId(id);
        record.setMaintainUser(maintainUser);
        record.setStatus(2); // 已分配
        boolean success = maintainRecordService.updateById(record);
        return success ? Result.success(true) : Result.error("分配维修人员失败");
    }
}
