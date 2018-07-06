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
 * 〈处理有关列表的数据类型的工具类〉<br>
 *
 * @author 未绪
 * @time 2018/3/17 11:26
 */
@SuppressWarnings("unchecked")
public class ListCmdUtils {

    private static HashMap<String, JoonerObject<Object>> joonerObject = DataInfo.getJoonerInstance();

    /**
     * 处理集合命令的方法
     *
     * @param keys 拆分后的命名数组
     */
    public static Object handleListCmd(String[] keys) {
        if (keys[0].equals(JoonerKeyUtils.LISTL_PUSH)) {
            return ListCmdUtils.llpush(keys[1], Arrays.asList(keys).subList(2, keys.length));
        } else if (keys[0].equals(JoonerKeyUtils.LISTR_PUSH)) {
            return ListCmdUtils.lrpush(keys[1], Arrays.asList(keys).subList(2, keys.length));
        } else if (keys[0].equals(JoonerKeyUtils.LIST_POP)) {
            return ListCmdUtils.lpop(keys[1]);
        } else if (keys[0].equals(JoonerKeyUtils.LIST_LEN)) {
            return ListCmdUtils.llen(keys[1]);
        } else if (keys[0].equals(JoonerKeyUtils.LIST_GET)) {
            if (keys.length == 3) {
                return ListCmdUtils.lget(keys[1], Integer.parseInt(keys[2]));
            } else if (keys.length == 4) {
                return ListCmdUtils.lget(keys[1], Integer.parseInt(keys[2]), Integer.parseInt(keys[3]));
            }
        } else if (keys[0].equals(JoonerKeyUtils.LIST_REMOVE)) {
            return ListCmdUtils.lremove(keys[1], Integer.parseInt(keys[2]), keys.length == 4 ? Integer.parseInt(keys[3]) : null);
        } else if (keys[0].equals(JoonerKeyUtils.LIST_INSERT)) {
            return ListCmdUtils.linsert(keys[1],Integer.parseInt(keys[2]), Arrays.asList(keys).subList(3, keys.length));
        } else if (keys[0].equals(JoonerKeyUtils.LIST_REPLACE)) {
            return ListCmdUtils.lreplace(keys[1], Integer.parseInt(keys[2]), keys[3]);
        }
        return DefaultInfoUtils.DEFAULT_FAIL;
    }

