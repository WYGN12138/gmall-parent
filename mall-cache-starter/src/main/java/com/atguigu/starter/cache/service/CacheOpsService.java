package com.atguigu.starter.cache.service;



import java.lang.reflect.Type;

public interface CacheOpsService {
    /**
     * 从缓存中获取一个json并转换为普通对象
     * @param cacheKey
     * @param clz
     * @param <T>
     * @return
     */
    <T> T getCacheData(String cacheKey, Class<T> clz);

    /**
     * 获取带泛型的对象
     * @param cacheKey
     * @param type
     * @return
     */
    Object getCacheData(String cacheKey, Type type);

    /**
     * 延迟双删解决缓存写一直问题
     * @param cacheKey
     */
    void delay2Delete(String cacheKey);

    /**
     * 布隆过滤器判断是否存在
     * @param skuId
     * @return
     */
    boolean bloomContains(Object skuId);

    /**
     * 判定指定（bloomName）布隆过滤器是否含有指定值（bVal）
     * @param bloomName
     * @param bVal
     * @return
     */
    boolean bloomContains(String bloomName, Object bVal);


    /**
     * 给指定商品加锁
     * @param skuId
     * @return
     */
    boolean tryLock(Long skuId);

    /**
     * 加指定的锁
     * @param lockName
     * @return
     */
    boolean tryLock(String lockName);

    /**
     * 使用指定key保存查询结果至缓存
     * @param cacheKey
     * @param formRpc
     */
    void saveData(String cacheKey, Object formRpc);

    /**
     * 带自定义过期时间的保存
     * @param cacheKey
     * @param formRpc
     * @param ttl
     */
    void saveData(String cacheKey, Object formRpc,Long dataTtl);

    /**
     * 对应解锁
     * @param skuId
     */
    void unlock(Long skuId);


    /**
     * 解对应的锁
     * @param lockName
     */
    void unlock(String lockName);
}
