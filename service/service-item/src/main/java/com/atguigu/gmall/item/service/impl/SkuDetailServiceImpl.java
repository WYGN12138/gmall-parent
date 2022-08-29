package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    //本地map缓存
//    private Map<Long, SkuDetailTo> skuCache = new ConcurrentHashMap<>();
    @Autowired
    StringRedisTemplate redisTemplate;


    /**
     * 可配置的自定义线程池
     */
    @Autowired
    ThreadPoolExecutor executor;


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
            Result<List<SkuImage>> skuImages = skuDetailFeignClient.getSkuImages(skuId);
            skuInfo.setSkuImageList(skuImages.getData());
        }, executor);

        //3.查询实时价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> price = skuDetailFeignClient.getSkuNowPrice(skuId);
            detailTo.setPrice(price.getData());
        }, executor);
        //4.查销售属性名值
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Long spuId = skuInfo.getSpuId();
            Result<List<SpuSaleAttr>> saleattrvalues = skuDetailFeignClient.getSkuSaleAttrValues(skuId, spuId);
            detailTo.setSpuSaleAttrList(saleattrvalues.getData());
        }, executor);
        //5.查sku组合
        CompletableFuture<Void> skuValueFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Long spuId = skuInfo.getSpuId();
            Result<String> skuValueJson = skuDetailFeignClient.getSkuValueJson(spuId);
            detailTo.setValueSkuJson(skuValueJson.getData());
        }, executor);
        //商品详情
//        SkuDetailTo skuDetailTo= detailService.getSkuDetail(skuId);
        //6.查分类
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<CategoryViewTo> categoryView = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id());
            detailTo.setCategoryView(categoryView.getData());
        }, executor);

        CompletableFuture
                .allOf(imageFuture, priceFuture, saleAttrFuture, skuValueFuture, categoryFuture)
                .join();
        return detailTo;
    }

    /**
     * 使用分布式缓存
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        //1.根据skuId查询
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        if ("x".equals(jsonStr)){
            //说明以前查过只不过没有记录为了避免回源 缓存了一个占位符
            return null;
        }
        if(StringUtils.isEmpty(jsonStr)){//2.如果没有命中
            //2.1 未命中 回源查找 之前判断redis中保存的skuid集合 有没有这个id
            //TODO 防止随机穿透？？？
            SkuDetailTo formRpc = getSkuDetailFormRpc(skuId);
            //2.2 放入缓存 (转为json)
            String cacheJson = "x";
            if (formRpc!=null){
               cacheJson = Jsons.toStr(formRpc);
            redisTemplate.opsForValue().set("sku:info:"+skuId, cacheJson,7, TimeUnit.DAYS);
            }else {
                redisTemplate.opsForValue().set("sku:info:"+skuId, cacheJson,30,TimeUnit.MINUTES);

            }
            return formRpc;
        }
        //3. 缓存命中 把json转未指定的对象
        SkuDetailTo skuDetailTo = Jsons.toObj(jsonStr,SkuDetailTo.class);
        return skuDetailTo;
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


