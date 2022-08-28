package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;


    /**
     * 可配置的自定义线程池
     */
    @Autowired
    ThreadPoolExecutor executor;


    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo detailTo = new SkuDetailTo();
//        //远程调用商品服务查询
//        Result<SkuDetailTo> skuDetail = skuDetailFeignClient.getSkuDetail(skuId);
//
//        return skuDetail.getData();

            //1.查基本信息
            Result<SkuInfo> result = skuDetailFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfo = result.getData();
            detailTo.setSkuInfo(skuInfo);
            //2. 查询商品图片信息
            Result<List<SkuImage>> skuImages = skuDetailFeignClient.getSkuImages(skuId);
            skuInfo.setSkuImageList(skuImages.getData());

            //3.查询实时价格
            Result<BigDecimal> skuNowPrice = skuDetailFeignClient.getSkuNowPrice(skuId);
            detailTo.setPrice(skuNowPrice.getData());
            //4.查销售属性名值
            Result<List<SpuSaleAttr>> skuSaleAttrValues = skuDetailFeignClient.getSkuSaleAttrValues(skuId, skuInfo.getSpuId());
            detailTo.setSpuSaleAttrList(skuSaleAttrValues.getData());
            //5.查sku组合
            Result<String> skuValueJson = skuDetailFeignClient.getSkuValueJson(skuInfo.getSpuId());
            detailTo.setValueSkuJson(skuValueJson.getData());
            //商品详情
//        SkuDetailTo skuDetailTo= detailService.getSkuDetail(skuId);
            Result<CategoryViewTo> viewTo = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id());
            //6.查分类
            CategoryViewTo categoryViewTo = viewTo.getData();
            detailTo.setCategoryView(categoryViewTo);
            return detailTo;
        }
    }


