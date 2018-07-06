package com.db.jooner.utils.type;

import com.db.jooner.object.DataInfo;
import com.db.jooner.object.JoonerObject;
import com.db.jooner.utils.DefaultInfoUtils;
import com.db.jooner.utils.JoonerTypeUtils;
import com.db.jooner.utils.global.DataSaveUtils;
import com.db.jooner.utils.global.JoonerKeyUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 〈处理全局命令的工具类〉<br>
 *
 * @author 未绪
 * @time 2018/3/3 10:20
 */
public class GlobalCmdUtils {

    private static HashMap<String, JoonerObject<Object>> joonerObject = DataInfo.getJoonerInstance();

    /**
     * 处理全局命令的方法
     *
     * @param keys 拆分后的命名数组
     */
    public static Object handleGlobalCmd(String[] keys) {

        switch (keys[0]) {
            case JoonerKeyUtils.GLOBAL_REMOVE:
                return GlobalCmdUtils.remove(keys[1]);
            case JoonerKeyUtils.GLOBAL_RENAME:
                return GlobalCmdUtils.rename(keys[1], keys[2]);
            case JoonerKeyUtils.GLOBAL_GETTTL:
                return GlobalCmdUtils.gttl(keys[1]);
            case JoonerKeyUtils.GLOBAL_SETTTL:
                return GlobalCmdUtils.sttl(keys[1], Double.parseDouble(keys[2]));
            case JoonerKeyUtils.GLOBAL_GETTYPE:
                return GlobalCmdUtils.gtype(keys[1]);
            case JoonerKeyUtils.GLOBAL_GETALLKEYS:
                return GlobalCmdUtils.getAllKeys(keys[1]);
            case JoonerKeyUtils.GLOBAL_BGSAVE:
                return GlobalCmdUtils.bgsave();
            default:
                break;
        }
        return DefaultInfoUtils.DEFAULT_FAIL;
    }

    /***
     * 删除一个键
     * @param key              待删除的键
     * @return 删除成功与否的标识
     */
    public static int remove(String key) {

        joonerObject.remove(key);

        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 更新键的名称
     * 使用 `HashMap<String, JoonerObject<Object>> 该指令就会失效`
     *
     * @param key     原来的键
     * @param tempKey 更新后的键
     * @return
     */
    public static int rename(String key, String tempKey) {

        JoonerObject<Object> keyObject = joonerObject.get(key);
        if (keyObject == null) {
            return DefaultInfoUtils.DEFAULT_FAIL;
        }
        joonerObject.remove(key);
        joonerObject.put(tempKey.toUpperCase(), keyObject);
        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 获取当前键的类型
     *
     * @param key
     * @return
     */
    public static Object gtype(String key) {

        JoonerObject<Object> keyObject = joonerObject.get(key);
        if (keyObject == null) {
            return DefaultInfoUtils.DEFAULT_FAIL;
        }
        return JoonerTypeUtils.TYPE_ARRAY[keyObject.getType()];
    }

    /**
     * 获取所有的键 支持通配符查询
     *
     * @param regex 模糊查询 *aaa aaa*a  * aaa*
     * @return
     */
    public static Object getAllKeys(String regex) {

        String allKeys = "";

        Iterator iter = joonerObject.entrySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            if (GlobalCmdUtils.isAlike(key, regex)) {
                count++;
                if (count == 1) {
                    allKeys += "(" + count + ")" + key;
                } else {
                    allKeys += " " + "(" + count + ")" + key;
                }
            }
        }

        return allKeys.equals("") ? null : allKeys;
    }

    /**
     * 判断当前的字符串是否满足当前的匹配规则 !!!
     *
     * @param key
     * @param regex
     * @return
     */
    public static boolean isAlike(String key, String regex) {
        // *号匹配
        int lstr = key.length();
        int lreg = regex.length();
        int x1 = regex.indexOf("*");
        switch (x1) {
            case -1: {
                //x1=-1    regex 中没有 * 号，不需要跌归计算
                if (lstr == lreg) {
                    if (lstr == 0) return true;
                    for (int kk = 0; kk < lreg; kk++)//检测字符串是否匹配
                        if (key.charAt(kk) != regex.charAt(kk)) return false;
                    return true;
                } else
                    return false;
            }
            case 0: {//x1=0 regex 中 * 号在首位
                if (lreg == 1) return true;//只有一个星号，自然是匹配的，如 regex="*"
                boolean right = false;
                int p = 0;
                // *号在首位，定位 * 号 后一位
                for (int k = 0; k < lstr; k++)
                    if (key.charAt(k) == regex.charAt(x1 + 1) || regex.charAt(x1 + 1) == '*') {
                        p = k;
                        right = true;
                        break;
                    }//遇到 ** 就直接 right=true;
                if (right == false) return false;
                else {
                    if (p == lstr) return true;
                    return isAlike(key.substring(p, lstr), regex.substring(x1 + 1, lreg));
                }
            }
            default: {    //x1>0
                for (int i = 0; i < x1; i++)
                    if (key.charAt(i) != regex.charAt(i)) return false;
                return isAlike(key.substring(x1, lstr), regex.substring(x1, lreg));
            }
        }
    }

    /**
     * 设置相应键的生命周期
     *
     * @param key  相应的键
     * @param time 过期时间（单位秒s）
     * @return
     */
    public static int sttl(String key, Double time) {

        JoonerObject<Object> object = joonerObject.get(key);
        if (object == null) {
            return DefaultInfoUtils.DEFAULT_FAIL;
        }
        object.setTtl((new Date()).getTime() + (long) (time * 1000));

        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

    /**
     * 获取相应键的生命周期（过期则返回 -1）
     *
     * @param key
     * @return
     */
    public static long gttl(String key) {

        JoonerObject<Object> object = joonerObject.get(key);
        if (object == null) {
            return DefaultInfoUtils.DEFAULT_FAIL;
        }
        long expire = object.getTtl() - (new Date().getTime());
        return expire < 0 ? DefaultInfoUtils.DEFAULT_FAIL : expire / 1000;
    }

    public static Object bgsave() {
        DataSaveUtils.doDataSave();
        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }

}
