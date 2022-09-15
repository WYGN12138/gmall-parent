package com.atguigu.gmall.order.biz;

import com.atguigu.gmall.model.vo.order.OrderConfirmDateVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;

/**
 * 跳到
 */
public interface OrderBizService {
    /**
     * 获取订单确认页需要的数据
     * @return
     */
    OrderConfirmDateVo getConfirmData();

    /**
     * 生成交易流水号
     * 1. 追踪整个订单
     * 2. 作为防重令牌
     *
     * @return
     */
    String generateTradeNo();

    /**
     * 检查令牌
     * @param tradeNo
     * @return
     */
    boolean checkTradeNo(String tradeNo);

    /**
     * 提交订单
     * @param tradeNo
     * @param submitVo
     * @return
     */
    Long submitOrder(String tradeNo, OrderSubmitVo submitVo);

}
