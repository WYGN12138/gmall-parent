package com.atguigu.gmall.model.vo.search;

import lombok.Data;

import java.util.List;

/**
 * 属性列表
 */
@Data
public class AttrVo {
    private Long attrId;
    private String attrName;
    //每个属性涉及到的所有属性值
    private List<String> attrValueList;



}
