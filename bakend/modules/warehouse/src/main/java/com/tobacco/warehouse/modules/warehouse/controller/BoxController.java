package com.tobacco.warehouse.modules.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.warehouse.entity.Box;
import com.tobacco.warehouse.modules.warehouse.service.BoxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 箱子管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "箱子管理")
@RestController
@RequestMapping("/api/v1/warehouse/box")
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;

    @Operation(summary = "分页查询箱子")
    @GetMapping("/page")
    public Result<PageResult<Box>> getBoxPage(PageRequest pageRequest, Box box) {
        LambdaQueryWrapper<Box> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(box.getBoxCode())) {
            wrapper.like(Box::getBoxCode, box.getBoxCode());
        }
        if (box.getStatus() != null) {
            wrapper.eq(Box::getStatus, box.getStatus());
        }
        
        wrapper.orderByDesc(Box::getCreateTime);
        
        Page<Box> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Box> result = boxService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取所有箱子")
    @GetMapping("/list")
    public Result<java.util.List<Box>> getBoxList(Box box) {
        LambdaQueryWrapper<Box> wrapper = new LambdaQueryWrapper<>();
        
        if (box.getStatus() != null) {
            wrapper.eq(Box::getStatus, box.getStatus());
        }
        
        wrapper.orderByAsc(Box::getBoxCode);
        
        return Result.success(boxService.list(wrapper));
    }

    @Operation(summary = "获取箱子详情")
    @GetMapping("/{id}")
    public Result<Box> getBoxById(@PathVariable Long id) {
        return Result.success(boxService.getById(id));
    }

    @Operation(summary = "创建箱子")
    @PostMapping
    public Result<Boolean> createBox(@RequestBody Box box) {
        return boxService.save(box) ? Result.success(true) : Result.error("创建箱子失败");
    }

    @Operation(summary = "更新箱子")
    @PutMapping
    public Result<Boolean> updateBox(@RequestBody Box box) {
        return boxService.updateById(box) ? Result.success(true) : Result.error("更新箱子失败");
    }

    @Operation(summary = "删除箱子")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteBox(@PathVariable Long id) {
        return boxService.removeById(id) ? Result.success(true) : Result.error("删除箱子失败");
    }
}
