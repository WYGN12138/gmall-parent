package com.atguigu.gmall.common.constant;

public class SysRedisConst {
    public static  final String NULL_VAL = "x";

    public static final String SKU_DETAIL = "lock:sku:detail:";
    public static final long NULL_VAL_TTL = 60*30L;
    public static final Long SKUDETAIL_TTL = 60*60*24*7L;
    public static final String SKU_INFO_CACHE_KEY = "sku:info:";
}
