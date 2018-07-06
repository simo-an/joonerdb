package com.db.jooner.utils.type;

import com.db.jooner.common.ClientInfo;
import com.db.jooner.object.DataInfo;
import com.db.jooner.object.JoonerObject;
import com.db.jooner.server.DataSyncThread;
import com.db.jooner.utils.DefaultInfoUtils;
import com.db.jooner.utils.global.DataSaveUtils;
import com.db.jooner.utils.global.JoonerKeyUtils;
import com.sun.org.apache.regexp.internal.RE;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 〈从服务器发送的命令处理工具〉<br>
 *
 * @author 未绪
 * @time 2018/5/15 21:18
 */
public class SlaveCmdUtils {

    private static HashMap<String, JoonerObject<Object>> joonerObject = DataInfo.getJoonerInstance();

    /**
     * 处理从服务器发送的命令的方法
     *
     * @param keys 拆分后的命名数组
     */
    public static Object handleSlaveCmd(String[] keys) {
        switch (keys[0]) {
            case JoonerKeyUtils.GLOBAL_PART_SYNC:
                return SlaveCmdUtils.partSync(keys[1]);
            case JoonerKeyUtils.GLOBAL_FULL_SYNC:
                return SlaveCmdUtils.fullSync();
            default:
                break;
        }
        return DefaultInfoUtils.DEFAULT_FAIL;
    }

    /***
     * 全部更新
     * @return JDB文件的内容
     */
    public static Object fullSync() {
        try {
            return DataSaveUtils.getDBFileContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DefaultInfoUtils.DEFAULT_EMPTY;
    }

    /***
     * 部分更新
     * @return JDB文件的内容
     */
    public static Object partSync(String key) {
        // 开启一个线程进行同步操作
        DataSyncThread.SYNC_FLAG = 2; // 部分同步
        DataSyncThread.SYNC_CMD = key + "\n";
        DataSyncThread syncThread = new DataSyncThread();
        syncThread.run();
        return DefaultInfoUtils.DEFAULT_SUCCESS;
    }
}
