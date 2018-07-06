package com.db.jooner.utils.global;

import com.db.jooner.object.DataInfo;
import com.db.jooner.object.JoonerObject;
import com.db.jooner.utils.DefaultInfoUtils;
import com.db.jooner.utils.type.GlobalCmdUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 〈处理和过期键有关的工具类〉<br>
 *
 * @author 未绪
 * @time 2018/3/11 8:44
 */
public class ExpireUtils {

    /**
     * 判断当前时间是否过期
     *
     * @param expireTime
     * @return
     */
    public static boolean isExpired(long expireTime) {
        return (expireTime - new Date().getTime()) < 0;
    }

    /**
     * 判断当前对象是否过期
     *
     * @param object
     * @return
     */
    public static boolean isExpired(JoonerObject<Object> object) {
        long expireTime = object.getTtl();
        return (expireTime - new Date().getTime()) < 0;
    }

    /**
     * 判断当前指令对应的对象是否过期
     *
     * @param key
     * @return
     */
    public static boolean isExpired(String key) {
        JoonerObject<Object> object = DataInfo.getJoonerInstance().get(key);
        if (object == null || object.getTtl() == DefaultInfoUtils.DEFAULT_TTL) {
            return false;
        }
        long expireTime = object.getTtl();
        return (expireTime - new Date().getTime()) < 0;
    }

    /**
     * 检测并删除已经过期的键
     */
public static void checkExpiredKey() {
    HashMap<String, JoonerObject<Object>> joonerObject = DataInfo.getJoonerInstance();
    Iterator iter = joonerObject.entrySet().iterator();
    while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry) iter.next();
        String key = entry.getKey().toString();
        JoonerObject<Object> object = joonerObject.get(key);
        if (object.getTtl() != DefaultInfoUtils.DEFAULT_TTL && isExpired(object.getTtl())) {
            GlobalCmdUtils.remove(key);
        }
    }
}

    /**
     * 设置当前键已经过期(直接删除即可)
     */
    public static void setKeyExpird(String key) {
        GlobalCmdUtils.remove(key);
    }
}
