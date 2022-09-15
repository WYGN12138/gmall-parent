package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/auth")
public class OrderRestController {
    //http://api.gmall.com/api/order/auth/submitOrder?tradeNo=e923404c08f14864a383e0be99f57133

    @Autowired
    OrderBizService orderBizService;

    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo")String tradeNo,
                              @RequestBody OrderSubmitVo submitVo){

        Long orderId = orderBizService.submitOrder(tradeNo,submitVo);
        return Result.ok();
    }
}
