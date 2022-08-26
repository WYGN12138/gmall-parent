package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author mi
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-08-23 20:20:32
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Resource
    SkuImageService skuImageService;
    @Resource
    SkuAttrValueService skuAttrValueService;
    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    SkuInfoMapper skuInfoMapper;

    @Override
    public void saveSkuInfo(SkuInfo info) {
        //保存基本信息到sku_info
        save(info);
        Long skuId = info.getId();
        //保存sku图片到sku_image
        List<SkuImage> skuImageList = info.getSkuImageList();
        for (SkuImage image : skuImageList) {
            //回填skuId
           image.setSkuId(skuId);
        }
        //批量保存
        skuImageService.saveBatch(skuImageList);

        //3.保存平台属性关系到sku_attr_value
        List<SkuAttrValue> skuAttrValueList = info.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
        }
        skuAttrValueService.saveBatch(skuAttrValueList);
        //3.保存销售属性到sku_sale_value
        List<SkuSaleAttrValue> skuSaleAttrValueList = info.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : skuSaleAttrValueList) {
            saleAttrValue.setSkuId(skuId);
            saleAttrValue.setSpuId(info.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);
    }

    @Override
    public void cancelSale(Long skuId) {
        //1.改数据库sku_info的is_sale
        skuInfoMapper.updateIsSale(skuId,0);
        //TODO 2.从es中删除
    }

    @Override
    public void onSale(Long skuId) {
        skuInfoMapper.updateIsSale(skuId,1);
        //TODO 添加到es
    }
}




