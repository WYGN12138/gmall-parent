package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkuIdBitMapController {

    /**
     * 同步数据库中的所有商品id占位符
     * @return
     */
    public Result syncBitMap(){
        //TODO
        //1.数据库中所有商品id查询出来
        //2.挨个占位 setbit skuids
        return Result.ok();

    }
}
