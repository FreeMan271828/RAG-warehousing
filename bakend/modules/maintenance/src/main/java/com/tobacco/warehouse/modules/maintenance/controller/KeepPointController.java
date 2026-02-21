package com.tobacco.warehouse.modules.maintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.maintenance.entity.KeepPoint;
import com.tobacco.warehouse.modules.maintenance.service.KeepPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 保养点管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "保养点管理")
@RestController
@RequestMapping("/api/v1/maintenance/keep-point")
@RequiredArgsConstructor
public class KeepPointController {

    private final KeepPointService keepPointService;

    @Operation(summary = "分页查询保养点")
    @GetMapping("/page")
    public Result<PageResult<KeepPoint>> getPage(PageRequest pageRequest, KeepPoint keepPoint) {
        LambdaQueryWrapper<KeepPoint> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keepPoint.getPointCode())) {
            wrapper.like(KeepPoint::getPointCode, keepPoint.getPointCode());
        }
        if (StringUtils.hasText(keepPoint.getPointName())) {
            wrapper.like(KeepPoint::getPointName, keepPoint.getPointName());
        }
        if (keepPoint.getCategoryId() != null) {
            wrapper.eq(KeepPoint::getCategoryId, keepPoint.getCategoryId());
        }
        if (keepPoint.getStatus() != null) {
            wrapper.eq(KeepPoint::getStatus, keepPoint.getStatus());
        }
        
        wrapper.orderByAsc(KeepPoint::getSortOrder);
        wrapper.orderByDesc(KeepPoint::getCreateTime);
        
        Page<KeepPoint> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<KeepPoint> result = keepPointService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取保养点详情")
    @GetMapping("/{id}")
    public Result<KeepPoint> getById(@PathVariable(name = "id") Long id) {
        KeepPoint keepPoint = keepPointService.getById(id);
        return Result.success(keepPoint);
    }

    @Operation(summary = "获取所有启用的保养点")
    @GetMapping("/list")
    public Result<java.util.List<KeepPoint>> getList(KeepPoint keepPoint) {
        LambdaQueryWrapper<KeepPoint> wrapper = new LambdaQueryWrapper<>();
        
        if (keepPoint.getCategoryId() != null) {
            wrapper.eq(KeepPoint::getCategoryId, keepPoint.getCategoryId());
        }
        
        wrapper.eq(KeepPoint::getStatus, 1);
        wrapper.orderByAsc(KeepPoint::getSortOrder);
        
        return Result.success(keepPointService.list(wrapper));
    }

    @Operation(summary = "创建保养点")
    @PostMapping
    public Result<Boolean> create(@RequestBody KeepPoint keepPoint) {
        boolean success = keepPointService.save(keepPoint);
        return success ? Result.success(true) : Result.error("创建保养点失败");
    }

    @Operation(summary = "更新保养点")
    @PutMapping
    public Result<Boolean> update(@RequestBody KeepPoint keepPoint) {
        boolean success = keepPointService.updateById(keepPoint);
        return success ? Result.success(true) : Result.error("更新保养点失败");
    }

    @Operation(summary = "删除保养点")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = keepPointService.removeById(id);
        return success ? Result.success(true) : Result.error("删除保养点失败");
    }

    @Operation(summary = "更新保养点状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable(name = "id") Long id, @RequestParam(name = "status") Integer status) {
        KeepPoint keepPoint = new KeepPoint();
        keepPoint.setId(id);
        keepPoint.setStatus(status);
        boolean success = keepPointService.updateById(keepPoint);
        return success ? Result.success(true) : Result.error("更新保养点状态失败");
    }
}
