package com.tobacco.warehouse.modules.warehouse.controller;

import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.warehouse.entity.AgvStatus;
import com.tobacco.warehouse.modules.warehouse.service.AgvStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AGV小车管理 REST API
 * 
 * @author warehouse
 */
@RestController
@RequestMapping("/api/v1/warehouse/agv")
public class AgvController {

    @Autowired
    private AgvStatusService agvStatusService;

    /**
     * 获取所有AGV列表
     */
    @GetMapping("/list")
    public Result list() {
        List<AgvStatus> list = agvStatusService.listAgvWithEquipment();
        return Result.success(list);
    }

    /**
     * 根据ID获取AGV详情
     */
    @GetMapping("/{agvId}")
    public Result getById(@PathVariable Long agvId) {
        AgvStatus agvStatus = agvStatusService.getByAgvId(agvId);
        return Result.success(agvStatus);
    }

    /**
     * 获取可用的小车列表
     */
    @GetMapping("/available")
    public Result getAvailableAgvs() {
        List<AgvStatus> list = agvStatusService.getAvailableAgvs();
        return Result.success(list);
    }

    /**
     * 更新电量
     */
    @PutMapping("/battery/{agvId}")
    public Result updateBattery(@PathVariable Long agvId, 
                                    @RequestParam("batteryLevel") java.math.BigDecimal batteryLevel) {
        boolean result = agvStatusService.updateBatteryLevel(agvId, batteryLevel);
        return result ? Result.success() : Result.error("更新失败");
    }

    /**
     * 更新小车状态
     */
    @PutMapping("/status/{agvId}")
    public Result updateStatus(@PathVariable("agvId") Long agvId,
                                   @RequestParam("status") String status) {
        boolean result = agvStatusService.updateAgvStatus(agvId, status);
        return result ? Result.success() : Result.error("更新失败");
    }

    /**
     * 开始充电
     */
    @PostMapping("/charge/start")
    public Result startCharging(@RequestParam("agvId") Long agvId, 
                                    @RequestParam("stationId") Long stationId) {
        boolean result = agvStatusService.startCharging(agvId, stationId);
        return result ? Result.success() : Result.error("开始充电失败");
    }

    /**
     * 结束充电
     */
    @PostMapping("/charge/end")
    public Result endCharging(@RequestParam("agvId") Long agvId,
                                  @RequestParam(required = false) String result,
                                  @RequestParam(required = false) String interruptReason) {
        boolean success = agvStatusService.endCharging(agvId, result, interruptReason);
        return success ? Result.success() : Result.error("结束充电失败");
    }

    /**
     * 检查是否需要充电
     */
    @GetMapping("/needs-charge/{agvId}")
    public Result needsCharging(@PathVariable Long agvId) {
        boolean needs = agvStatusService.needsCharging(agvId);
        return Result.success(needs);
    }
}