    /**
     * 往一个列表里面插入一个值(左插入)
     * 如果当前列表里面存在值，则直接在后面追加
     * 反之，重新new一个添加
     *
     * @param key
     * @param value
     * @return
     */
    private static Object llpush(String key, List<String> value) {

        JoonerObject<Object> object = joonerObject.get(key);
        ArrayList<String> valueList;
        if (object == null) {
            valueList = new ArrayList<>(value);
            insertIntoList(valueList, object, key);
        } else if (isList(key)) {
            valueList = (ArrayList) object.getObj();
            valueList.addAll(value);
            object.setObj(valueList);
            joonerObject.put(key, object);
        } else {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 往一个列表里面插入一个值(右边插入)
     * 如果当前列表里面存在值，则直接在后面追加
     * 反之，重新new一个添加
     *
     * @param key
     * @param value
     * @return
     */
    private static Object lrpush(String key, List value) {
        Collections.reverse(value);
        JoonerObject<Object> object = joonerObject.get(key);
        ArrayList valueList;
        if (object == null) {
            valueList = new ArrayList<String>(value);
            insertIntoList(valueList, object, key);
        } else if (isList(key)) {
            valueList = (ArrayList) object.getObj();
            valueList.addAll(0, value);
            object.setObj(valueList);
            joonerObject.put(key, object);
        } else {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 弹出栈顶元素
     *
     * @param key
     * @return
     */
    private static Object lpop(String key) {
        JoonerObject<Object> object = joonerObject.get(key);
        if (object == null) {
            return DefaultInfoUtils.NULL_KEY;
        } else if (!isList(key)) {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        ArrayList list = (ArrayList) object.getObj();
        if (list.size() < 1) {
            ExpireUtils.setKeyExpird(key);
            return DefaultInfoUtils.DEFAULT_NULL;
        }
        Object popValue = list.get(0);
        list.remove(0);
        return popValue;
    }

    private static void insertIntoList(ArrayList valueList, JoonerObject<Object> object, String key) {

        object = new JoonerObject<>();
        object.setObj(valueList);
        object.setType(JoonerTypeUtils.JOONER_TYPE_LIST);
        object.setEncoding(JoonerEncodingUtils.JOONER_ENCODING_LIST);
        object.setTtl(DefaultInfoUtils.DEFAULT_TTL);
        joonerObject.put(key, object);
    }

    /**
     * 根据索引获取值
     *
     * @param key
     * @param index
     * @return
     */
    public static Object lget(String key, int index) {
        JoonerObject<Object> object = joonerObject.get(key);
        if (object == null) {
            return DefaultInfoUtils.NULL_KEY;
        } else if (!isList(key)) {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        ArrayList list = (ArrayList) object.getObj();
        return list.get(index);
    }

    /**
     * 根据范围获取值
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static Object lget(String key, int start, int end) {
        JoonerObject<Object> object = joonerObject.get(key);
        if (object == null) {
            return DefaultInfoUtils.NULL_KEY;
        } else if (!isList(key)) {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        ArrayList list = (ArrayList) object.getObj();
        if (end < start || end > list.size()) {
            end = list.size();
        }
        String listInfo = "";
        for (int i = start; i < end; i++) {
            listInfo = listInfo + "\'" + list.get(i) + "\'";
            if (i != (end - 1)) {
                listInfo += " ";
            }
        }
        return listInfo;
    }

    /**
     * 获取当前列表的长度
     */
    public static Object llen(String key) {
        JoonerObject<Object> object = joonerObject.get(key);
        if (object == null) {
            return DefaultInfoUtils.NULL_KEY;
        } else if (!isList(key)) {
            return DefaultInfoUtils.WRONG_TYPE;
        }

        return ((ArrayList) object.getObj()).size();
    }

    /**
     * 删除范围内的LIST的值 包含前不包含后
     *
     * @param key
     * @param start 如果end为空 则end=start+1
     * @param end
     * @return 删除成功的元素的个数
     */
    private static Object lremove(String key, int start, Integer end) {
        JoonerObject<Object> object = joonerObject.get(key);
        if (object == null) {
            return DefaultInfoUtils.NULL_KEY;
        } else if (!isList(key)) {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        if (end == null) {
            end = start + 1;
        }
        ArrayList list = (ArrayList) object.getObj();
        if (end < start || end > list.size()) {
            end = list.size();
        }
        for (int i = start; i < end; i++) {
            list.remove(start);
        }
        if (list.size() == 0) {
            ExpireUtils.setKeyExpird(key);
        }
        return end - start;
    }

    /**
     * 替换对应索引对应的值
     */
    private static Object lreplace(String key, int index, String newValue) {
        JoonerObject<Object> object = joonerObject.get(key);
        if (object == null) {
            return DefaultInfoUtils.NULL_KEY;
        } else if (!isList(key)) {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        ArrayList list = (ArrayList) object.getObj();
        if (index < list.size() && 0 <= index) {
            list.set(index, newValue);
            object.setObj(list);
            joonerObject.put(key, object);
            return DefaultInfoUtils.DEFAULT_SUCCESS;
        }
        return DefaultInfoUtils.DEFAULT_FAIL;
    }

    /**
     * 在指定索引插入一个值（可以插入数组）
     * @param key
     * @param index
     * @param valueList 插入值的数组
     * @return
     */
    private static Object linsert(String key,int index , List valueList){
        JoonerObject<Object> object = joonerObject.get(key);
        if (object == null) {
            return DefaultInfoUtils.NULL_KEY;
        } else if (!isList(key)) {
            return DefaultInfoUtils.WRONG_TYPE;
        }
        ArrayList list = (ArrayList) object.getObj();
        if(index >= 0 && index<list.size()){
            list.addAll(index,valueList);
            object.setObj(list);
            joonerObject.put(key,object);
            return valueList.size();
        }
        return DefaultInfoUtils.DEFAULT_FAIL;
    }
    /**
     * 判断当前键对应的值是不是LIST类型
     *
     * @param key
     * @return
     */
    private static boolean isList(String key) {
        if (joonerObject.get(key) == null) return false;
        return joonerObject.get(key).getType() == JoonerTypeUtils.JOONER_TYPE_LIST;
    }

}
