package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailTo;

public interface SkuDetailService {
    SkuDetailTo getSkuDetail(Long skuId);

    /**
     * 跟新热度分
     * @param skuId
     */
    void updateScore(Long skuId);
}
