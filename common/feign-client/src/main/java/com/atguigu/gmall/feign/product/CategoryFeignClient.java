package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product") //告诉springboot远程调用客户端
//远程调用之前fegin会自己找nacos要到service-product的
public interface CategoryFeignClient {

    //
    @GetMapping("/category/tree")
   Result<List<CategoryTreeTo>> getAllCategoryWithTree();

}
