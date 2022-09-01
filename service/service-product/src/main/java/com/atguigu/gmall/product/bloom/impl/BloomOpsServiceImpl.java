package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloomOpsServiceImpl implements BloomOpsService {
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    SkuInfoService skuInfoService;
    @Override
    public void rebuildBloom(String bloomName, BloomDataQueryService dataQueryService) {
        //1.得到旧布隆
        RBloomFilter<Object> oldbloomFiler = redissonClient.getBloomFilter(bloomName);
        //2.先准备新的布隆过滤器，所有东西都初始化好
        String newBloomName =  bloomName+"_new"; //新名字
        //2.1 新建布隆过滤器
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(newBloomName);
        //2.2 获取所有指定的集合
//        List<Long> skuIds = skuInfoService.findAllSkuId();
        List list = dataQueryService.queryData();
        //2.3 初始化布隆过滤器
        bloomFilter.tryInit(5000000,0.00001);
        //2.4 遍历所有skuId并将添加到布隆
        for (Object obj : list) {
            bloomFilter.add(obj);
        }
        //3.新建布隆准备完毕
        //冒泡交换  ob：老布隆 bb：中间 nb：新布隆
        //4.两个交换  nb换成ob  大数据量删除会导致redis卡死
        //为了原子性保证：最好使用lua脚本
        //4.1 老布隆改名为中间布隆
        oldbloomFiler.rename("bb_Filer");
        //4.2 新布隆改为老布隆
        bloomFilter.rename(bloomName);
        //4.3删除老布隆
        oldbloomFiler.deleteAsync();
        //4.4删除中间布隆
        redissonClient.getBloomFilter("bb_Filer").deleteAsync();




    }
}
