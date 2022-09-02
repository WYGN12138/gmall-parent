package com.atguigu.starter.cache;

import com.atguigu.starter.cache.aspect.CacheAspect;
import com.atguigu.starter.cache.service.CacheOpsService;
import com.atguigu.starter.cache.service.impl.CacheOpsServiceImpl;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

/**
 * 以前容器中的所有组件要导入进去
 */
@Import(CacheAspect.class)
@EnableAspectJAutoProxy //开启aspectj的自动代理功能
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class MallCacheAutoConfiguration {
    @Resource
    RedisProperties redisProperties;

    /**
     * 缓存切面
     *
     * @return
     */
    @Bean
    public CacheAspect cacheAspect() {
        return new CacheAspect();
    }

    /**
     * 缓存操作服务CacheOpsService
     *
     * @return
     */
    @Bean
    public CacheOpsService cacheOpsService() {
        return new CacheOpsServiceImpl();
    }

    /**
     * RedissonClient
     * @return
     */
    @Bean
    public RedissonClient redissonClient() {
        //1.创建一个配置
        Config config = new Config();
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        String password = redisProperties.getPassword();
        //2.指定好redisson的配置
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password);
        //3.创建一个redissonClient
        RedissonClient client = Redisson.create(config);
        return client;
    }


}
