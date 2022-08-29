package com.atguigu.gmall.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Jsons {
   private static  ObjectMapper mapper = new ObjectMapper();
    /**
     * 对象转json字符串
     * @param o
     * @return
     */
    public static String toStr(Object o) {
        //jackson

        try {
            String s = mapper.writeValueAsString(o);
            return  s;
        } catch (JsonProcessingException e) {
           return null;
        }

    }

    public static<T> T toObj(String jsonStr,Class<T> clz) {
        T t =null;
        try {
            t = mapper.readValue(jsonStr, clz);
            return t;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
