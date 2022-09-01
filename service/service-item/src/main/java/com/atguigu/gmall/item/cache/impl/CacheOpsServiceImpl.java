package com.atguigu.gmall.item.cache.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

@Service
public class CacheOpsServiceImpl implements CacheOpsService {


    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;
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
                return type.getClass();  //带泛型的类型
            }
        });
        return obj;
    }

    @Override
    public boolean bloomContains(Object skuId) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        return filter.contains(skuId);
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
    public void unlock(Long skuId) {
        String lockKey = SysRedisConst.SKU_DETAIL+skuId;
        RLock lock = redissonClient.getLock(lockKey);
        //解锁
        lock.unlock();
    }
}
