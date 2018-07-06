package com.db.jooner.utils.type;

import com.db.jooner.object.DataInfo;
import com.db.jooner.object.JoonerObject;
import com.db.jooner.utils.DefaultInfoUtils;
import com.db.jooner.utils.JoonerEncodingUtils;
import com.db.jooner.utils.JoonerTypeUtils;
import com.db.jooner.utils.global.ExpireUtils;
import com.db.jooner.utils.global.JoonerKeyUtils;

import java.util.*;

/**
 * 〈映射命令处理类〉<br>
 *
 * @author 未绪
 * @time 2018/4/15 17:07
 */
@SuppressWarnings("unchecked")
public class MapCmdUtils {
    private static HashMap<String, JoonerObject<Object>> joonerObject = DataInfo.getJoonerInstance();

    /**
     * 处理映射命令的方法
     *
     * @param keys 拆分后的命名数组
     */
    public static Object handleMapCmd(String[] keys) {
        try {
            String command = keys[0];
            String key = keys[1];
            // 除了添加之外其他指令都要检测是否值为空并且是SET键
            if (!JoonerKeyUtils.MAP_PUT.equals(command)) {
                JoonerObject<Object> object = joonerObject.get(key);
                if (object == null) {
                    return DefaultInfoUtils.NULL_KEY;
                } else if (!isMap(key)) {
                    return DefaultInfoUtils.WRONG_TYPE;
                }
            }
            switch (command) {
                case JoonerKeyUtils.MAP_PUT:
                    Map mapValue = MapCmdUtils.getMapValue(keys);
                    return MapCmdUtils.mput(key, mapValue);
                case JoonerKeyUtils.MAP_GET:
                    if (keys.length == 2) {
                        return MapCmdUtils.mget(key);
                    } else if (keys.length == 3) {
                        String field = keys[2];
                        return MapCmdUtils.mget(key, field);
                    }
                    break;
                case JoonerKeyUtils.MAP_FIELDS:// 后去当前键的所有域
                    return MapCmdUtils.mfields(key);
                case JoonerKeyUtils.MAP_REMOVE:
                    return MapCmdUtils.mremove(key, getFieldArray(keys));
                case JoonerKeyUtils.MAP_LEN:
                    return MapCmdUtils.mlen(key);
            }
            return DefaultInfoUtils.DEFAULT_FAIL;
        } catch (RuntimeException e) {
//            return e.getMessage();
            return DefaultInfoUtils.DEFAULT_FAIL;
        }
    }

    /**
     * 往映射中添加值
     *
     * @param key
     * @param value
     * @return
     */
    public static Object mput(String key, Map value) {
        JoonerObject<Object> object = joonerObject.get(key);
        HashMap<String, String> valueMap;
        if (object == null) {
            valueMap = new HashMap<>(value);
            object = new JoonerObject<>();
            object.setObj(valueMap);
            object.setType(JoonerTypeUtils.JOONER_TYPE_MAP);
            object.setEncoding(JoonerEncodingUtils.JOONER_ENCODING_MAP);
            object.setTtl(DefaultInfoUtils.DEFAULT_TTL);
            joonerObject.put(key, object);
        } else if (isMap(key)) {
            valueMap = (HashMap) object.getObj();
            valueMap.putAll(value);
            object.setObj(valueMap);
            joonerObject.put(key, object);
        } else {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 获取当前映射中的KEY对应所有的域和值
     *
     * @param key
     * @return
     */
    public static Object mget(String key) {
        JoonerObject<Object> object = joonerObject.get(key);
        HashMap<String, String> valueMap = (HashMap) object.getObj();
        String result = DefaultInfoUtils.DEFAULT_EMPTY;
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            result += (entry.getKey() + " '" + entry.getValue() + "' ");
        }
        return DefaultInfoUtils.DEFAULT_EMPTY.equals(result) ? DefaultInfoUtils.NULL_KEY : result;
    }

    /**
     * 获取KEY对应的FIELD对应的VALUE
     *
     * @param key
     * @param field
     * @return
     */
    public static Object mget(String key, String field) {
        JoonerObject<Object> object = joonerObject.get(key);
        HashMap<String, String> valueMap = (HashMap) object.getObj();
        String result = valueMap.get(field);
        return result == null ? DefaultInfoUtils.NULL_KEY : result;
    }

    /**
     * 移除当前的 KEY 对应的 多个 FIELDS
     *
     * @param key
     * @param fields
     * @return
     */
    public static Object mremove(String key, List<String> fields) {
        JoonerObject<Object> object = joonerObject.get(key);
        HashMap<String, String> valueMap = (HashMap) object.getObj();
        for (String field : fields) {
            valueMap.remove(field);
        }
        if (valueMap.isEmpty()) {
            ExpireUtils.setKeyExpird(key);
        }
        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 获取当前键的所有的 FIELD
     *
     * @param key
     * @return
     */
    public static Object mfields(String key) {
        String result = DefaultInfoUtils.DEFAULT_EMPTY;
        JoonerObject<Object> object = joonerObject.get(key);
        HashMap<String, String> valueMap = (HashMap) object.getObj();
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            result += (entry.getKey() + " ");
        }

        return DefaultInfoUtils.DEFAULT_EMPTY.equals(result) ? DefaultInfoUtils.NULL_KEY : result;
    }

    public static Object mlen(String key) {
        System.out.println(key);
        JoonerObject<Object> object = joonerObject.get(key);
        HashMap<String, String> valueMap = (HashMap) object.getObj();
        return valueMap.size();
    }

/**---------------------------------------------------------------------------------------**/
    /**
     * 判断当前键对应的值是不是MAPT类型
     *
     * @param key
     * @return
     */
    private static boolean isMap(String key) {
        if (joonerObject.get(key) == null) return false;
        return joonerObject.get(key).getType() == JoonerTypeUtils.JOONER_TYPE_MAP;
    }

    /**
     * 从一个数组里面获取一个MAP
     */
    public static Map getMapValue(String[] array) {
        Map<String, String> valueMap = new HashMap<>();
        // i 从 2 开始 去除第一个命令 再去除第二个 key
        for (int i = 2; i < array.length; i++) {
            String field = array[i];
            i++;
            String value = array[i];
            valueMap.put(field, value);
        }
        return valueMap;
    }

    public static List getFieldArray(String[] array) {
        List fields = new ArrayList();
        // i 从 2 开始 去除第一个命令 再去除第二个 key
        for (int i = 2, j = 0; i < array.length; i++) {
            String field = array[i];
            fields.add(field);
            i++;                    // 跳过 VALUE
        }
        return fields;
    }
}
