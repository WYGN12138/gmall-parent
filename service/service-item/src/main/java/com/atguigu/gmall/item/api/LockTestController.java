package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.lock.RedisDistLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.ReentrantLock;

@RequestMapping("/lock")
@RestController
public class LockTestController {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedisDistLock redisDistLock;

//    ReentrantLock lock = new ReentrantLock();

    //默认 2w -331 默认不加锁
    //加锁  2w -20000
    //分布式锁

    @GetMapping("/incr")
    public Result increment(){
//        lock.lock();
        String token = redisDistLock.lock();
        //阻塞式加锁
        //获取值
        String a = redisTemplate.opsForValue().get("a");
        int i = Integer.parseInt(a);
        //业务计算
        i++;
        redisTemplate.opsForValue().set("a",i+"");
//        lock.unlock();
        redisDistLock.unlock(token);
        return Result.ok();
    }
}
