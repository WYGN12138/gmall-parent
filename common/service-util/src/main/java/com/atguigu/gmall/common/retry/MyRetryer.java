package com.atguigu.gmall.common.retry;

import feign.RetryableException;
import feign.Retryer;

/**
 * 幂等性： 做一次和做很多次效果一样
 *   -select / update /delete 天然幂等
 *   -insert 不幂等 每次操作都会导致新入数据
 *   -所以 关闭重试  特殊业务放大读取超时
 */
public class MyRetryer implements Retryer {

    private int cur = 0;
    private int max = 2;

    public MyRetryer(){
        cur=0;
        max=2;
    }

    /**
     * 继续重试还是抛出异常
     * @param e
     */
    @Override
    public void continueOrPropagate(RetryableException e) {
        throw e;
//        if (cur++>max){
//            throw  e;
//        }
    }

    @Override
    public Retryer clone() {
        return new MyRetryer();
    }
}
