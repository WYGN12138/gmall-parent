package com.atguigu.gmall.item;

import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.RedissonAutoConfiguration;
import com.atguigu.gmall.common.config.threadpool.AppThreadPoolAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 1.RedisAutoConfiguration
 给容器中放了RedisTemplate<Object,Object>和StringRedisTemplate
 给redis存数据，都是k-v(v有很多类型)【string,jsonstring】
 StringRedisTemplate RedisTemplate<string,String>
 给redis存数据，key是string,value序列化成字符
 */

@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@SpringCloudApplication
@EnableThreadPool  //自定义线程池注解
public class ItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class,args);
    }
}
