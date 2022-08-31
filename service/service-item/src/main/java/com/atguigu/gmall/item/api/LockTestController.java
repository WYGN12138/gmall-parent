package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.lock.RedisDistLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@RequestMapping("/lock")
@RestController
public class LockTestController {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedisDistLock redisDistLock;

//    ReentrantLock lock = new ReentrantLock();

    @Autowired
    RedissonClient redissonClient;



    @GetMapping("/common")
    public Result redissonLock() throws InterruptedException {
        //名字相同就是同意把锁
        //有自动续期机制 默认30s过去  低于20秒自定续期
        //加锁+过期时间  判断和删除
        //1.得到锁
        RLock lock = redissonClient.getLock("lock-he");//获取锁 可重入
        //2.加锁
        //2.1 lock
        lock.lock();//阻塞式加锁,非要等到，默认30s过期
        lock.lock(10, TimeUnit.SECONDS);//10s 过期不候 最多等十秒
        //2.2 trylock
        boolean b = lock.tryLock();//立即枪锁，抢不到不等
        boolean b1 = lock.tryLock(10, TimeUnit.SECONDS);//等10s过期不候
        boolean b2 = lock.tryLock(10, 20, TimeUnit.SECONDS);//等10s，等到20s释放，等不到false
        System.out.println("得到锁");
        //3.执行业务
        Thread.sleep(50000);
        System.out.println("执行结束");
        //3.解锁
        lock.unlock();

        return Result.ok();

    }

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
