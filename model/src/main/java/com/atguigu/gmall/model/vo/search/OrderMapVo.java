package com.atguigu.gmall.model.vo.search;

import com.tdunning.math.stats.Sort;
import lombok.Data;

@Data
public class OrderMapVo {
    private String type; //排序类型 1：综合 2：价格
    private String  Sort; //排序方式： asc：升序 desc：降序
}
