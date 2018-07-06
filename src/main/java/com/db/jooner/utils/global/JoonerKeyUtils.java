package com.db.jooner.utils.global;

/**
 * 〈当前数据库的所有键的集合〉<br>
 *
 * @author 未绪
 * @time 2018/2/14 21:43
 */
public class JoonerKeyUtils {

    //全局命令 GLOBAL
    public static final String GLOBAL_REMOVE = "REMOVE";      //移除相应的键
    public static final String GLOBAL_RENAME = "RENAME";      //重命名相应的键
    public static final String GLOBAL_SETTTL = "STTL";      //设置相应的键的生存时间
    public static final String GLOBAL_GETTTL = "GTTL";      //获取相应的键的生存时间
    public static final String GLOBAL_GETTYPE = "GTYPE";      //获取相应的键的类型（一共有五种）
    public static final String GLOBAL_GETALLKEYS = "GALLKEYS";      //获取所有的键
    public static final String GLOBAL_BGSAVE = "BGSAVE";      //后台经行数据持久化

    //
    public static final String GLOBAL_FULL_SYNC = "FSYNC";      // 全同步
    public static final String GLOBAL_PART_SYNC = "PSYNC";      // 部分同步

    //String的命令 STR
    public static final String STR_SET = "STRSET";         //设置字符串键的值
    public static final String STR_GET = "STRGET";         //获取字符串键的值
    public static final String STR_APPEND = "STRAPPEND";    //在字符串的末尾追加
    public static final String STR_LEN = "STRLEN";         //返回当前键对应值的长度

    //List的命令 LIST
    public static final String LISTL_PUSH = "LLPUSH";      //从左边插入
    public static final String LISTR_PUSH = "LRPUSH";      //从右边插入
    public static final String LIST_POP = "LPOP";          //弹出列表的第一个元素
    public static final String LIST_LEN = "LLEN";           //返回当前列表的长度
    public static final String LIST_GET = "LGET";            //获取列表的值 一个参数 或者两个参数
    public static final String LIST_REMOVE = "LREMOVE";      //删除指定编号的值
    public static final String LIST_INSERT = "LINSERT";     //在指定位置插入一个值
    public static final String LIST_REPLACE = "LREPLACE";    //替换指定位置的值

    //Set的命令 SET
    public static final String SET_ADD = "SADD";          //往集合中添加值
    public static final String SET_GET = "SGET";          //获取集合的值 一个参数
    public static final String SET_INTER = "SINTER";          //获取两个集合的交集
    public static final String SET_UNION = "SUNION";          //获取两个集合的并集
    public static final String SET_DIFFER = "SDIFFER";          //获取两个集合的差集
    public static final String SET_POP = "SPOP";          //随机弹出一个集合中的值
    public static final String SET_REMOVE = "SREMOVE";          //移除集合中指定的值
    public static final String SET_LEN = "SLEN";          //获取几个中元素的个数

    //Map的命令 MAP
    public static final String MAP_PUT = "MPUT";          //往映射中添加值
    public static final String MAP_GET = "MGET";          //获取当前映射中的KEY对应所有的域和值 获取KEY对应的FIELD对应的VALUE
    public static final String MAP_REMOVE = "MREMOVE";          //从映射中删除一个或者多个域
    public static final String MAP_FIELDS = "MFIELDS";          //获取当前 KEY 的所有的 FIELD
    public static final String MAP_LEN = "MLEN";          // 获取当前的 KEY 的域的个数

    //JSON的命令 JSON
    public static final String JSON_SET = "JSET";           //往JSON中添加JSON值
    public static final String JSON_GET = "JGET";           //获取JSON某一成员的值
    public static final String JSON_ADD = "JADD";           //往数组里面添加数据
    public static final String JSON_REMOVE = "JREMOVE";           //移除JSON某一成员的值
    public static final String JSON_PUT = "JPUT";           //添加非JSON值

    /**
     * 判断相应的命令是不是从服务器命令
     *
     * @param key
     * @return
     */
    public static boolean isSlaveServerKey(String key) {
        return GLOBAL_FULL_SYNC.equals(key) || GLOBAL_PART_SYNC.equals(key);
    }

    /**
     * 判断相应的命令是不是全局命令
     */
    public static boolean isGlobalKey(String key) {

        return GLOBAL_REMOVE.equals(key) || GLOBAL_RENAME.equals(key) ||
                GLOBAL_SETTTL.equals(key) || GLOBAL_GETTTL.equals(key) ||
                GLOBAL_GETTYPE.equals(key) || GLOBAL_GETALLKEYS.equals(key) ||
                GLOBAL_GETTYPE.equals(key) || GLOBAL_GETALLKEYS.equals(key) ||
                GLOBAL_BGSAVE.equals(key);

    }

    /**
     * 判断相应的命令是不是字符串命令
     */
    public static boolean isStrKey(String key) {
        return key.equals(STR_SET) || key.equals(STR_APPEND) || key.equals(STR_LEN) || key.equals(STR_GET);
    }

    /**
     * 判断相应的命令是不是列表命令（LIST）
     */
    public static boolean isListKey(String key) {

        return key.equals(LIST_GET) || key.equals(LIST_INSERT) ||
                key.equals(LISTL_PUSH) || key.equals(LISTR_PUSH) ||
                key.equals(LIST_POP) || key.equals(LIST_LEN) ||
                key.equals(LIST_REMOVE) || key.equals(LIST_REPLACE);

    }

    /**
     * 判断相应的命令是不是集合命令（SET）
     */
    public static boolean isSetKey(String key) {

        return key.equals(SET_ADD) || key.equals(SET_GET) ||
                key.equals(SET_INTER) || key.equals(SET_UNION) ||
                key.equals(SET_POP) || key.equals(SET_REMOVE) ||
                key.equals(SET_LEN) || key.equals(SET_DIFFER);
    }

    /**
     * 判断相应的命令是不是映射命令（SET）
     */
    public static boolean isMapKey(String key) {

        return MAP_PUT.equals(key) || MAP_GET.equals(key) ||
                MAP_FIELDS.equals(key) || MAP_REMOVE.equals(key) ||
                MAP_LEN.equals(key);
    }

    public static boolean isJsonKey(String key) {

        return JSON_SET.equals(key) || JSON_GET.equals(key) ||
                JSON_REMOVE.equals(key) || JSON_PUT.equals(key) ||
                JSON_ADD.equals(key);

    }

    /**
     * 判断当前命令是不是读命令
     *
     * @param key
     * @return
     */
    public static boolean isReadingKey(String key) {
        return !isWritingKey(key);
    }

    /**
     * 判断当前命令是不是写命令
     *
     * @param key
     * @return
     */
    public static boolean isWritingKey(String key) {
        return (GLOBAL_REMOVE.equals(key) || GLOBAL_SETTTL.equals(key) || GLOBAL_RENAME.equals(key)) ||
                (STR_SET.equals(key) || STR_APPEND.equals(key)) ||
                (LISTL_PUSH.equals(key) || LISTR_PUSH.equals(key) || LIST_INSERT.equals(key) || LIST_POP.equals(key) || LIST_REMOVE.equals(key) || LIST_REPLACE.equals(key)) ||
                (SET_ADD.equals(key) || SET_POP.equals(key) || SET_REMOVE.equals(key)) ||
                (MAP_PUT.equals(key) || MAP_REMOVE.equals(key)) ||
                (JSON_SET.equals(key) || JSON_PUT.equals(key) || JSON_REMOVE.equals(key) ||
                        JSON_ADD.equals(key));
    }

}
