package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;

import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;

    /**
     * 检索列表
     * 检索条件
     * 1.按照分类 ：
     * 2. 按照关键字：
     * 3. 按照属性(多个)
     * 4.按照品牌
     * 5.分页
     * 6.排序
     *
     * @return
     */
    @GetMapping("/list.html")
    public String search(SearchParamVo searchParamVo, Model model) {

        Result<SearchResponseVo> search = searchFeignClient.search(searchParamVo);
        SearchResponseVo data = search.getData();
        //把result数据展示到页面
        //1.以前检索页面点击传来的所有条件，返给页面
        model.addAttribute("searchParam", data.getSearchParam());
        //2.品牌面包屑位置的显式
        model.addAttribute("trademarkParam", data.getTrademarkParam());
        //3.属性面包屑 集合 集合里面的每一个元素就是一个对象 拥有这些数据（attrId，attrName，attrValue）
        model.addAttribute("propsParamList", data.getPropsParamList());
        //4.所有品牌集合，集合里面每一个元素都是对象（tmid、tmlogoUrl、tmName）
        model.addAttribute("trademarkList", data.getTrademarkList());
        //5.所有属性，集合 集合里面每一个元素就是一个对象，拥有这些数据（attrId、attrName、list<String> attrValueList）
        model.addAttribute("attrsList", data.getAttrsList());
        //6.排序信息（type、sort）
        model.addAttribute("orderMap", data.getOrderMap());
        //7.所有商品列表 集合每个元素都有（id，price、imageList、title）
        model.addAttribute("goodsList", data.getGoodsList());
        //8.分页信息
        model.addAttribute("pageNo", data.getPageNo());
        model.addAttribute("totalPages", data.getTotalPages());
        //9.参数url
        model.addAttribute("urlParam", data.getUrlParam());

        model.addAttribute("", search);


        return "list/index";
    }


}
