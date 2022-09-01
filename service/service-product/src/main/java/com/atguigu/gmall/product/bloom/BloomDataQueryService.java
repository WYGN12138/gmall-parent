package com.atguigu.gmall.product.bloom;

import java.util.List;

/**
 * 布隆数据查询服务
 */
public interface BloomDataQueryService {

   /**
    * ：所有设计模式都是：封装、继承、多态
    * 模板模式
    * 父类规定好流程 ，子类动态改变传入
    * @return
    */
   List queryData();
}
