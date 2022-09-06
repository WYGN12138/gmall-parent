package com.atguigu.gmall.search.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;

public interface GoodsService {


    /**
     * 保存
     * @param goods
     */
    void saveGoods(Goods goods);

    /**
     * 删除
     * @param skuId
     */
    void deleteGoods(Long skuId);

    //商品检索
    SearchResponseVo search(SearchParamVo paramVo);
}
