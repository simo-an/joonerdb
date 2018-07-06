package com.db.jooner.utils.global;

import com.db.jooner.common.ConfigInfo;
import com.db.jooner.object.DataInfo;
import com.db.jooner.server.DataUtils;
import com.db.jooner.utils.DefaultInfoUtils;
import com.db.jooner.utils.type.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一些要使用的全局方法〉<br>
 *
 * @author 未绪
 * @time 2018/2/14 21:32
 */
public class JoonerMethodUtils {

    /**
     * 〈判断当前命令的类型〉<br>
     *
     * @param key 命令
     * @author 未绪
     * @time 2018/2/14 21:32
     */
    public static Object executeCmd(String key) {

        if (null == key || DefaultInfoUtils.DEFAULT_EMPTY.equals(key.trim())) {
            return DefaultInfoUtils.DEFAULT_EMPTY;
        }

        // 增加一次操作时间
        DataSaveUtils.OPERATION_TIMES++;

        key = key.trim().replaceAll("\\s+", " ");        //将当前的命令转化为大写字符串并进行相应的裁剪

        List<String> commandContent = JoonerMethodUtils.getContentArray(key);
        String[] keys = new String[commandContent.size()];
        commandContent.toArray(keys);
        keys[0] = keys[0].toUpperCase();
        if (keys.length > 1) {                  //防止有的命令只有一个键 FLUSHHALL etc.
            keys[1] = keys[1].toUpperCase();
            if (ExpireUtils.isExpired(keys[1])) {       //实现惰性删除
                GlobalCmdUtils.remove(keys[1]);
            }
        }
        // 如果当前服务器是主服务器[有从库]
        if (DefaultInfoUtils.ROLE_MASER.equalsIgnoreCase(ConfigInfo.role) && DataInfo.getClientInfoInstance().size() != 0) {
            // 如果当前命令是 写命令
            if (JoonerKeyUtils.isWritingKey(keys[0])) {
                // 将当前命令传播到所有的从服务器
                String[] tmpKeys = {JoonerKeyUtils.GLOBAL_PART_SYNC, key};
                SlaveCmdUtils.handleSlaveCmd(tmpKeys);
            }
        }
        if (JoonerKeyUtils.isGlobalKey(keys[0])) {            //判断命令的键
            return GlobalCmdUtils.handleGlobalCmd(keys);
        } else if (JoonerKeyUtils.isStrKey(keys[0])) {
            return StrCmdUtils.handleStrCmd(keys);
        } else if (JoonerKeyUtils.isListKey(keys[0])) {
            return ListCmdUtils.handleListCmd(keys);
        } else if (JoonerKeyUtils.isSetKey(keys[0])) {
            return SetCmdUtils.handleSetCmd(keys);
        } else if (JoonerKeyUtils.isMapKey(keys[0])) {
            return MapCmdUtils.handleMapCmd(keys);
        } else if (JoonerKeyUtils.isSlaveServerKey(keys[0])) {
            return SlaveCmdUtils.handleSlaveCmd(keys);
        } else if (JoonerKeyUtils.isJsonKey(keys[0])) {
            return JsonCmdUtils.handleJsonCmd(keys[0], keys[1], key, keys);
        }

        return DefaultInfoUtils.DEFAULT_FAIL;

    }

    /**
     * Grance T\"om "Bob Steave" Lindy "Su San" "Tom's Feare" "Array\" Creater\""
     * =》
     * Grance
     * T"om
     * Bob Steave
     * Lindy
     * Su San
     * Tom's Feare
     * Array" Creater"
     *
     * @param content
     * @return
     */
public static List<String> getContentArray(String content) {

    List<String> contentList = new ArrayList<>();
    String contentItem = "";
    boolean hasQuote = false;

    for (int i = 0; i < content.length(); i++) {
        char item = content.charAt(i);
        if (item == '"') {
            if (hasQuote) {           // 如果当前引号前面有引号
                hasQuote = false;
                if (!contentItem.equals("")) {
                    contentList.add(contentItem);
                    contentItem = "";
                }
            } else {
                hasQuote = true;
            }
        } else if (item == '\\') {
            i++;
            contentItem += content.charAt(i);
        } else if (item == ' ') {
            if (hasQuote) {
                contentItem += item;
            } else {
                if (!contentItem.equals("")) {
                    contentList.add(contentItem);
                    contentItem = "";
                }
            }
        } else {
            contentItem += item;
        }
        if (i == content.length() - 1) {
            if (!contentItem.equals("")) {
                contentList.add(contentItem);
                contentItem = "";
            }
        }
    }
    return contentList;
}
}
