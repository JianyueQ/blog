package com.mojian.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 线程池配置类
 * <p>
 * 避免使用parallelStream()默认的ForkJoinPool.commonPool()，
 * 为IO密集型任务（如Redis操作）提供独立的线程池
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * IO密集型线程池
     * 用于验证码图片缓存等Redis/数据库异步操作
     */
    @Bean("ioExecutor")
    public ExecutorService ioExecutor() {
        return new ThreadPoolExecutor(
                4,                          // 核心线程数
                10,                         // 最大线程数
                60L,                        // 空闲线程存活时间
                TimeUnit.SECONDS,           // 时间单位
                new LinkedBlockingQueue<>(100), // 任务队列
                new ThreadFactory() {
                    private int count = 0;
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "io-pool-" + (++count));
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：由调用线程执行
        );
    }
}
