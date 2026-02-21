package com.tobacco.warehouse.common.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 处理 createTime, updateTime, createBy, updateBy, deleted 等字段的自动填充
 *
 * @author warehouse
 */
@Component
public class DefaultDBFieldHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 填充删除标记（默认0-未删除）
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
        // 填充创建人
        this.strictInsertFill(metaObject, "createBy", Long.class, getCurrentUserId());
        // 填充更新人
        this.strictInsertFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 填充更新人
        this.strictUpdateFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

    /**
     * 获取当前登录用户ID
     * 如果未登录返回 0L
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                // 尝试从 principal 中获取用户ID
                Object principal = authentication.getPrincipal();
                if (principal instanceof Long) {
                    return (Long) principal;
                } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    // 返回默认值，实际项目中应该从用户服务获取
                    return 1L;
                }
            }
        } catch (Exception e) {
            // 如果获取失败，返回默认值
        }
        return 0L;
    }
}
