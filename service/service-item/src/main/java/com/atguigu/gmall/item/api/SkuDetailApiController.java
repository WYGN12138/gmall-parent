package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;

import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;

import com.atguigu.gmall.model.to.SkuDetailTo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api("商品详情页")
@RestController
@RequestMapping("/api/inner/rpc/item")
public class SkuDetailApiController {

    @Autowired
    SkuDetailService detailService;



    /**
     *查询商品详情  带价钱的就是sku
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId) {
        //商品的详情
        SkuDetailTo skuDetailTo = detailService.getSkuDetail(skuId);
        //更新热度分 跟新太平凡所有没100次跟新一次
        detailService.updateScore(skuId);

        return Result.ok(skuDetailTo);
    }
}
