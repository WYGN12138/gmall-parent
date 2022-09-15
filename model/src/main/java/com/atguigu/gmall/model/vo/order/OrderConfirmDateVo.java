package com.atguigu.gmall.model.vo.order;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.user.UserAddress;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页数据
 */
@Data
public class OrderConfirmDateVo {
    //购物车所有需要结算单的商品
    private List<CartInfoVo> detailArrayList;
    private Integer totalNum;
    private BigDecimal totalAmount;
    //用户的shou
    private List<UserAddress> userAddressList;
    //交易订单号
    private String tradeNo;

}
