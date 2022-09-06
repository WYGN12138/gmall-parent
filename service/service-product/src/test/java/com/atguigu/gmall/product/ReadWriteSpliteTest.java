package com.atguigu.gmall.product;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReadWriteSpliteTest {

    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;


    @Test
    public void testw(){



    }
    @Test
    public void testr(){
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark);
        BaseTrademark baseTrademark1 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark1);
        BaseTrademark baseTrademark2 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark2);
        BaseTrademark baseTrademark3 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark3);
        BaseTrademark baseTrademark4 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark4);
        BaseTrademark baseTrademark5 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark5);
        BaseTrademark baseTrademark6 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark6);
    }
}
