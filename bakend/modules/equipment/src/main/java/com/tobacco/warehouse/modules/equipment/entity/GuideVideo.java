package com.tobacco.warehouse.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库视频 实体类
 *
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("guide_video")
public class GuideVideo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 视频标题
     */
    private String videoTitle;

    /**
     * 设备类别ID
     */
    private Long categoryId;

    /**
     * 设备型号ID
     */
    private Long modelId;

    /**
     * 视频类型：operation-demo-操作演示 maintenance-tutorial-维修教程
     */
    private String videoType;

    /**
     * 视频路径
     */
    private String videoPath;

    /**
     * 缩略图路径
     */
    private String thumbnailPath;

    /**
     * 时长(秒)
     */
    private Integer duration;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 视频描述
     */
    private String description;

    /**
     * 播放次数
     */
    private Integer viewCount;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
