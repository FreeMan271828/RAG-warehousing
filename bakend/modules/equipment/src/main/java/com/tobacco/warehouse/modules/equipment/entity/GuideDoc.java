package com.tobacco.warehouse.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tobacco.warehouse.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库文档 实体类
 *
 * @author warehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("guide_doc")
public class GuideDoc extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 文档标题
     */
    private String docTitle;

    /**
     * 设备类别ID
     */
    private Long categoryId;

    /**
     * 设备型号ID
     */
    private Long modelId;

    /**
     * 文档类型：operation-操作手册 maintenance-维护指南 troubleshooting-故障处理
     */
    private String docType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文档内容摘要
     */
    private String content;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}
