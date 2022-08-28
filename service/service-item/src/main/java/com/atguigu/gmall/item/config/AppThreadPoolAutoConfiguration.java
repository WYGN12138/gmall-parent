package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 配置线程池
 */
@Configuration
public class AppThreadPoolAutoConfiguration {
    @Bean
    public ThreadPoolExecutor coreExecutor() {
        /*int corePoolSize, 核心线程池：cpU核心数 4
         *int maximumPoolSize,最大线程数： 8
         *Long keepAliveTime,线程存活时间
         *TimeUnit unit, 时间单位
         *BLockingQueue<Runnable>workQueue,
         * ThreadFactorythreadFactory,
         * RejectedExecutionHandler handler
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                4,
                8,
                5,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue(2000),//由项目最终能站多大内存决定
                new ThreadFactory() { //负责给线程池创建线程
                    int i=0; //记录自增id
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("core-thread-"+ i++);
                        return thread;
                    }
                },
                //生产环境用callerRuns 谁给我谁解决
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
//        new ArrayBlockingQueue(10);  数组不连续
//        new LinkedBlockingDeque(10);  底层链表连续


        return executor;
    }
}
