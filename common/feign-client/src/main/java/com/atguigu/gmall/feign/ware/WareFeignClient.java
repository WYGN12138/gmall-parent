package com.atguigu.gmall.feign.ware;

import io.swagger.models.auth.In;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * url: 指定请求发送的绝对路径
 */
@FeignClient(value = "ware-manage",url = "${app.ware-url:http://localhost:9001/}")
public interface WareFeignClient {

    ///hasStock?skuId=10221&num=2

    /**
     * 查询商品是否有库存
     * @param skuId
     * @param num
     * @return
     */
   @GetMapping("/hasStock")
    String hasStock(@RequestParam("skuId") Long skuId,
                    @RequestParam("num") Integer num);

}
