package com.tobacco.warehouse.modules.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.warehouse.entity.ChargingStation;
import com.tobacco.warehouse.modules.warehouse.service.ChargingStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 充电站管理 REST API
 * 
 * @author warehouse
 */
@RestController
@RequestMapping("/api/v1/warehouse/charging-station")
public class ChargingStationController {

    @Autowired
    private ChargingStationService chargingStationService;

    /**
     * 获取所有充电站列表
     */
    @GetMapping("/list")
    public Result list() {
        List<ChargingStation> list = chargingStationService.list();
        return Result.success(list);
    }

    /**
     * 分页查询充电站
     */
    @GetMapping("/page")
    public Result page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ChargingStation> page = new Page<>(pageNum, pageSize);
        Page<ChargingStation> result = chargingStationService.page(page);
        return Result.success(result);
    }

    /**
     * 根据ID获取充电站详情
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ChargingStation station = chargingStationService.getById(id);
        return Result.success(station);
    }

    /**
     * 获取可用的充电站
     */
    @GetMapping("/available")
    public Result getAvailable() {
        List<ChargingStation> list = chargingStationService.list(
                new LambdaQueryWrapper<ChargingStation>()
                        .eq(ChargingStation::getStatus, 1)
        );
        return Result.success(list);
    }

    /**
     * 创建充电站
     */
    @PostMapping
    public Result add(@RequestBody ChargingStation station) {
        boolean result = chargingStationService.save(station);
        return result ? Result.success() : Result.error("创建失败");
    }

    /**
     * 更新充电站
     */
    @PutMapping
    public Result update(@RequestBody ChargingStation station) {
        boolean result = chargingStationService.updateById(station);
        return result ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除充电站
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean result = chargingStationService.removeById(id);
        return result ? Result.success() : Result.error("删除失败");
    }
}
