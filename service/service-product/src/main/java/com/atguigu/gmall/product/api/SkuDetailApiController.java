package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情数据库操作
 */
@RestController
@RequestMapping("/api/inner/rpc/product")
public class SkuDetailApiController {

    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    BaseCategory3Service baseCategory3Service;

    /**
     * 数据库库层真正查询商品详情
     * @param
     * @return
     */
//    @GetMapping("/skuDetail/{skuId}")
//    public Result<SkuDetailTo> getSkuDetail(@PathVariable Long skuId){
//        //准备查询所需要的额数据
//           SkuDetailTo skuDetailTo=  skuInfoService.getSkuDetail(skuId);
//        return Result.ok(skuDetailTo);
//    }

    /**
     * 分类信息
     * @param c3Id
     * @return
     */
    @GetMapping("/skudetail/categoryview/{c3Id}")
    public Result<CategoryViewTo> getCategoryView(@PathVariable("c3Id")Long c3Id){
        CategoryViewTo categoryViewTo=   baseCategory3Service.getCategoryView(c3Id);
        return Result.ok(categoryViewTo);

    }


    /**
     * 查询sku的基本信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/info/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId) {
        SkuInfo skuInfo = skuInfoService.getDetailSkuInfo(skuId);
        return Result.ok(skuInfo);
    }

    /**
     * 查询sku图片列表
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/images/{skuId}")
    public Result<List<SkuImage>> getSkuImages(@PathVariable("skuId") Long skuId) {
        List<SkuImage> images = skuInfoService.getDetailSkuImage(skuId);
        return Result.ok(images);
    }


    /**
     * 查询sku的实时价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/price/{skuId}")
    public Result<BigDecimal> getSkuNowPrice(@PathVariable("skuId") Long skuId) {
        BigDecimal price = skuInfoService.getNowPrice(skuId);
        return Result.ok(price);
    }

    /**
     * 获取sku对应的属性值和属性名并标记所属属性值
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/skudetail/saleattrvalues/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSkuSaleAttrValues(@PathVariable("spuId") Long spuId,
                                                          @PathVariable("skuId") Long skuId) {
        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueMarkSku(spuId,skuId);
        return Result.ok(saleAttrList);
    }


    /**
     * 查询sku所有的组合属性
     * @param spuId
     * @return
     */
    @GetMapping("/skudetail/valuejson/{spuId}")
    public Result getSkuValueJson(@PathVariable("spuId")Long spuId){
        String valueJson= spuSaleAttrService.getAllSkuSaleAttrValueJson(spuId);
       return Result.ok(valueJson);
    }


}

