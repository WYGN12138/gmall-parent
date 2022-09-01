package com.atguigu.gmall.item.cache.annotation;

import java.lang.annotation.*;

/**
 * 缓存注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {

    String cacheKey() default ""; //指定cacheKey



}
