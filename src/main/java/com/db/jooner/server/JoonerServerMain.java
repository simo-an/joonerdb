package com.db.jooner.server;

import com.db.jooner.common.ConfigInfo;
import com.db.jooner.object.JoonerObject;
import com.db.jooner.utils.ConfigFileUtils;
import com.db.jooner.utils.DefaultInfoUtils;
import com.db.jooner.utils.global.DataSaveUtils;
import com.db.jooner.utils.global.ExpireUtils;
import com.db.jooner.utils.global.JoonerMethodUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * 〈Jooner数据库的入口〉<br>
 *
 * @author 未绪
 * @time 2018/2/12 21:06
 */
public class JoonerServerMain {
//
//    //从服务器同步主服务器数据
//    private DataSyncThread dataSyncThread;

    public static void main(String[] args) {
        ConfigInfo configInfo = new ConfigInfo();
        try {   // 加载配置文件内容
            ConfigFileUtils.loadConfigFile(args[0], configInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            TCPReactor reactor = new TCPReactor(configInfo);
            reactor.checkExpiredKey();           //检测是否有过期键
            reactor.loadJDBData();               //加载数据库原先内容
            if (DefaultInfoUtils.ROLE_SLAVE.equalsIgnoreCase(configInfo.getRole())) {
                // 如果当前的服务器为从服务器 SLAVE
                // 连接到主服务器
                DataSyncThread dataSyncThread = new DataSyncThread();
                dataSyncThread.run();
            }
            reactor.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
