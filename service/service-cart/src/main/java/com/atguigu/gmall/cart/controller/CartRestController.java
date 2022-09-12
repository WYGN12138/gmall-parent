package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 购物车处理前端ajax请求等
 */
@RequestMapping
@RestController
public class CartRestController {


    /**
     * 购物车列表
     */
    @GetMapping()
    public Result cartList(){
        //1. 决定用哪个购物车建

    }
}
