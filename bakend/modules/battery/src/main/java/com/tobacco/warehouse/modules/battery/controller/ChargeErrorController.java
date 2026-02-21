package com.tobacco.warehouse.modules.battery.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.battery.entity.ChargeError;
import com.tobacco.warehouse.modules.battery.service.ChargeErrorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 充电故障记录 Controller
 * 
 * @author warehouse
 */
@Tag(name = "充电故障记录")
@RestController
@RequestMapping("/api/v1/battery/error")
@RequiredArgsConstructor
public class ChargeErrorController {

    private final ChargeErrorService chargeErrorService;

    @Operation(summary = "分页查询充电故障")
    @GetMapping("/page")
    public Result<PageResult<ChargeError>> getPage(PageRequest pageRequest, ChargeError error) {
        LambdaQueryWrapper<ChargeError> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(error.getBatteryCode())) {
            wrapper.like(ChargeError::getBatteryCode, error.getBatteryCode());
        }
        if (error.getBatteryId() != null) {
            wrapper.eq(ChargeError::getBatteryId, error.getBatteryId());
        }
        if (StringUtils.hasText(error.getErrorType())) {
            wrapper.eq(ChargeError::getErrorType, error.getErrorType());
        }
        if (error.getIsResolved() != null) {
            wrapper.eq(ChargeError::getIsResolved, error.getIsResolved());
        }
        
        wrapper.orderByDesc(ChargeError::getErrorTime);
        
        Page<ChargeError> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<ChargeError> result = chargeErrorService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取充电故障详情")
    @GetMapping("/{id}")
    public Result<ChargeError> getById(@PathVariable(name = "id") Long id) {
        ChargeError error = chargeErrorService.getById(id);
        return Result.success(error);
    }

    @Operation(summary = "获取所有未解决的充电故障")
    @GetMapping("/unresolved")
    public Result<java.util.List<ChargeError>> getUnresolvedList() {
        LambdaQueryWrapper<ChargeError> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargeError::getIsResolved, 0);
        wrapper.orderByDesc(ChargeError::getErrorTime);
        return Result.success(chargeErrorService.list(wrapper));
    }

    @Operation(summary = "获取所有充电故障")
    @GetMapping("/list")
    public Result<java.util.List<ChargeError>> getList(ChargeError error) {
        LambdaQueryWrapper<ChargeError> wrapper = new LambdaQueryWrapper<>();
        
        if (error.getBatteryId() != null) {
            wrapper.eq(ChargeError::getBatteryId, error.getBatteryId());
        }
        if (error.getIsResolved() != null) {
            wrapper.eq(ChargeError::getIsResolved, error.getIsResolved());
        }
        
        wrapper.orderByDesc(ChargeError::getErrorTime);
        
        return Result.success(chargeErrorService.list(wrapper));
    }

    @Operation(summary = "创建充电故障记录")
    @PostMapping
    public Result<Boolean> create(@RequestBody ChargeError error) {
        boolean success = chargeErrorService.save(error);
        return success ? Result.success(true) : Result.error("创建充电故障记录失败");
    }

    @Operation(summary = "更新充电故障记录")
    @PutMapping
    public Result<Boolean> update(@RequestBody ChargeError error) {
        boolean success = chargeErrorService.updateById(error);
        return success ? Result.success(true) : Result.error("更新充电故障记录失败");
    }

    @Operation(summary = "解决充电故障")
    @PutMapping("/{id}/resolve")
    public Result<Boolean> resolve(@PathVariable(name = "id") Long id, @RequestParam(name = "resolveMethod") String resolveMethod) {
        ChargeError error = new ChargeError();
        error.setId(id);
        error.setIsResolved(1);
        error.setResolveMethod(resolveMethod);
        boolean success = chargeErrorService.updateById(error);
        return success ? Result.success(true) : Result.error("解决充电故障失败");
    }

    @Operation(summary = "删除充电故障记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = chargeErrorService.removeById(id);
        return success ? Result.success(true) : Result.error("删除充电故障记录失败");
    }
}
