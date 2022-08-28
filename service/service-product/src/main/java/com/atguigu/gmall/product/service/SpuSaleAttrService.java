package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author mi
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service
* @createDate 2022-08-23 20:20:32
*/
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    /**
     * 根据spuId查询对应的所有销售属性和值
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSaleAttrAndValueBySpuId(Long spuId);

    /**
     * //1.查询对应值  2.固定顺序 3.标记所选sku定义属性
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSaleAttrAndValueMarkSku(Long spuId, Long skuId);

    /**
     * 查询所有sku组合并完成所需封装
     * @param spuId
     * @return
     */
    String getAllSkuSaleAttrValueJson(Long spuId);
}
