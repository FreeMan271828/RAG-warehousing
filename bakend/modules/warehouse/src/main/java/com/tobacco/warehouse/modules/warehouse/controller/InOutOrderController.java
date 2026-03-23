package com.tobacco.warehouse.modules.warehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageRequest;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.warehouse.entity.InOutOrder;
import com.tobacco.warehouse.modules.warehouse.enums.OrderStatus;
import com.tobacco.warehouse.modules.warehouse.service.InOutOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 出入库工单管理 Controller
 * 
 * @author warehouse
 */
@Tag(name = "出入库工单管理")
@RestController
@RequestMapping("/api/v1/warehouse/inout")
@RequiredArgsConstructor
public class InOutOrderController {

    private final InOutOrderService inOutOrderService;

    @Operation(summary = "分页查询出入库工单")
    @GetMapping("/page")
    public Result<PageResult<InOutOrder>> getInOutOrderPage(PageRequest pageRequest, InOutOrder order) {
        LambdaQueryWrapper<InOutOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(order.getOrderCode())) {
            wrapper.like(InOutOrder::getOrderCode, order.getOrderCode());
        }
        if (StringUtils.hasText(order.getOrderType())) {
            wrapper.eq(InOutOrder::getOrderType, order.getOrderType());
        }
        if (StringUtils.hasText(order.getOrderStatus())) {
            wrapper.eq(InOutOrder::getOrderStatus, order.getOrderStatus());
        }
        
        wrapper.orderByDesc(InOutOrder::getCreateTime);
        
        Page<InOutOrder> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<InOutOrder> result = inOutOrderService.page(page, wrapper);
        
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取所有出入库工单")
    @GetMapping("/list")
    public Result<List<InOutOrder>> getInOutOrderList(InOutOrder order) {
        LambdaQueryWrapper<InOutOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (order.getOrderStatus() != null) {
            wrapper.eq(InOutOrder::getOrderStatus, order.getOrderStatus());
        }
        
        wrapper.orderByDesc(InOutOrder::getCreateTime);
        
        return Result.success(inOutOrderService.list(wrapper));
    }

    @Operation(summary = "获取出入库工单详情")
    @GetMapping("/{id}")
    public Result<InOutOrder> getInOutOrderById(@PathVariable("id") Long id) {
        return Result.success(inOutOrderService.getById(id));
    }

    @Operation(summary = "创建出入库工单")
    @PostMapping
    public Result<InOutOrder> createInOutOrder(@RequestBody InOutOrder order) {
        try {
            InOutOrder created = inOutOrderService.createOrder(order);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error("创建出入库工单失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新出入库工单")
    @PutMapping
    public Result<Boolean> updateInOutOrder(@RequestBody InOutOrder order) {
        return inOutOrderService.updateById(order) ? Result.success(true) : Result.error("更新出入库工单失败");
    }

    @Operation(summary = "删除出入库工单")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteInOutOrder(@PathVariable("id") Long id) {
        return inOutOrderService.removeById(id) ? Result.success(true) : Result.error("删除出入库工单失败");
    }

    @Operation(summary = "推进工单状态")
    @PutMapping("/{id}/transition")
    public Result<InOutOrder> transitionStatus(
            @PathVariable("id") Long id,
            @Parameter(description = "目标状态编码") @RequestParam(value = "targetStatus") String targetStatus,
            @Parameter(description = "操作人ID") @RequestParam(value = "operatorId") Long operatorId,
            @Parameter(description = "操作人名称") @RequestParam(value = "operatorName") String operatorName,
            @Parameter(description = "备注") @RequestParam(value = "remark", required = false) String remark) {
        try {
            InOutOrder order = inOutOrderService.transitionStatus(id, targetStatus, operatorId, operatorName, remark);
            return Result.success(order);
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("状态推进失败: " + e.getMessage());
        }
    }

    @Operation(summary = "审核工单")
    @PutMapping("/{id}/review")
    public Result<InOutOrder> reviewOrder(
            @PathVariable("id") Long id,
            @Parameter(description = "是否通过") @RequestParam(value = "approved") boolean approved,
            @Parameter(description = "审核人ID") @RequestParam(value = "reviewerId") Long reviewerId,
            @Parameter(description = "审核人名称") @RequestParam(value = "reviewerName") String reviewerName,
            @Parameter(description = "审核备注") @RequestParam(value = "remark", required = false) String remark) {
        try {
            InOutOrder order = inOutOrderService.reviewOrder(id, approved, reviewerId, reviewerName, remark);
            return Result.success(order);
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("审核失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取工单可用的操作列表")
    @GetMapping("/{id}/actions")
    public Result<List<String>> getAvailableActions(@PathVariable("id") Long id) {
        try {
            List<String> actions = inOutOrderService.getAvailableActions(id);
            return Result.success(actions);
        } catch (Exception e) {
            return Result.error("获取可用操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取所有状态列表")
    @GetMapping("/statuses")
    public Result<List<OrderStatus>> getAllStatuses() {
        return Result.success(inOutOrderService.getAllStatuses());
    }
}
