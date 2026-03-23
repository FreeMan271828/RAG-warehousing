package com.tobacco.warehouse.modules.warehouse.task;

import com.tobacco.warehouse.modules.warehouse.service.AgvSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * AGV调度定时任务
 * 
 * @author warehouse
 */
@Slf4j
@Component
public class AgvSchedulerTask implements ApplicationRunner {

    @Autowired
    private AgvSchedulerService agvSchedulerService;

    private ScheduledExecutorService scheduler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 创建调度线程池（单线程）
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // 每秒执行一次调度任务
        scheduler.scheduleAtFixedRate(() -> {
            try {
                agvSchedulerService.schedule();
            } catch (Exception e) {
                log.error("AGV调度任务执行失败", e);
            }
        }, 1, 1, TimeUnit.SECONDS);

        log.info("AGV调度任务已启动，每秒执行一次");
    }

    /**
     * 停止调度器
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("AGV调度任务已停止");
        }
    }
}
