package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    SpuImageService spuImageService;


    //admin/product/1/10?category3Id=2

    /**
     * 分页获取spu
     * @PathVariable 路径参数
     * @RequestParam  请求参数 （请求体中莫格数据）
     * @RequestBody  请求参数（请求体钟所有的数据）
     *   无论是？以后的数据 还是请求体数据都叫请求参数
     * /admin/product/1/10?category3Id=2
     * ？以前的是请求路径：@PathVariable
     * ？以后是请求参数：@RequestParam
     * 如果是post ，请求参数既可以返稿？之后，也可以放请求体
     *      -@RequestParam ？以后和请求体都能取
     *      如果是get 求情参数只能放到url？后面
     *       -@RequestParam ？以后和请求体都能取
     * 发一个亲求：
     * 请求首行：  \n GET http://xxxx?dadad=dad
     * 请求头：    \n  Content-Type:xxx,xxx  内容类型
     * 请求体   \n  任意数据
     * 负载：  请求参数
     *
     *
     * @return
     */
    @GetMapping("/{pn}/{ps}")
    public Result getSpuPage(@PathVariable("pn")Long pn,
                             @PathVariable("ps")Long ps,
                             @RequestParam("category3Id")Long category3Id){

        Page<SpuInfo> page = spuInfoService.page(
                new Page<SpuInfo>(pn, ps),
                new LambdaQueryWrapper<SpuInfo>()
                        .eq(SpuInfo::getCategory3Id, category3Id));

        return Result.ok(page);
    }

    //SPU定义这种商品所有的销售属性
    //sku是spu中的一个“实例”
   ///admin/product/saveSpuInfo
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo info){
         //spu_info 大保存，spu_info,spu_image,spu_sale_attr,spu_sale_attr_value
        spuInfoService.saveSpuInfo(info);
        return Result.ok();
    }

    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId")Long spuId){

        List<SpuImage> list = spuImageService.list(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId,spuId));
        return Result.ok(list);
    }




}
