package com.tobacco.warehouse.common.core.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 *
 * @author warehouse
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码，从1开始
     */
    private Integer pageNum = 1;

    /**
     * 每页显示条数
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方向: asc/desc
     */
    private String orderDirection = "desc";
}
