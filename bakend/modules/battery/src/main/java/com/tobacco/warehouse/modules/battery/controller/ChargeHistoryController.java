package com.tobacco.warehouse.modules.battery.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.battery.entity.ChargeHistory;
import com.tobacco.warehouse.modules.battery.service.ChargeHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 充电历史记录 Controller
 * 
 * @author warehouse
 */
@Tag(name = "充电历史记录")
@RestController
@RequestMapping("/api/v1/battery/history")
@RequiredArgsConstructor
public class ChargeHistoryController {

    private final ChargeHistoryService chargeHistoryService;

    @Operation(summary = "分页查询充电历史")
    @GetMapping("/page")
    public Result<PageResult<ChargeHistory>> getPage(PageRequest pageRequest, ChargeHistory history) {
        LambdaQueryWrapper<ChargeHistory> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(history.getBatteryCode())) {
            wrapper.like(ChargeHistory::getBatteryCode, history.getBatteryCode());
        }
        if (history.getBatteryId() != null) {
            wrapper.eq(ChargeHistory::getBatteryId, history.getBatteryId());
        }
        if (history.getIsCompleted() != null) {
            wrapper.eq(ChargeHistory::getIsCompleted, history.getIsCompleted());
        }
        if (history.getIsError() != null) {
            wrapper.eq(ChargeHistory::getIsError, history.getIsError());
        }
        
        wrapper.orderByDesc(ChargeHistory::getChargeStartTime);
        
        Page<ChargeHistory> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<ChargeHistory> result = chargeHistoryService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取充电历史详情")
    @GetMapping("/{id}")
    public Result<ChargeHistory> getById(@PathVariable(name = "id") Long id) {
        ChargeHistory history = chargeHistoryService.getById(id);
        return Result.success(history);
    }

    @Operation(summary = "获取所有充电历史")
    @GetMapping("/list")
    public Result<java.util.List<ChargeHistory>> getList(ChargeHistory history) {
        LambdaQueryWrapper<ChargeHistory> wrapper = new LambdaQueryWrapper<>();
        
        if (history.getBatteryId() != null) {
            wrapper.eq(ChargeHistory::getBatteryId, history.getBatteryId());
        }
        
        wrapper.orderByDesc(ChargeHistory::getChargeStartTime);
        
        return Result.success(chargeHistoryService.list(wrapper));
    }

    @Operation(summary = "获取电池最近的充电历史")
    @GetMapping("/battery/{batteryId}/latest")
    public Result<java.util.List<ChargeHistory>> getLatestByBatteryId(@PathVariable(name = "batteryId") Long batteryId) {
        LambdaQueryWrapper<ChargeHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargeHistory::getBatteryId, batteryId);
        wrapper.orderByDesc(ChargeHistory::getChargeStartTime);
        wrapper.last("LIMIT 10");
        return Result.success(chargeHistoryService.list(wrapper));
    }

    @Operation(summary = "创建充电历史记录")
    @PostMapping
    public Result<Boolean> create(@RequestBody ChargeHistory history) {
        boolean success = chargeHistoryService.save(history);
        return success ? Result.success(true) : Result.error("创建充电历史记录失败");
    }

    @Operation(summary = "删除充电历史记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable(name = "id") Long id) {
        boolean success = chargeHistoryService.removeById(id);
        return success ? Result.success(true) : Result.error("删除充电历史记录失败");
    }
}
