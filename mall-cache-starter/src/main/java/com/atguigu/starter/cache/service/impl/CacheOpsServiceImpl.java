package com.atguigu.starter.cache.service.impl;

import com.atguigu.starter.cache.constant.SysRedisConst;
import com.atguigu.starter.cache.service.CacheOpsService;
import com.atguigu.starter.cache.utils.Jsons;
import com.fasterxml.jackson.core.type.TypeReference;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class CacheOpsServiceImpl implements CacheOpsService {


    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    //专门执行延迟任务的线程池
    ScheduledExecutorService scheduledExecutor =Executors.newScheduledThreadPool(4);
    /**
     * 从缓存中获取数据，并转成指定类
     * @param cacheKey
     * @param clz
     * @param <T>
     * @return
     */
    @Override
    public <T> T getCacheData(String cacheKey, Class<T> clz) {
        //缓存中获取
        String jsonStr =  redisTemplate.opsForValue().get(cacheKey);
        //引入null值缓存机制
        if(SysRedisConst.NULL_VAL.equals(jsonStr)){ //查询到
            return null;
        }
        T t = Jsons.toObj(jsonStr, clz);
        return t;
    }

    /**
     * 逆转json生成type类型的复杂对象
     * @param cacheKey
     * @param type
     * @return
     */
    @Override
    public Object getCacheData(String cacheKey, Type type) {
        //缓存中获取
        String jsonStr =  redisTemplate.opsForValue().get(cacheKey);
        //引入null值缓存机制
        if(SysRedisConst.NULL_VAL.equals(jsonStr)){ //查询到
            return null;
        }
        Object obj = Jsons.toObj(jsonStr, new TypeReference<Object>() {
            @Override
            public Type getType() {
                return type;  //这个是方法的带泛型的返回值类型
            }
        });
        return obj;
    }

    /**
     * 延迟双删
     * @param cacheKey
     */
    @Override
    public void delay2Delete(String cacheKey) {
        redisTemplate.delete(cacheKey);
        //提交延迟任务，但是断电失效  非高并发不影响
        //TODO 分布式框架 Redisson 找redisson api
        scheduledExecutor.schedule(()->{
            redisTemplate.delete(cacheKey);
        },5,TimeUnit.SECONDS);

    }

    @Override
    public boolean bloomContains(Object skuId) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        return filter.contains(skuId);
    }

    @Override
    public boolean bloomContains(String bloomName, Object bVal) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(bloomName);
        return filter.contains(bVal);

    }

    @Override
    public boolean tryLock(Long skuId) {
        String lockKey = SysRedisConst.SKU_DETAIL+skuId;
        //skuId的唯一锁
        RLock lock = redissonClient.getLock(lockKey);
        //尝试加锁
        boolean tryLock = lock.tryLock();
        return tryLock;
    }

    /**
     * 加指定的锁
     * @param lockName
     * @return
     */
    @Override
    public boolean tryLock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        //尝试加锁
        return lock.tryLock();

    }

    @Override
    public void saveData(String cacheKey, Object formRpc) {
        if (formRpc ==null){
            //null值缓存机制(短时间)
            redisTemplate.opsForValue().set(cacheKey,
                    SysRedisConst.NULL_VAL,
                    SysRedisConst.NULL_VAL_TTL,
                    TimeUnit.SECONDS);
        }else {//正常值
            String str=Jsons.toStr(formRpc);
            redisTemplate.opsForValue().set(cacheKey,str,SysRedisConst.SKUDETAIL_TTL,TimeUnit.SECONDS);
        }
    }

    @Override
    public void saveData(String cacheKey, Object formRpc, Long dataTtl) {
        if (formRpc ==null){
            //null值缓存机制(短时间)
            redisTemplate.opsForValue().set(cacheKey,
                    SysRedisConst.NULL_VAL,
                    SysRedisConst.NULL_VAL_TTL,
                    TimeUnit.SECONDS);
        }else {//正常值
            String str=Jsons.toStr(formRpc);
            redisTemplate.opsForValue().set(cacheKey,str,SysRedisConst.SKUDETAIL_TTL,TimeUnit.SECONDS);
        }
    }

    /**
     * 解锁
     * @param skuId
     */
    @Override
    public void unlock(Long skuId) {
        String lockKey = SysRedisConst.SKU_DETAIL+skuId;
        RLock lock = redissonClient.getLock(lockKey);
        //解锁
        lock.unlock();
    }

    /**
     * 解指定锁
     * @param lockName
     */
    @Override
    public void unlock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        lock.unlock(); //redisson已经防止删别人的锁
    }
}
