package com.atguigu.gmall.item.cache;

import com.atguigu.gmall.model.to.SkuDetailTo;

public interface CacheOpsService {
    <T> T getCacheData(String cacheKey, Class<T> clz);

    /**
     * 布隆过滤器判断是否存在
     * @param skuId
     * @return
     */
    boolean bloomContains(Long skuId);

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
