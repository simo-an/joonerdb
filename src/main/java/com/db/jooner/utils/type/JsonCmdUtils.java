package com.db.jooner.utils.type;

import com.alibaba.fastjson.JSONObject;
import com.db.jooner.object.DataInfo;
import com.db.jooner.object.JoonerObject;
import com.db.jooner.utils.DefaultInfoUtils;
import com.db.jooner.utils.JoonerEncodingUtils;
import com.db.jooner.utils.JoonerTypeUtils;
import com.db.jooner.utils.JsonUtils;
import com.db.jooner.utils.global.JoonerKeyUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 *
 * @author 未绪
 * @time 2018/5/17 9:01
 */
public class JsonCmdUtils {
    private static HashMap<String, JoonerObject<Object>> joonerObject = DataInfo.getJoonerInstance();

    /**
     * 处理JSON命令的方法
     *
     * @param key JSON 键
     * @param cmd 客户端的指令
     */
    public static Object handleJsonCmd(String dbkey, String key, String cmd, String[] keys) {

        String command = keys[0];
        // 除了添加之外JSON_SET其他指令都要检测是否值为空并且是JSON键
        if (!JoonerKeyUtils.JSON_SET.equals(command)) {
            JoonerObject<Object> object = joonerObject.get(key);
            if (object == null) {
                return DefaultInfoUtils.NULL_KEY;
            } else if (!isJson(key)) {
                return DefaultInfoUtils.WRONG_TYPE;
            }
        }
        switch (dbkey) {
            case JoonerKeyUtils.JSON_SET:
                int start = cmd.indexOf("{");
                if (start < 0) {
                    break;
                }
                int end = 0;
                String jsonValue = cmd.substring(cmd.indexOf("{"), cmd.length());
                for (int i = 0; i < keys.length; i++) {
                    if (keys[i].startsWith("{")) {
                        end = i;
                        break;
                    }
                }
                String[] keyValues = Arrays.copyOfRange(keys, 2, end);

                return JsonCmdUtils.jsonSet(key, jsonValue, keyValues);
            case JoonerKeyUtils.JSON_PUT:
                keyValues = Arrays.copyOfRange(keys, 2, keys.length - 1);
                return JsonCmdUtils.jsonPut(key, keyValues, keys[keys.length - 1]);
            case JoonerKeyUtils.JSON_GET:
                return JsonCmdUtils.jsonGet(key, keys);
            case JoonerKeyUtils.JSON_REMOVE:
                keyValues = Arrays.copyOfRange(keys, 2, keys.length);
                return JsonCmdUtils.jsonRemove(key, keyValues);
        }

        return DefaultInfoUtils.DEFAULT_FAIL;
    }

    /**
     * 往JSON中添加JSON
     *
     * @param key
     * @param jsonValue
     * @param keyValues 指定项
     * @return
     */
    public static Object jsonSet(String key, String jsonValue, String[] keyValues) {
        JoonerObject<Object> object = joonerObject.get(key);
        Map mapValue;
        if (object == null || keyValues.length == 0) {
            mapValue = JsonUtils.convertJsonStrToMap(jsonValue);
            object = new JoonerObject<>();
            object.setObj(mapValue);
            object.setType(JoonerTypeUtils.JOONER_TYPE_JSON);
            object.setEncoding(JoonerEncodingUtils.JOONER_ENCODING_JSON);
            object.setTtl(DefaultInfoUtils.DEFAULT_TTL);
            joonerObject.put(key, object);
        } else if (isJson(key)) {
            Map oldValue = (Map) object.getObj();
            Map tmpValue = JsonCmdUtils.setMapValue(keyValues, jsonValue, oldValue);
            object.setObj(tmpValue);
            joonerObject.put(key, object);
        } else {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 往JSON中添加字符串
     *
     * @param key
     * @param keyValues
     * @param value
     * @return
     */
    public static Object jsonPut(String key, String[] keyValues, String value) {
        for (int i = 0; i < keyValues.length; i++) {
            System.out.println(keyValues[i]);
        }
        JoonerObject<Object> object = joonerObject.get(key);
        Map oldValue = (Map) object.getObj();

        Map tmpValue = JsonCmdUtils.setMapValue(keyValues, value, oldValue);
        object.setObj(tmpValue);
        return DefaultInfoUtils.DEFAULT_SUCCESS;

    }

    /**
     * @param keyValues
     * @param value
     * @param map
     * @return
     */
    public static Map setMapValue(String[] keyValues, String value, Map map) {
        if (keyValues.length > 1) {
            Map tmp = setMapValue(Arrays.copyOfRange(keyValues, 1, keyValues.length), value, (Map) map.get(keyValues[0]));
            map.put(keyValues[0], tmp);
        }
        if (keyValues.length == 1) {
            if (value.startsWith("{")) {
                map.put(keyValues[0], JsonUtils.convertJsonStrToMap(value));
            } else {
                map.put(keyValues[0], value);
            }
        }
        return map;
    }

    /**
     * 获取JSON中的某一值
     *
     * @param key
     * @param keyValues
     * @return
     */
    public static Object jsonGet(String key, String[] keyValues) {
        JoonerObject<Object> object = joonerObject.get(key);
        Map tmpValue = (Map) object.getObj();
        String strResult = "";
        // 跳过 cmd key 从 2 开始遍历
        for (int i = 2; i < keyValues.length; i++) {
            Object obj = tmpValue.get(keyValues[i]);
            if (obj instanceof Map) {
                tmpValue = (Map) tmpValue.get(keyValues[i]);
            } else {
                strResult = tmpValue.get(keyValues[i]).toString();
            }
        }
        if (!"".equals(strResult)) {
            return strResult;
        }
        JSONObject jsonObject = new JSONObject(tmpValue);
        return jsonObject;
    }

    /**
     * 移除JSON中的某一值
     *
     * @param key
     * @param keyValues
     * @return
     */
    public static Object jsonRemove(String key, String[] keyValues) {
        JoonerObject<Object> object = joonerObject.get(key);
        Map mapValue = (Map) object.getObj();
        mapValue = removeMapValue(keyValues, mapValue);
        object.setObj(mapValue);
        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * @param keyValues\
     * @param map
     * @return
     */
    public static Map removeMapValue(String[] keyValues, Map map) {
        if (keyValues.length > 1) {
            Map tmp = removeMapValue(Arrays.copyOfRange(keyValues, 1, keyValues.length), (Map) map.get(keyValues[0]));
            map.put(keyValues[0], tmp);
        }
        if (keyValues.length == 1) {
            System.out.println(keyValues[0]);
            map.remove(keyValues[0]);
        }
        return map;
    }

    /**
     * 判断当前键对应的值是不是JSON类型
     *
     * @param key
     * @return
     */
    private static boolean isJson(String key) {
        if (joonerObject.get(key) == null) return false;
        return joonerObject.get(key).getType() == JoonerTypeUtils.JOONER_TYPE_JSON;
    }
}
