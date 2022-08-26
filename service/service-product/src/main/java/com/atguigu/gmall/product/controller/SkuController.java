package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Sku管理
 */
@RequestMapping("/admin/product")
@RestController
public class SkuController {
    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/list/{pn}/{ps}")
    public Result getSkuList(@PathVariable("pn")Long pn,
                             @PathVariable("ps")Long ps){
        Page<SkuInfo> result = skuInfoService.page(new Page<>(pn, ps));
        return Result.ok(result);
    }
    @PostMapping("/saveSkuInfo")
    public  Result saveSku(@RequestBody SkuInfo info){
        skuInfoService.saveSkuInfo(info);
        return Result.ok();
    }

    /**
     * 商品下架
     * @param skuId
     * @return
     */
    //cancelSale/
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        skuInfoService.cancelSale(skuId);
        return  Result.ok();
    }
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        skuInfoService.onSale(skuId);
        return Result.ok();

    }

}
