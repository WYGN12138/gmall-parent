package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;
    //admin/product/attrInfoList/2/13/61
   /**
       根据分类查询对应属性
    * date 2022/8/23
    * @since 1.8
    * @author mi
    * 
    */
    @GetMapping("/attrInfoList/{c1Id}/{c2Id}/{c3Id}")
    public Result getAttrInfoList(@PathVariable("c1Id")Long c1Id,
                                  @PathVariable("c2Id")Long c2Id,
                                  @PathVariable("c3Id")Long c3Id){
      List<BaseAttrInfo> infos =  baseAttrInfoService.getAttrInfoAndValueByCategoryId(c1Id,c2Id,c3Id);
      return Result.ok(infos);
    }

}
