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
 * 〈集合命令处理类〉<br>
 *
 * @author 未绪
 * @time 2018/4/15 10:15
 */
@SuppressWarnings("unchecked")
public class SetCmdUtils {
    private static HashMap<String, JoonerObject<Object>> joonerObject = DataInfo.getJoonerInstance();

    public enum Operation {
        INTER, UNION, DIFFER
    }

    /**
     * 处理列表命令的方法
     *
     * @param keys 拆分后的命名数组
     */
    public static Object handleSetCmd(String[] keys) {
        try {
            String command = keys[0];
            String key = keys[1];
            // 除了添加之外其他指令都要检测是否值为空并且是SET键
            if (!JoonerKeyUtils.SET_ADD.equals(command)) {
                JoonerObject<Object> object = joonerObject.get(key);
                if (object == null) {
                    return DefaultInfoUtils.NULL_KEY;
                } else if (!isSet(key)) {
                    return DefaultInfoUtils.WRONG_TYPE;
                }
            }
            switch (command) {
                case JoonerKeyUtils.SET_ADD:
                    return SetCmdUtils.sadd(key, Arrays.asList(keys).subList(2, keys.length));
                case JoonerKeyUtils.SET_GET:
                    return SetCmdUtils.sget(key);
                case JoonerKeyUtils.SET_INTER:
                    return SetCmdUtils.sinter(key, keys[2].toUpperCase(), Operation.INTER);
                case JoonerKeyUtils.SET_UNION:
                    return SetCmdUtils.sinter(key, keys[2].toUpperCase(), Operation.UNION);
                case JoonerKeyUtils.SET_DIFFER:
                    return SetCmdUtils.sinter(key, keys[2].toUpperCase(), Operation.DIFFER);
                case JoonerKeyUtils.SET_POP:
                    return SetCmdUtils.spop(key);
                case JoonerKeyUtils.SET_REMOVE:
                    return SetCmdUtils.sremove(key, keys[2]);
                case JoonerKeyUtils.SET_LEN:
                    return SetCmdUtils.slen(key);
            }
        } catch (RuntimeException e) {
            return DefaultInfoUtils.DEFAULT_FAIL;
        }
        return DefaultInfoUtils.DEFAULT_FAIL;
    }

    /**
     * 往集合里面添加一个元素
     *
     * @param key
     * @param value
     * @return
     */
    public static Object sadd(String key, List<String> value) {
        JoonerObject<Object> object = joonerObject.get(key);
        HashSet<String> valueList;
        if (object == null) {
            valueList = new HashSet<>(value);
            object = new JoonerObject<>();
            object.setObj(valueList);
            object.setType(JoonerTypeUtils.JOONER_TYPE_SET);
            object.setEncoding(JoonerEncodingUtils.JOONER_ENCODING_SET);
            object.setTtl(DefaultInfoUtils.DEFAULT_TTL);
            joonerObject.put(key, object);
        } else if (isSet(key)) {
            valueList = (HashSet) object.getObj();
            valueList.addAll(value);
            object.setObj(valueList);
            joonerObject.put(key, object);
        } else {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 随机获取一个集合中的值
     *
     * @param key
     * @return
     */
    public static Object sget(String key) {
        JoonerObject<Object> object = joonerObject.get(key);
        HashSet<String> valueSet = (HashSet) object.getObj();
        return (valueSet.toArray())[((int) (Math.random() * (valueSet.size())))].toString();
    }


    public static Object spop(String key) {
        JoonerObject<Object> object = joonerObject.get(key);
        HashSet set = (HashSet) object.getObj();
        if (set.size() < 1) {
            ExpireUtils.setKeyExpird(key);
            return DefaultInfoUtils.DEFAULT_NULL;
        }
        Object popValue = SetCmdUtils.sget(key);
        set.remove(popValue);
        return popValue;
    }

    /**
     * 获取两个集合的交集，并集，差集
     *
     * @param key1      集合一的KEY
     * @param key2      集合二的KEY
     * @param operation 操作类型
     * @return 操作结果
     */
    public static Object sinter(String key1, String key2, Operation operation) {
        JoonerObject<Object> object1 = joonerObject.get(key1);
        JoonerObject<Object> object2 = joonerObject.get(key2);
        if (object2 == null) {
            return DefaultInfoUtils.NULL_KEY;
        } else if (!isSet(key2)) {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        HashSet<String> set1 = new HashSet<>((HashSet<String>) object1.getObj());
        HashSet<String> set2 = new HashSet<>((HashSet<String>) object2.getObj());
        switch (operation) {
            case INTER:
                set1.retainAll(set2);
                break;
            case UNION:
                set1.addAll(set2);
                break;
            case DIFFER:
                set1.removeAll(set2);
                break;
        }
        String tmpValaue = "";
        for (String value : set1) {
            tmpValaue += "\'" + value + "\' ";
        }
        return "".equals(tmpValaue) ? DefaultInfoUtils.DEFAULT_NULL : tmpValaue;
    }

    /**
     * 移除集合中的指定元素
     *
     * @param key
     * @param value
     * @return 已经移除的元素
     */
    public static Object sremove(String key, String value) {
        JoonerObject<Object> object = joonerObject.get(key);
        HashSet set = (HashSet) object.getObj();
        if (set.size() < 1) {
            ExpireUtils.setKeyExpird(key);
            return DefaultInfoUtils.DEFAULT_NULL;
        }
        boolean flag = set.remove(value);
        if (flag) {
            return DefaultInfoUtils.DEFAULT_SUCCESS;
        } else {
            return DefaultInfoUtils.DEFAULT_FAIL;
        }
    }

    /**
     * 获取当前集合的大小
     *
     * @param key
     * @return
     */
    public static Object slen(String key) {
        JoonerObject<Object> object = joonerObject.get(key);
        return ((HashSet) object.getObj()).size();
    }

    /**
     * 判断当前键对应的值是不是SET类型
     *
     * @param key
     * @return
     */
    private static boolean isSet(String key) {
        if (joonerObject.get(key) == null) return false;
        return joonerObject.get(key).getType() == JoonerTypeUtils.JOONER_TYPE_SET;
    }
}
