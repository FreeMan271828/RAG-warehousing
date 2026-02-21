package com.tobacco.warehouse.modules.battery.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.battery.entity.ChargingNow;
import com.tobacco.warehouse.modules.battery.service.ChargingNowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 充电实时数据 Controller
 * 
 * @author warehouse
 */
@Tag(name = "充电实时数据")
@RestController
@RequestMapping("/api/v1/battery/charging-now")
@RequiredArgsConstructor
public class ChargingNowController {

    private final ChargingNowService chargingNowService;

    @Operation(summary = "获取所有正在充电的实时数据")
    @GetMapping("/list")
    public Result<java.util.List<ChargingNow>> getChargingList() {
        LambdaQueryWrapper<ChargingNow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingNow::getIsCharging, 1);
        wrapper.orderByDesc(ChargingNow::getChargeStartTime);
        return Result.success(chargingNowService.list(wrapper));
    }

    @Operation(summary = "根据电池ID获取实时充电数据")
    @GetMapping("/battery/{batteryId}")
    public Result<ChargingNow> getByBatteryId(@PathVariable(name = "batteryId") Long batteryId) {
        LambdaQueryWrapper<ChargingNow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingNow::getBatteryId, batteryId);
        wrapper.eq(ChargingNow::getIsCharging, 1);
        wrapper.orderByDesc(ChargingNow::getChargeStartTime);
        wrapper.last("LIMIT 1");
        return Result.success(chargingNowService.getOne(wrapper));
    }

    @Operation(summary = "保存实时充电数据")
    @PostMapping
    public Result<Boolean> create(@RequestBody ChargingNow chargingNow) {
        boolean success = chargingNowService.save(chargingNow);
        return success ? Result.success(true) : Result.error("保存充电数据失败");
    }

    @Operation(summary = "更新实时充电数据")
    @PutMapping
    public Result<Boolean> update(@RequestBody ChargingNow chargingNow) {
        boolean success = chargingNowService.updateById(chargingNow);
        return success ? Result.success(true) : Result.error("更新充电数据失败");
    }

    @Operation(summary = "结束充电")
    @PutMapping("/{id}/stop")
    public Result<Boolean> stopCharging(@PathVariable(name = "id") Long id) {
        ChargingNow chargingNow = new ChargingNow();
        chargingNow.setId(id);
        chargingNow.setIsCharging(0);
        boolean success = chargingNowService.updateById(chargingNow);
        return success ? Result.success(true) : Result.error("结束充电失败");
    }
}
