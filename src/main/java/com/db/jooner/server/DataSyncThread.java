package com.db.jooner.server;

import com.db.jooner.common.ClientInfo;
import com.db.jooner.common.ConfigInfo;
import com.db.jooner.object.DataInfo;
import com.db.jooner.utils.global.DataSaveUtils;
import com.db.jooner.utils.global.ExpireUtils;
import com.db.jooner.utils.global.JoonerKeyUtils;
import com.db.jooner.utils.global.JoonerMethodUtils;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 〈一句话功能简述〉<br>
 *
 * @author 未绪
 * @time 2018/5/15 21:51
 */
public class DataSyncThread implements Runnable {

    // 同步标志 1 全同步 2 部分同步
    public static int SYNC_FLAG = 1;

    // 同步命令
    public static String SYNC_CMD = "";

    private Timer getCmdTimer;

    private BufferedReader bufferedReader;

    // 当前同步标志 1 FSYNC 2 PSYNC
    private static int SYNC_FLAGE = 1;

    @Override
    public void run() {
        if (SYNC_FLAG == 1) {
            try {
                fullSync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                partSync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 在当前服务器是从服务器的情况下 向主服务器发送 FSYNC 命令
     *
     * @throws IOException
     */
    public void fullSync() throws IOException {
        // 向主服务器发送 FSYNC 命令 从服务器的端口信息等
        Socket socket;
        // 连接到主服务器
        socket = new Socket(ConfigInfo.masterIp, Integer.parseInt(ConfigInfo.masterPort));
        OutputStream outputStream = socket.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
        PrintWriter printWriter = new PrintWriter(outputStreamWriter, true);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // 发送请求 FSYNC
        printWriter.println(JoonerKeyUtils.GLOBAL_FULL_SYNC);
        // 清空本地持久化数据
        DataSaveUtils.deleteDBFile();
        // 获取服务器返回的结果
        String result;
        // 写进数据持久化文件
        checkExpiredKey();
    }

    // 检查当前的环境中是否有过期数据
    public void checkExpiredKey() {
        getCmdTimer = new Timer();
        getCmdTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                String result;
                try {
                    while (!((result = bufferedReader.readLine()) == null)) {
                        if (null != result && (!"".equals(result.trim()))) {
                            if (SYNC_FLAGE == 1) {
                                // FSYNC
                                DataSaveUtils.doDataSave(result);
                            }
                            JoonerMethodUtils.executeCmd(result);
                        } else {
                            if (SYNC_FLAGE == 1) {
                                SYNC_FLAGE = 2;
                            }

                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    /**
     * 在当前服务器是主服务器的情况下 向主服务器发送 PSYNC 命令
     *
     * @throws IOException
     */
    public void partSync() throws IOException {
        // 将 key 广播到所有的从库中去
        HashSet<ClientInfo> clientInfoInstance = DataInfo.getClientInfoInstance();
        Iterator<ClientInfo> it = clientInfoInstance.iterator();
        while (it.hasNext()) {
            ClientInfo clientInfo = it.next();
            if (clientInfo.getClientRole().equalsIgnoreCase(DataUtils.SLAVE_CLIENT)) {// 从
                SocketChannel socketChannel = clientInfo.getSocketChannel();
                //
                ByteBuffer byteBuffer = ByteBuffer.wrap(DataSyncThread.SYNC_CMD.getBytes());
                if (socketChannel.isConnected()) {
                    // 在已经连接的情况下
                    System.out.println(socketChannel.socket().getRemoteSocketAddress().toString());
                    while (byteBuffer.hasRemaining()) {
                        socketChannel.write(byteBuffer);    // 写回客户端
                    }
                }
            }
        }
    }
}
