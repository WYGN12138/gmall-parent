package com.atguigu.gmall.model.to;

import lombok.Data;

import java.util.List;

/**
 * DDD:领域驱动设计
 * 三级分类树形结构
 * 支持无限层级
 */
@Data
public class CategoryTreeTo {
    private Long categoryId;//1
    private String categoryName;
    private List<CategoryTreeTo> categoryChild; //子分类

}
