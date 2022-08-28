package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.ValueSkuJsonTo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author mi
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2022-08-23 20:20:32
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
    implements SpuSaleAttrService{

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    /**
     * 根据spuId查询属性名和属性值
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSaleAttrAndValueBySpuId(Long spuId) {
       List<SpuSaleAttr> list= spuSaleAttrMapper.getSaleAttrAndValueBySpuId(spuId);

       return list;


    }

    @Override
    public List<SpuSaleAttr> getSaleAttrAndValueMarkSku(Long spuId, Long skuId) {
        return   spuSaleAttrMapper.getSaleAttrAndValueMarkSku(spuId,skuId);

    }

    /**
     * 查询所有值组合
     * @param spuId
     * @return
     */
    @Override
    public String getAllSkuSaleAttrValueJson(Long spuId) {
       List<ValueSkuJsonTo> valueSkuJsonTos =  spuSaleAttrMapper.getAllSkuSaleAttrValueJson(spuId);
        //{"118|121":"50","119|120","51"}
        Map<String,Long> map = new HashMap<>();
        for (ValueSkuJsonTo valueSkuJsonTo : valueSkuJsonTos) {
            String valueJson = valueSkuJsonTo.getValueJson();  //118|121
            Long skuId = valueSkuJsonTo.getSkuId(); //50
            map.put(valueJson,skuId);
        }
        String json =  Jsons.toStr(map);
        return json;
    }
}




