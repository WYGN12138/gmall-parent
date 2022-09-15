package com.atguigu.gmall.model.vo.order;

import lombok.Data;

import java.util.List;

/**
 * 订单提交数据模型
 */
@Data
public class OrderSubmitVo {
    private String consignee; //收货人
    private String consigneeTel; //收货人电话
    private String deliveryAddress; //送货地址
    private String paymentWay;  //支付方式
    private String orderComment; //订单说明
    private List<CartInfoVo> orderDetailList; //订单明细表
}
