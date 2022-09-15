package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.vo.order.OrderConfirmDateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 订单
 */
@Controller
public class OrderController {
    @Autowired
    OrderFeignClient orderFeignClient;

    //http://order.gmall.com/trade.html

    /**
     * 跳转结算页面
     * @return
     */
    @GetMapping("/trade.html")
    public String tradePage(Model model){

        // 远程调用会透传用户信息
        Result<OrderConfirmDateVo> orderConfirmDate = orderFeignClient.getOrderConfirmDate();

        if (orderConfirmDate.isOk()){
            OrderConfirmDateVo data = orderConfirmDate.getData();
            //imgUrl、skuName、orderPrice、skuNum  item中的数据
        model.addAttribute("detailArrayList",data.getDetailArrayList());
        //总数
        model.addAttribute("totalNum",data.getTotalNum());
        //总金额
        model.addAttribute("totalAmount",data.getTotalAmount());
        //用户地址列表
        model.addAttribute("userAddressList",data.getUserAddressList());
        //订单追踪号
        model.addAttribute("tradeNo",data.getTradeNo());

        }
        return "/order/trade";
    }


}
