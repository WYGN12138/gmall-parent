package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/inner/rpc/search")
@FeignClient("service-search")
public interface SearchFeignClient {

    @PostMapping("/goods")
    Result saveGoods(@RequestBody Goods goods);

    @DeleteMapping("/goods/{skuId}")
     Result deleteGoods(@PathVariable("skuId")Long skuId);

    /**
     * 商品检索
     * @param paramVo
     * @return
     */
    @PostMapping("/goods/search")
    Result<SearchResponseVo> search(@RequestBody SearchParamVo paramVo);

    /**
     * 跟新热度分
     * @return
     */
    @GetMapping("/goods/hotScore/{skuId}")
    Result updateHotScore(@PathVariable("skuId")Long skuId,
                                 @RequestParam("score")Long score);
}
