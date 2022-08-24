package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 品牌列表
 */
@RestController
@RequestMapping("/admin/product")
public class BaseTrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;
    //baseTrademark/1/10
    @GetMapping("/baseTrademark/{pn}/{ps}")
    public Result getBaseTrademark(@PathVariable("pn")Long pn,
                                   @PathVariable("ps")Long ps){
        Page<BaseTrademark> pageResult =  baseTrademarkService.page(new Page<>(pn,ps));
        return Result.ok(pageResult);
    }

}
