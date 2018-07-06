package com.db.jooner.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 *
 * @author 未绪
 * @time 2018/5/17 8:51
 */
public class JsonUtils {
    /**
     * 将json转化成map
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> convertJsonStrToMap(String jsonStr) {

        Map<String, Object> map = JSON.parseObject(
                jsonStr, new TypeReference<Map<String, Object>>() {
                });
        return map;

    }
}
