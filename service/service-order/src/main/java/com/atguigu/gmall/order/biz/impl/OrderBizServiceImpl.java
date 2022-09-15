package com.atguigu.gmall.order.biz.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.CartInfoVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.google.common.collect.Lists;

import com.atguigu.gmall.model.vo.order.OrderConfirmDateVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.websocket.server.ServerEndpoint;

@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    UserFeignClient userFeignClient;
    @Autowired
    SkuProductFeignClient skuProductFeignClient;
    @Autowired
    WareFeignClient wareFeignClient;
    @Autowired
    OrderBizService orderBizService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    OrderInfoService orderInfoService;





    /**
     * 获取订单确认页需要的数据
     *
     * @return
     */
    @Override
    public OrderConfirmDateVo getConfirmData() {
        OrderConfirmDateVo vo = new OrderConfirmDateVo();
        //1.获取购物车中选中的所有商品
        // 是购物车服务返回的
        //购物车中商品只代表在redis中储存的数据 并不代表最新
        List<CartInfo> data = cartFeignClient.getChecked().getData();
        List<CartInfoVo> infoVos = data.stream().map(cartInfo -> {
            CartInfoVo infoVo = new CartInfoVo();
            infoVo.setSkuId(cartInfo.getSkuId());
            infoVo.setImgUrl(cartInfo.getImgUrl());
            infoVo.setSkuName(cartInfo.getSkuName());
            //凡是用到必须再查最新 但是只要锁单就不能再查
            //TODO 实时查询价格逻辑
            Result<BigDecimal> price = skuProductFeignClient.getSkuNowPrice(cartInfo.getSkuId());
            infoVo.setOrderPrice(price.getData());
            infoVo.setSkuNum(cartInfo.getSkuNum());
//            infoVo.setHasStock("");
            //查询商品库存
            String hasStock = wareFeignClient.hasStock(cartInfo.getSkuId(), cartInfo.getSkuNum());
            infoVo.setHasStock(hasStock);
            return infoVo;

        }).collect(Collectors.toList());
        vo.setDetailArrayList(infoVos);
        //2.统计商品的总数量
        Integer totalNum = infoVos.stream()
                .map(CartInfoVo::getSkuNum)
                .reduce((o1, o2) -> o1 + o2).get();
        vo.setTotalNum(totalNum);
        //3.统计总金额
        BigDecimal totalAmount = infoVos.stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum() + "")))
                .reduce((o1, o2) -> o1.add(o2)).get();
        vo.setTotalAmount(totalAmount);
        //4.获取用户收获地址列表
        Result<List<UserAddress>> addressList = userFeignClient.getUserAddressList();
        vo.setUserAddressList(addressList.getData());
        //5.生成一个追踪号
        //5.1 订单的唯一追踪号，对外交易号（和第三方交互）
        //5.2 用来防止重复提交 做防重令牌
        String tradeNo = generateTradeNo();
        //前端传一份
        vo.setTradeNo(tradeNo);
        return vo;

    }

    @Override
    public String generateTradeNo() {
        //一个用户同一毫秒只能下一单
        long millis = System.currentTimeMillis();
        UserAuthInfo info = AuthUtils.getCurrentAuthInfo();
        String tradeNo = millis + "_" + info.getUserId();
        //令牌redis存一份
        redisTemplate.opsForValue()
                .set(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo, "1", 15, TimeUnit.MINUTES);
        return tradeNo;
    }

    @Override
    public boolean checkTradeNo(String tradeNo) {
        //1、先看有没有，如果有就是正确令牌, 1, 0 。脚本校验令牌
        String lua = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end";

        /**
         * RedisScript<T> script,
         * List<K> keys, Object... args
         */
        Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(lua, Long.class),
                Arrays.asList(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo),
                new String[]{"1"});

        if(execute > 0){
            //令牌正确，并且已经删除
            return true;
        }
//        String val = redisTemplate.opsForValue().get(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo);
//        if(!StringUtils.isEmpty(val)){
//            //redis有这个令牌。校验成功
//            redisTemplate.delete(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo);
//            return true;
//        }

        return false;
    }

    /**
     * 提交订单
     * @param tradeNo
     * @param submitVo
     * @return
     */
    @Override
    public Long submitOrder(String tradeNo, OrderSubmitVo submitVo) {
        //1、验令牌
        boolean checkTradeNo = checkTradeNo(tradeNo);
        if(!checkTradeNo){
            throw new GmallException(ResultCodeEnum.TOKEN_INVAILD);
        }

        //2、验库存
        List<String> noStockSkus = new ArrayList<>();
        for (CartInfoVo infoVo : submitVo.getOrderDetailList()) {
            Long skuId = infoVo.getSkuId();
            String stock = wareFeignClient.hasStock(skuId, infoVo.getSkuNum());
            if(!"1".equals(stock)){
                noStockSkus.add(infoVo.getSkuName());
            }
        }

        if(noStockSkus.size() > 0){
            GmallException exception = new GmallException(ResultCodeEnum.ORDER_NO_STOCK);
            String skuNames = noStockSkus.stream()
                    .reduce((s1, s2) -> s1 + " " + s2)
                    .get();

            throw  new GmallException(
                    ResultCodeEnum.ORDER_NO_STOCK.getMessage() + skuNames,
                    ResultCodeEnum.ORDER_NO_STOCK.getCode());
        }

        //3、验价格
        List<String> skuNames = new ArrayList<>();
        for (CartInfoVo infoVo : submitVo.getOrderDetailList()) {
            //1.00
            //1.0000001
            Result<BigDecimal> price = skuProductFeignClient.getSkuNowPrice(infoVo.getSkuId());
//            BigDecimal decimal = price.getData().subtract(infoVo.getOrderPrice());
//            if( decimal.doubleValue() < 0.0001){
//                //认为对等
//            }
            if(!price.getData().equals(infoVo.getOrderPrice())){
                skuNames.add(infoVo.getSkuName());
            }
        }
        if(skuNames.size() > 0){
            String skuName = skuNames.stream()
                    .reduce((s1, s2) -> s1 + " " + s2)
                    .get();
            //有价格发生变化的商品
            throw  new GmallException(
                    ResultCodeEnum.ORDER_PRICE_CHANGED.getMessage() + "<br/>" +skuName,
                    ResultCodeEnum.ORDER_PRICE_CHANGED.getCode());
        }
        //4.保存数据库
        Long orderId = orderInfoService.saveOrder(submitVo,tradeNo);

        //5.删除购物车
        cartFeignClient.deleteChecked();


        return orderId;
    }


}
