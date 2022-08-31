package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;

import org.apache.tomcat.util.security.Escape;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service   //单例实例
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;
    //本地map缓存
//    private Map<Long, SkuDetailTo> skuCache = new ConcurrentHashMap<>();
    @Autowired
    StringRedisTemplate redisTemplate;
    /**
     * 加锁之前考虑锁不锁得住
     * 公用一把锁 还是 各自有各自的锁
     * 锁精确  越精确越好
     * JUC的所有锁都是本地锁 锁不住分布式微服务  所以使用----分布式锁
     */
//    ReentrantLock lock = new ReentrantLock();
    Map<Long, ReentrantLock> lockPool = new HashMap<>();
    /**
     * 可配置的自定义线程池
     */
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    CacheOpsService cacheOpsService;


    public SkuDetailTo getSkuDetailFormRpc(Long skuId) {
        SkuDetailTo detailTo = new SkuDetailTo();
//        //远程调用商品服务查询
//        Result<SkuDetailTo> skuDetail = skuDetailFeignClient.getSkuDetail(skuId);
//
//        return skuDetail.getData();

        //1.查基本信息
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> result = skuDetailFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfo = result.getData();
            detailTo.setSkuInfo(skuInfo);
            return skuInfo;
        }, executor);
        //2. 查询商品图片信息
        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!=null){
            Result<List<SkuImage>> skuImages = skuDetailFeignClient.getSkuImages(skuId);skuInfo.setSkuImageList(skuImages.getData());

            }
        }, executor);

        //3.查询实时价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> price = skuDetailFeignClient.getSkuNowPrice(skuId);
            detailTo.setPrice(price.getData());
        }, executor);
        //4.查销售属性名值
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!= null){
            Long spuId = skuInfo.getSpuId();
            Result<List<SpuSaleAttr>> saleattrvalues = skuDetailFeignClient.getSkuSaleAttrValues(skuId, spuId);
            detailTo.setSpuSaleAttrList(saleattrvalues.getData());
            }
        }, executor);
        //5.查sku组合
        CompletableFuture<Void> skuValueFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null){
            Long spuId = skuInfo.getSpuId();
            Result<String> skuValueJson = skuDetailFeignClient.getSkuValueJson(spuId);
            detailTo.setValueSkuJson(skuValueJson.getData());
            }
        }, executor);
        //商品详情
//        SkuDetailTo skuDetailTo= detailService.getSkuDetail(skuId);
        //6.查分类
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null){
            Result<CategoryViewTo> categoryView = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id());
            detailTo.setCategoryView(categoryView.getData());
            }
        }, executor);

        CompletableFuture
                .allOf(imageFuture, priceFuture, saleAttrFuture, skuValueFuture, categoryFuture)
                .join();
        return detailTo;
    }


/**
 * 加锁问题及解决
 */
//    void redisLock(){
//        //问题1： 机器宕机，其他服务永远得不到锁-----引入过期时间
//        //问题2： 加锁和设置过期时间必须同时完成------set lock 1 nx ex 60
//        //问题3： 解锁命令满 另一个刚上锁就被解开 -----用随机数当锁值 定义唯一锁 只解自己的锁
//        //问题4： 判断和删除不同步导致中途发生意外误删新加的锁
//        //最终版： 使用lua脚本将获取锁值 判断和 删除 整合成一步  保证原子性  （要保证锁的自动解锁时间大于业务完成时间）-----自动续期
//
//
//    }

    /**
     * 使用分布式缓存
     *
     * @param skuId
     * @return
     */

    public SkuDetailTo getSkuDetailxxxxxxx(Long skuId) {
        //1.根据skuId查询
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        if ("x".equals(jsonStr)) {
            //说明以前查过只不过没有记录为了避免回源 缓存了一个占位符
            return null;
        }
        if (StringUtils.isEmpty(jsonStr)) {//2.如果没有命中
            //2.1 未命中 回源查找 之前判断redis中保存的skuid集合 有没有这个id
            //TODO 防止随机穿透？？？
            SkuDetailTo fromRpc = null;
            //synchronized () 没法编写没抢到锁的业务逻辑
            //ReentrantLock  同上 调lock才加锁
            //判断锁池中有没有锁没有了再添加
            lockPool.putIfAbsent(skuId, new ReentrantLock()); //如果没有就添加
            //取出对应id的锁
            ReentrantLock skuIdLock = lockPool.get(skuId);
            boolean b = skuIdLock.tryLock();
            if (b) {//抢到锁
                fromRpc = getSkuDetailFormRpc(skuId);
            } else {//没抢到 睡一觉
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            SkuDetailTo formRpc = getSkuDetailFormRpc(skuId);
            //2.2 放入缓存 (转为json)
            String cacheJson = "x";
            if (formRpc != null) {
                cacheJson = Jsons.toStr(formRpc);
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 7, TimeUnit.DAYS);
            } else {
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 30, TimeUnit.MINUTES);

            }
            return formRpc;
        }
        //3. 缓存命中 把json转未指定的对象
        SkuDetailTo skuDetailTo = Jsons.toObj(jsonStr, SkuDetailTo.class);
        return skuDetailTo;
    }

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        String cacheKey = SysRedisConst.SKU_INFO_CACHE_KEY + skuId;
        //1.先查缓存中是否可以命中
        SkuDetailTo cacheData = cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
        //2.判断
        if (cacheData == null) {//未命中
            //查询布隆过滤器
            boolean contain = cacheOpsService.bloomContains(skuId);
            if (!contain) {
                //没有返回null
                return null;
            }
            //有，加锁
            boolean lock = cacheOpsService.tryLock(skuId); //为skuId加唯一锁
            if (lock) {//获取锁成功,
                //远程查询
                SkuDetailTo formRpc = getSkuDetailFormRpc(skuId);
                //保存数据库
                cacheOpsService.saveData(cacheKey, formRpc);
                //解锁
                cacheOpsService.unlock(skuId);
                //返回数据
                return formRpc;
            }
            //没获取到锁
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
            }
        }
        //命中直接返回
        return cacheData;
    }
/**
 * 使用本地缓存
 */
//    @Override
//    public SkuDetailTo getSkuDetail(Long skuId) {
//        //先从缓存中拿
//        SkuDetailTo cacheData = skuCache.get(skuId);
//        //2.判断
//        if (cacheData == null) {
//            //3.未命中 回源
//            //3、缓存没有，真正查询【回源（回到数据源头真正检索）】【提高缓存的命中率】
//            //1-0/1:0%
//            //2-1/2:50%
//            //N-(N-1)/N:无限接近100%
//            //缓存命中率提升到100%；预缓存机制；
//            SkuDetailTo formRpc = getSkuDetailFormRpc(skuId);
//            skuCache.put(skuId,formRpc);
//            return formRpc;
//        }
//        //4.缓存有
//
//
//        return cacheData;
//    }
}


