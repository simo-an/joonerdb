package com.db.jooner.utils;

/**
 * 〈一些默认信息的工具类〉<br>
 *
 * @author 未绪
 * @time 2018/3/4 15:13
 */
public class DefaultInfoUtils {

    // 默认的键的生命周期 -1 表示永不过期
    public static int DEFAULT_TTL = -1 ;

    // 默认端口
    public static int DEFAULT_HOST_PORT = 5991;

    // 主服务器
    public static String ROLE_MASER = "master";

    // 从服务器
    public static String ROLE_SLAVE = "slave";

    // 默认成功
    public static final int DEFAULT_SUCCESS = 1;
    // 默认失败
    public static final int DEFAULT_FAIL = -1;

    // 默认空值
    public static final String DEFAULT_EMPTY = "";

    // 类型不对
    public static final String WRONG_TYPE = "fail:the type of the key is wrong";
    // 键是空值
    public static final String NULL_KEY = "fail:the key is null";

    // null 值
    public static final String DEFAULT_NULL =null;

}
