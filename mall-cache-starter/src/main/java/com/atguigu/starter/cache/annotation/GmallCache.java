package com.atguigu.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * 缓存注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {

    String cacheKey() default ""; //指定cacheKey


    String bloomName() default "";//指定布隆过滤器名字

    String bloomValue() default ""; //指定布隆判定的表达式

    String lockName() default ""; //指定锁

    long ttl() default 60*30L;  //指定过期时间
}
