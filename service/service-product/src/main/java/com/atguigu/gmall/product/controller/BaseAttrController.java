package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;
    //admin/product/attrInfoList/2/13/61
    @Autowired
    BaseAttrValueService baseAttrValueService;

    /**
     * 根据分类查询对应属性
     * date 2022/8/23
     *
     * @author mi
     * @since 1.8
     */
    @GetMapping("/attrInfoList/{c1Id}/{c2Id}/{c3Id}")
    public Result getAttrInfoList(@PathVariable("c1Id") Long c1Id,
                                  @PathVariable("c2Id") Long c2Id,
                                  @PathVariable("c3Id") Long c3Id) {
        List<BaseAttrInfo> infos = baseAttrInfoService.getAttrInfoAndValueByCategoryId(c1Id, c2Id, c3Id);
        return Result.ok(infos);
    }

    /**
     * /saveAttrInfo
     * 请求体中的json
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo info) {
//        新增
        baseAttrInfoService.saveAttrInfo(info);
        return Result.ok();
    }

    /**
     * /admin/product/getAttrValueList/1
     * 根据属性id获得属性值
     */
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId")Long attrId){
        List<BaseAttrValue> list =  baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(list);
    }

    //




}
