package com.tobacco.warehouse.modules.equipment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobacco.warehouse.common.core.domain.PageResult;
import com.tobacco.warehouse.common.core.domain.Result;
import com.tobacco.warehouse.modules.equipment.entity.GuideDoc;
import com.tobacco.warehouse.modules.equipment.entity.GuideVideo;
import com.tobacco.warehouse.modules.equipment.mapper.GuideDocMapper;
import com.tobacco.warehouse.modules.equipment.mapper.GuideVideoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 知识库管理 Controller
 * 包含：文档管理、视频管理
 *
 * @author warehouse
 */
@Tag(name = "知识库管理")
@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final GuideDocMapper guideDocMapper;
    private final GuideVideoMapper guideVideoMapper;

    // ==================== 文档管理 ====================

    @Operation(summary = "分页查询文档")
    @GetMapping("/doc/page")
    public Result<PageResult<GuideDoc>> getDocPage(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            GuideDoc query) {
        Page<GuideDoc> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<GuideDoc> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getDocTitle())) {
            wrapper.like(GuideDoc::getDocTitle, query.getDocTitle());
        }
        if (query.getCategoryId() != null) {
            wrapper.eq(GuideDoc::getCategoryId, query.getCategoryId());
        }
        if (StringUtils.hasText(query.getDocType())) {
            wrapper.eq(GuideDoc::getDocType, query.getDocType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(GuideDoc::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(GuideDoc::getCreateTime);
        IPage<GuideDoc> result = guideDocMapper.selectPage(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取文档详情")
    @GetMapping("/doc/{id}")
    public Result<GuideDoc> getDocById(@PathVariable Long id) {
        GuideDoc doc = guideDocMapper.selectById(id);
        // 增加浏览次数
        if (doc != null) {
            doc.setViewCount(doc.getViewCount() + 1);
            guideDocMapper.updateById(doc);
        }
        return Result.success(doc);
    }

    @Operation(summary = "创建文档")
    @PostMapping("/doc")
    public Result<Boolean> createDoc(@RequestBody GuideDoc doc) {
        doc.setStatus(1);
        doc.setViewCount(0);
        return Result.success(guideDocMapper.insert(doc) > 0);
    }

    @Operation(summary = "更新文档")
    @PutMapping("/doc")
    public Result<Boolean> updateDoc(@RequestBody GuideDoc doc) {
        return Result.success(guideDocMapper.updateById(doc) > 0);
    }

    @Operation(summary = "删除文档")
    @DeleteMapping("/doc/{id}")
    public Result<Boolean> deleteDoc(@PathVariable Long id) {
        return Result.success(guideDocMapper.deleteById(id) > 0);
    }

    @Operation(summary = "获取文档列表(用于下拉选择)")
    @GetMapping("/doc/list")
    public Result<java.util.List<GuideDoc>> getDocList(GuideDoc query) {
        LambdaQueryWrapper<GuideDoc> wrapper = new LambdaQueryWrapper<>();
        if (query.getCategoryId() != null) {
            wrapper.eq(GuideDoc::getCategoryId, query.getCategoryId());
        }
        if (StringUtils.hasText(query.getDocType())) {
            wrapper.eq(GuideDoc::getDocType, query.getDocType());
        }
        wrapper.eq(GuideDoc::getStatus, 1);
        wrapper.orderByDesc(GuideDoc::getCreateTime);
        return Result.success(guideDocMapper.selectList(wrapper));
    }

    // ==================== 视频管理 ====================

    @Operation(summary = "分页查询视频")
    @GetMapping("/video/page")
    public Result<PageResult<GuideVideo>> getVideoPage(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            GuideVideo query) {
        Page<GuideVideo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<GuideVideo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getVideoTitle())) {
            wrapper.like(GuideVideo::getVideoTitle, query.getVideoTitle());
        }
        if (query.getCategoryId() != null) {
            wrapper.eq(GuideVideo::getCategoryId, query.getCategoryId());
        }
        if (StringUtils.hasText(query.getVideoType())) {
            wrapper.eq(GuideVideo::getVideoType, query.getVideoType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(GuideVideo::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(GuideVideo::getCreateTime);
        IPage<GuideVideo> result = guideVideoMapper.selectPage(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "获取视频详情")
    @GetMapping("/video/{id}")
    public Result<GuideVideo> getVideoById(@PathVariable Long id) {
        GuideVideo video = guideVideoMapper.selectById(id);
        // 增加播放次数
        if (video != null) {
            video.setViewCount(video.getViewCount() + 1);
            guideVideoMapper.updateById(video);
        }
        return Result.success(video);
    }

    @Operation(summary = "创建视频")
    @PostMapping("/video")
    public Result<Boolean> createVideo(@RequestBody GuideVideo video) {
        video.setStatus(1);
        video.setViewCount(0);
        return Result.success(guideVideoMapper.insert(video) > 0);
    }

    @Operation(summary = "更新视频")
    @PutMapping("/video")
    public Result<Boolean> updateVideo(@RequestBody GuideVideo video) {
        return Result.success(guideVideoMapper.updateById(video) > 0);
    }

    @Operation(summary = "删除视频")
    @DeleteMapping("/video/{id}")
    public Result<Boolean> deleteVideo(@PathVariable Long id) {
        return Result.success(guideVideoMapper.deleteById(id) > 0);
    }

    @Operation(summary = "获取视频列表(用于下拉选择)")
    @GetMapping("/video/list")
    public Result<java.util.List<GuideVideo>> getVideoList(GuideVideo query) {
        LambdaQueryWrapper<GuideVideo> wrapper = new LambdaQueryWrapper<>();
        if (query.getCategoryId() != null) {
            wrapper.eq(GuideVideo::getCategoryId, query.getCategoryId());
        }
        if (StringUtils.hasText(query.getVideoType())) {
            wrapper.eq(GuideVideo::getVideoType, query.getVideoType());
        }
        wrapper.eq(GuideVideo::getStatus, 1);
        wrapper.orderByDesc(GuideVideo::getCreateTime);
        return Result.success(guideVideoMapper.selectList(wrapper));
    }
}
