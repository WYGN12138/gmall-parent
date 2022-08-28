package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类有关api
 * 所有远程都是内部接口
 */
@RestController
@RequestMapping("/api/inner/rpc/product")
public class CategoryApiController {


    @Autowired
    BaseCategory2Service baseCategory2Service;

    /**
     * 查询所有分类封装成树形结构
     * @return
     */
    @GetMapping("/category/tree")
    public Result getAllCategoryWithTree(){
        //javaBean
       List<CategoryTreeTo> categoryTreeTos= baseCategory2Service.getAllCategoryWithTree();

        return  Result.ok(categoryTreeTos);
    }
}
