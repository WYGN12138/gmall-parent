package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.mapper.SpuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author mi
 * @description 针对表【spu_info(商品表)】的数据库操作Service实现
 * @createDate 2022-08-23 20:20:32
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
        implements SpuInfoService {

    @Autowired
    SpuInfoMapper spuInfoMapper;
    @Autowired
    SpuImageService spuImageService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    SpuSaleAttrValueService spuSaleAttrValueService;

    @Transactional //开启事务
    @Override
    public void saveSpuInfo(SpuInfo info) {
        //1.保存基本信息到spu_info表中
        spuInfoMapper.insert(info);
        Long spuId = info.getId();
        //2.保存图片信息
        List<SpuImage> imageList = info.getSpuImageList();
        for (SpuImage spuImage : imageList) {
            //回调spuid
            spuImage.setSpuId(spuId);
        }
        //批量保存图片
        spuImageService.saveBatch(imageList);
        //3.保存销售属性名
        List<SpuSaleAttr> attrNameList = info.getSpuSaleAttrList();
        for (SpuSaleAttr attr : attrNameList) {
//            回填spuid
            attr.setSpuId(spuId);
            //拿到这个属性名对应的属性值集合
            List<SpuSaleAttrValue> valueList = attr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue value : valueList) {
                //回填spuId
                value.setSpuId(spuId);
                String saleAttrName = attr.getSaleAttrName();
                //回填销售属性名
                value.setSaleAttrName(saleAttrName);
            }
                //保存销售属性值
            spuSaleAttrValueService.saveBatch(valueList);
        }
        //批量保存
        spuSaleAttrService.saveBatch(attrNameList);

        //保存属性值
    }
}




