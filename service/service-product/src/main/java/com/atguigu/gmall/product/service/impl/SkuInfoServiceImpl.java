package com.atguigu.gmall.product.service.impl;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.list.SearchAttr;
import com.google.common.collect.Lists;
import java.util.Date;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.mapper.SkuInfoMapper;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    @Resource
    BaseCategory3Mapper baseCategory3Mapper;

    @Resource
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @Autowired
    SearchFeignClient searchFeignClient;

    /**
     * 保存sku信息
     * @param info
     */
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
        //把这个skuId保存到布隆过滤器中
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        filter.add(skuId);
    }

    /**
     * 下架
     * @param skuId
     */
    @Override
    public void cancelSale(Long skuId) {
        //1.改数据库sku_info的is_sale
        skuInfoMapper.updateIsSale(skuId,0);
        //TODO 2.从es中删除
        searchFeignClient.deleteGoods(skuId);

    }

    /**
     * 上架
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
        skuInfoMapper.updateIsSale(skuId,1);
        //TODO 添加到es
        Goods goods =getGoodsBySkuId(skuId);
        searchFeignClient.saveGoods(goods);
    }

    /**
     * 获取详情页信息
     * @param skuId
     * @return
     */
    @Deprecated
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();
        // 0.查询skuInfo获c3_id
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        // 1.商品（sku）所属的完整分类信息：
        CategoryViewTo categoryViewTo= baseCategory3Mapper.getCategoryView(skuInfo.getCategory3Id());
        skuDetailTo.setCategoryView(categoryViewTo);
        //实时间隔查询
        BigDecimal price = getNowPrice(skuId);
        skuDetailTo.setPrice(price);
        // 2，sku基本信息  sku_info
        skuDetailTo.setSkuInfo(skuInfo);
        // 3.sku图片    sku_image
        List<SkuImage> imageList= skuImageService.getSkuImage(skuId);
        skuInfo.setSkuImageList(imageList);
        //4.sku所属的所有销售属性，标识当前sku组合   spu_sale_attr,spu_sale_attr_value
        //1.查询对应值  2.固定顺序 3.标记所选sku定义属性
        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueMarkSku(skuInfo.getSpuId(),skuId);
        skuDetailTo.setSpuSaleAttrList(saleAttrList);
        //商品sku的所有兄弟产品的销售属性和值组合关系全部查出来，封装
        //{"119|120":"50","118|121":"51",}
        Long spuId = skuInfo.getSpuId();
         String valueJson= spuSaleAttrService.getAllSkuSaleAttrValueJson(spuId);
         skuDetailTo.setValueSkuJson(valueJson);

        //以下不用管
        //5.商品类似介绍(sku)
        //6.sku介绍（所属的spu海报）    spu_poster不用管
        //7.sku规格参数   sku_attr_value
        //8.sku售后
        return skuDetailTo;
    }

    /**
     * 获得实时价格
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getNowPrice(Long skuId) {
        BigDecimal price=  skuInfoMapper.getNowPrice(skuId);
        return price;
    }

    /**
     * 获取skuInfo信息
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getDetailSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    /**
     * 获取sku的图片信息
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getDetailSkuImage(Long skuId) {
        List<SkuImage> imageList = skuImageService.getSkuImage(skuId);
        return imageList;
    }

    /**
     * 查询所有的skuId
     * @return
     */
    @Override
    public List<Long> findAllSkuId() {
        //数据量大可以分页查
        List<Long> skuIds= skuInfoMapper.getAllSkuId();
        return skuIds;
    }

    /**
     * 获得sku对应的所有信息
     * @param skuId
     * @return
     */
    @Override
    public Goods getGoodsBySkuId(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        Goods goods= new Goods();
        goods.setId(skuId);
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());
        goods.setTmId(skuInfo.getTmId());
        //品牌信息
        BaseTrademark trademark = baseTrademarkService.getById(skuInfo.getTmId());
        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());

        Long category3Id = skuInfo.getCategory3Id();
        CategoryViewTo view = baseCategory3Mapper.getCategoryView(category3Id);
        goods.setCategory1Id(view.getCategory1Id());
        goods.setCategory1Name(view.getCategory1Name());
        goods.setCategory2Id(view.getCategory2Id());
        goods.setCategory2Name(view.getCategory2Name());
        goods.setCategory3Id(view.getCategory3Id());
        goods.setCategory3Name(view.getCategory3Name());
        //热度分
        goods.setHotScore(0L); //靠es
        //查询当前sku的所有平台属性名和值
        List<SearchAttr> attrs= skuAttrValueService.getSkuAttrNameAndValue(skuId);

        goods.setAttrs(attrs);


        return goods;
    }


}




