package com.tobacco.warehouse.modules.equipment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.equipment.entity.Element;
import com.tobacco.warehouse.modules.equipment.service.ElementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 零件管理 Controller
 *
 * @author warehouse
 */
@Tag(name = "零件管理")
@RestController
@RequestMapping("/api/v1/element")
@RequiredArgsConstructor
public class ElementController {

    private final ElementService elementService;

    @Operation(summary = "分页查询零件")
    @GetMapping("/page")
    public Result<PageResult<Element>> getPage(PageRequest pageRequest, Element element) {
        LambdaQueryWrapper<Element> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(element.getElementCode())) {
            wrapper.like(Element::getElementCode, element.getElementCode());
        }
        if (StringUtils.hasText(element.getElementName())) {
            wrapper.like(Element::getElementName, element.getElementName());
        }
        if (element.getModelId() != null) {
            wrapper.eq(Element::getModelId, element.getModelId());
        }
        if (StringUtils.hasText(element.getCategory())) {
            wrapper.like(Element::getCategory, element.getCategory());
        }
        if (element.getStatus() != null) {
            wrapper.eq(Element::getStatus, element.getStatus());
        }

        wrapper.orderByDesc(Element::getCreateTime);
        
        Page<Element> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Element> result = elementService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取零件详情")
    @GetMapping("/{id}")
    public Result<Element> getById(@PathVariable(name = "id") Long id) {
        Element element = elementService.getById(id);
        return Result.success(element);
    }

    @Operation(summary = "获取所有零件")
    @GetMapping("/list")
    public Result<java.util.List<Element>> getList(Element element) {
        LambdaQueryWrapper<Element> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(element.getElementName())) {
            wrapper.like(Element::getElementName, element.getElementName());
        }
        if (element.getModelId() != null) {
            wrapper.eq(Element::getModelId, element.getModelId());
        }
        if (StringUtils.hasText(element.getCategory())) {
            wrapper.like(Element::getCategory, element.getCategory());
        }
        if (element.getStatus() != null) {
            wrapper.eq(Element::getStatus, element.getStatus());
        }

        wrapper.orderByAsc(Element::getElementCode);
        
        return Result.success(elementService.list(wrapper));
    }

    @Operation(summary = "获取库存不足的零件")
    @GetMapping("/low-stock")
    public Result<java.util.List<Element>> getLowStock() {
        LambdaQueryWrapper<Element> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Element::getStatus, 1);
        wrapper.apply("stock_quantity <= min_stock OR stock_quantity < 0");
        wrapper.orderByAsc(Element::getElementCode);
        return Result.success(elementService.list(wrapper));
    }

    @Operation(summary = "创建零件")
    @PostMapping
    public Result<Boolean> create(@RequestBody Element element) {
        boolean success = elementService.save(element);
        return success ? Result.success(true) : Result.error("创建零件失败");
    }

    @Operation(summary = "更新零件")
    @PutMapping
    public Result<Boolean> update(@RequestBody Element element) {
        boolean success = elementService.updateById(element);
        return success ? Result.success(true) : Result.error("更新零件失败");
    }

    @Operation(summary = "删除零件")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = elementService.removeById(id);
        return success ? Result.success(true) : Result.error("删除零件失败");
    }

    @Operation(summary = "更新零件库存")
    @PutMapping("/{id}/stock")
    public Result<Boolean> updateStock(@PathVariable(name = "id") Long id, @RequestParam(name = "stockQuantity") Integer stockQuantity) {
        Element element = new Element();
        element.setId(id);
        element.setStockQuantity(stockQuantity);
        boolean success = elementService.updateById(element);
        return success ? Result.success(true) : Result.error("更新库存失败");
    }
}
