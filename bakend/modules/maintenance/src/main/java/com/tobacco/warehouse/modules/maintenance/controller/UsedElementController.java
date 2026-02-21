package com.tobacco.warehouse.modules.maintenance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.maintenance.entity.UsedElement;
import com.tobacco.warehouse.modules.maintenance.service.UsedElementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 零件更换记录管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "零件更换记录管理")
@RestController
@RequestMapping("/api/v1/maintenance/used-element")
@RequiredArgsConstructor
public class UsedElementController {

    private final UsedElementService usedElementService;

    @Operation(summary = "分页查询零件更换记录")
    @GetMapping("/page")
    public Result<PageResult<UsedElement>> getPage(PageRequest pageRequest, UsedElement usedElement) {
        LambdaQueryWrapper<UsedElement> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(usedElement.getRecordCode())) {
            wrapper.like(UsedElement::getRecordCode, usedElement.getRecordCode());
        }
        if (usedElement.getEquipmentId() != null) {
            wrapper.eq(UsedElement::getEquipmentId, usedElement.getEquipmentId());
        }
        if (usedElement.getElementId() != null) {
            wrapper.eq(UsedElement::getElementId, usedElement.getElementId());
        }
        if (usedElement.getMaintainRecordId() != null) {
            wrapper.eq(UsedElement::getMaintainRecordId, usedElement.getMaintainRecordId());
        }
        
        wrapper.orderByDesc(UsedElement::getCreateTime);
        
        Page<UsedElement> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<UsedElement> result = usedElementService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取零件更换记录详情")
    @GetMapping("/{id}")
    public Result<UsedElement> getById(@PathVariable(name = "id") Long id) {
        UsedElement usedElement = usedElementService.getById(id);
        return Result.success(usedElement);
    }

    @Operation(summary = "获取所有零件更换记录")
    @GetMapping("/list")
    public Result<java.util.List<UsedElement>> getList(UsedElement usedElement) {
        LambdaQueryWrapper<UsedElement> wrapper = new LambdaQueryWrapper<>();
        
        if (usedElement.getEquipmentId() != null) {
            wrapper.eq(UsedElement::getEquipmentId, usedElement.getEquipmentId());
        }
        if (usedElement.getElementId() != null) {
            wrapper.eq(UsedElement::getElementId, usedElement.getElementId());
        }
        
        wrapper.orderByDesc(UsedElement::getCreateTime);
        
        return Result.success(usedElementService.list(wrapper));
    }

    @Operation(summary = "创建零件更换记录")
    @PostMapping
    public Result<Boolean> create(@RequestBody UsedElement usedElement) {
        boolean success = usedElementService.save(usedElement);
        return success ? Result.success(true) : Result.error("创建零件更换记录失败");
    }

    @Operation(summary = "更新零件更换记录")
    @PutMapping
    public Result<Boolean> update(@RequestBody UsedElement usedElement) {
        boolean success = usedElementService.updateById(usedElement);
        return success ? Result.success(true) : Result.error("更新零件更换记录失败");
    }

    @Operation(summary = "删除零件更换记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = usedElementService.removeById(id);
        return success ? Result.success(true) : Result.error("删除零件更换记录失败");
    }
}
