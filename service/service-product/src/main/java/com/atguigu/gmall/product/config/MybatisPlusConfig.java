package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration //告诉容器我是个配置类
@EnableTransactionManagement //开启基于注解的事务
public class MybatisPlusConfig {
    //1.把Mybatis的插件主体（总插件）放到容器里
    @Bean
    public MybatisPlusInterceptor interceptor(){
        //插件主题
   MybatisPlusInterceptor interceptor= new MybatisPlusInterceptor();
        //加入内部小插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOverflow(true); //页码溢出默认最后一页
        //加入分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }




}
