package com.atguigu.starter.cache.utils;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;


public class Jsons {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * 对象转json字符串
     *
     * @param o
     * @return
     */
    public static String toStr(Object o) {
        //jackson

        try {
            String s = mapper.writeValueAsString(o);
            return s;
        } catch (JsonProcessingException e) {
            return null;
        }

    }

    /**
     * 返回带泛型的对象
     *
     * @param jsonStr
     * @param tr
     * @param <T>
     * @return
     */
    public static <T> T toObj(String jsonStr, TypeReference<T> tr) {
        if (StringUtils.isEmpty(jsonStr)) {
            return null;
        }
        T t = null;
        try {
            t = mapper.readValue(jsonStr,tr);
            return t;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 把js转换为普通类型的对象
     *
     * @param jsonStr
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T toObj(String jsonStr, Class<T> clz) {
        if (StringUtils.isEmpty(jsonStr)) {
            return null;
        }
        T t = null;
        try {
            t = mapper.readValue(jsonStr, clz);
            return t;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
