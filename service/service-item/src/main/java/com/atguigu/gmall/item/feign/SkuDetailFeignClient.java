package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;

import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("service-product")
@RequestMapping("/api/inner/rpc/product")
public interface SkuDetailFeignClient {
//    @GetMapping("/skuDetail/{skuId}")
//    Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId);
    /**
     * 分类信息
     * @param c3Id
     * @return
     */
    @GetMapping("/skudetail/categoryview/{c3Id}")
     Result<CategoryViewTo> getCategoryView(@PathVariable("c3Id")Long c3Id);

    /**
     * 查询sku的基本信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/info/{skuId}")
   Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 查询sku图片列表
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/images/{skuId}")
     Result<List<SkuImage>> getSkuImages(@PathVariable("skuId") Long skuId);

    /**
     * 查询sku的实时价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/price/{skuId}")
     Result<BigDecimal> getSkuNowPrice(@PathVariable("skuId") Long skuId);

    /**
     * 获取sku对应的属性值和属性名并标记所属属性值
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/skudetail/saleattrvalues/{skuId}/{spuId}")
    Result<List<SpuSaleAttr>> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId,
                                                          @PathVariable("spuId") Long spuId);


    /**
     * 查询sku所有的组合属性
     * @param spuId
     * @return
     */
    @GetMapping("/skudetail/valuejson/{spuId}")
     Result<String> getSkuValueJson(@PathVariable("spuId")Long spuId);



}
