package com.atguigu.gmall.common.config.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 配置线程池
 * 1.AppThreadPoolProperties 里面的所有属性和指定配置绑定
 * 2；AppThreadPoolProperties 组件自动放到容器中
 */
@EnableConfigurationProperties(AppThreadPoolProperties.class)
@Configuration
public class AppThreadPoolAutoConfiguration {


    @Autowired
    AppThreadPoolProperties appThreadPoolProperties;

    @Value("${spring.application.name}")
    String applicationName;
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
                appThreadPoolProperties.getCore(),
                appThreadPoolProperties.getMax(),
                appThreadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(appThreadPoolProperties.getQueueSize()),//由项目最终能站多大内存决定
                new ThreadFactory() { //负责给线程池创建线程
                    int i=0; //记录自增id
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName(applicationName+ "[core-thread-"+ i++ +"]");
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
