package com.tobacco.warehouse.modules.maintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.maintenance.entity.MaintenancePlan;
import com.tobacco.warehouse.modules.maintenance.service.MaintenancePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 保养计划管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "保养计划管理")
@RestController
@RequestMapping("/api/v1/maintenance/plan")
@RequiredArgsConstructor
public class MaintenancePlanController {

    private final MaintenancePlanService maintenancePlanService;

    @Operation(summary = "分页查询保养计划")
    @GetMapping("/page")
    public Result<PageResult<MaintenancePlan>> getPage(PageRequest pageRequest, MaintenancePlan plan) {
        LambdaQueryWrapper<MaintenancePlan> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(plan.getPlanCode())) {
            wrapper.like(MaintenancePlan::getPlanCode, plan.getPlanCode());
        }
        if (StringUtils.hasText(plan.getPlanName())) {
            wrapper.like(MaintenancePlan::getPlanName, plan.getPlanName());
        }
        if (plan.getEquipmentId() != null) {
            wrapper.eq(MaintenancePlan::getEquipmentId, plan.getEquipmentId());
        }
        if (StringUtils.hasText(plan.getPlanType())) {
            wrapper.eq(MaintenancePlan::getPlanType, plan.getPlanType());
        }
        if (plan.getStatus() != null) {
            wrapper.eq(MaintenancePlan::getStatus, plan.getStatus());
        }
        
        wrapper.orderByDesc(MaintenancePlan::getCreateTime);
        
        Page<MaintenancePlan> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<MaintenancePlan> result = maintenancePlanService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取保养计划详情")
    @GetMapping("/{id}")
    public Result<MaintenancePlan> getById(@PathVariable(name = "id") Long id) {
        MaintenancePlan plan = maintenancePlanService.getById(id);
        return Result.success(plan);
    }

    @Operation(summary = "获取所有启用的保养计划")
    @GetMapping("/list")
    public Result<java.util.List<MaintenancePlan>> getList(MaintenancePlan plan) {
        LambdaQueryWrapper<MaintenancePlan> wrapper = new LambdaQueryWrapper<>();
        
        if (plan.getEquipmentId() != null) {
            wrapper.eq(MaintenancePlan::getEquipmentId, plan.getEquipmentId());
        }
        if (plan.getStatus() != null) {
            wrapper.eq(MaintenancePlan::getStatus, plan.getStatus());
        }
        
        wrapper.orderByDesc(MaintenancePlan::getCreateTime);
        
        return Result.success(maintenancePlanService.list(wrapper));
    }

    @Operation(summary = "创建保养计划")
    @PostMapping
    public Result<Boolean> create(@RequestBody MaintenancePlan plan) {
        boolean success = maintenancePlanService.save(plan);
        return success ? Result.success(true) : Result.error("创建保养计划失败");
    }

    @Operation(summary = "更新保养计划")
    @PutMapping
    public Result<Boolean> update(@RequestBody MaintenancePlan plan) {
        boolean success = maintenancePlanService.updateById(plan);
        return success ? Result.success(true) : Result.error("更新保养计划失败");
    }

    @Operation(summary = "删除保养计划")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = maintenancePlanService.removeById(id);
        return success ? Result.success(true) : Result.error("删除保养计划失败");
    }

    @Operation(summary = "更新保养计划状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable(name = "id") Long id, @RequestParam(name = "status") Integer status) {
        MaintenancePlan plan = new MaintenancePlan();
        plan.setId(id);
        plan.setStatus(status);
        boolean success = maintenancePlanService.updateById(plan);
        return success ? Result.success(true) : Result.error("更新保养计划状态失败");
    }
}
