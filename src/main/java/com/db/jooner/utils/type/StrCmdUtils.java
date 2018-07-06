package com.db.jooner.utils.type;

import com.db.jooner.object.DataInfo;
import com.db.jooner.object.JoonerObject;
import com.db.jooner.utils.DefaultInfoUtils;
import com.db.jooner.utils.JoonerEncodingUtils;
import com.db.jooner.utils.JoonerTypeUtils;
import com.db.jooner.utils.global.ExpireUtils;
import com.db.jooner.utils.global.JoonerKeyUtils;

import java.util.HashMap;

/**
 * 〈字符串命令解析方法〉<br>
 *
 * @author 未绪
 * @time 2018/3/4 14:52
 */
public class StrCmdUtils {
    private static HashMap<String, JoonerObject<Object>> joonerObject = DataInfo.getJoonerInstance();

    /**
     * 字符串命令解析方法
     *
     * @param keys 拆分后的命名数组
     */
    public static Object handleStrCmd(String[] keys) {

        String command = keys[0];
        String key = keys[1];
        // 除了添加之外其他指令都要检测是否值为空并且是SET键
        if (!JoonerKeyUtils.STR_SET.equals(command)) {
            JoonerObject<Object> object = joonerObject.get(key);
            if (object == null) {
                return DefaultInfoUtils.NULL_KEY;
            } else if (!isStr(key)) {
                return DefaultInfoUtils.WRONG_TYPE;
            }
        }

        if (keys[0].equals(JoonerKeyUtils.STR_SET)) {
            return StrCmdUtils.strSet(keys[1], keys[2]);     //设置值
        } else if (keys[0].equals(JoonerKeyUtils.STR_APPEND)) {
            return StrCmdUtils.strAppend(keys[1], keys[2]);
        } else if (keys[0].equals(JoonerKeyUtils.STR_LEN)) {
            return StrCmdUtils.strLen(keys[1]);
        } else if (keys[0].equals(JoonerKeyUtils.STR_GET)) {
            return StrCmdUtils.strGet(keys[1]);     //获取值
        }

        return DefaultInfoUtils.DEFAULT_FAIL;
    }

    /**
     * 设置字符串的值
     *
     * @param key          字符串的键
     * @param value        字符串的值
     * @return 1 设置成功 -1 设置失败
     */
    public static int strSet(String key, String value) {
        JoonerObject<Object> keyObject = new JoonerObject<>();
        keyObject.setEncoding(JoonerEncodingUtils.JOONER_ENCODING_STR);
        keyObject.setTtl(DefaultInfoUtils.DEFAULT_TTL);
        keyObject.setType(JoonerTypeUtils.JOONER_TYPE_STR);
        keyObject.setObj(value);
        joonerObject.put(key, keyObject);

        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 获取字符串的值
     *
     * @param key          字符串的键
     */
    public static Object strGet(String key) {

        JoonerObject<Object> object = joonerObject.get(key);
        return object == null ? null : object.getObj();
    }

    /***
     * 在字符串后面追加数据
     * @param key           待追加数据的键
     * @param value         追加的数据
     * @return
     */
    public static int strAppend(String key, String value) {

        JoonerObject<Object> keyObject = joonerObject.get(key);
        if (keyObject == null) {
            return strSet(key, value);
        }
        keyObject.setObj(keyObject.getObj().toString() + value);
        joonerObject.put(key, keyObject);

        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 获取当前键存储的数据的长度
     *
     * @param key
     * @return 数据的长度
     */
    public static int strLen(String key) {

        JoonerObject<Object> keyObject = joonerObject.get(key);

        return keyObject == null ? DefaultInfoUtils.DEFAULT_FAIL : keyObject.getObj().toString().length();
    }

    /**
     * 判断当前键对应的值是不是STR类型
     *
     * @param key
     * @return
     */
    private static boolean isStr(String key) {
        if (joonerObject.get(key) == null) return false;
        return joonerObject.get(key).getType() == JoonerTypeUtils.JOONER_TYPE_STR;
    }
}
