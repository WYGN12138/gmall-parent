package com.atguigu.gmall.item.cache;

import com.atguigu.gmall.model.to.SkuDetailTo;

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
     * 布隆过滤器判断是否存在
     * @param skuId
     * @return
     */
    boolean bloomContains(Object skuId);

    /**
     * 给指定商品加锁
     * @param skuId
     * @return
     */
    boolean tryLock(Long skuId);

    /**
     * 使用指定key保存查询结果至缓存
     * @param cacheKey
     * @param formRpc
     */
    void saveData(String cacheKey, Object formRpc);

    /**
     * 对应解锁
     * @param skuId
     */
    void unlock(Long skuId);
}
