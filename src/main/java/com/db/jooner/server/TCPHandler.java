package com.db.jooner.server;

import com.db.jooner.common.ClientInfo;
import com.db.jooner.common.ConfigInfo;
import com.db.jooner.object.DataInfo;
import com.db.jooner.utils.global.DataSaveUtils;
import com.db.jooner.utils.global.JoonerKeyUtils;
import com.db.jooner.utils.global.JoonerMethodUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 〈Handle 线程〉<br>
 *
 * @author 未绪
 * @time 2018/5/4 20:17
 */
public class TCPHandler implements Runnable {

    private final SelectionKey selectionKey;
    private final SocketChannel socketChannel;

    private int state;

    // 写回客户端的数据
    private String resultInfo = "";

    public TCPHandler(SelectionKey selectionKey, SocketChannel socketChannel) {
        this.selectionKey = selectionKey;
        this.socketChannel = socketChannel;
        this.state = DataUtils.READING;                     // 初始状态设置为Reading
    }

    @Override
    public void run() {
        try {
            if (state == DataUtils.READING) {
                doRead(); // 读取客户端数据
            } else {
                doSend(); //   往客户端写会数据
            }
        } catch (IOException e) {
            closeChannel();
        }
    }

    private void closeChannel() {
        try {
            selectionKey.cancel();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void doRead() throws IOException {
        // non-blocking 不可用 Readers
        byte[] bytes = new byte[1024];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        // 读取字符串
        int bytesNum = socketChannel.read(byteBuffer);

        if (bytesNum == -1) {
            // 有客户端关闭连接
            closeChannel();
            return;
        }
        String cmd = new String(bytes, 0, bytesNum);

        // 判断当前命令是不是从库发过来的命令FSYNC
        if (cmd.startsWith(JoonerKeyUtils.GLOBAL_FULL_SYNC)) {
            // 当前的 socetChannel 是从服务器
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setSocketChannel(socketChannel);
            clientInfo.setClientRole(DataUtils.SLAVE_CLIENT);
            DataInfo.getClientInfoInstance().add(clientInfo);
        }

        // 获取客户端的输入数据
        Object object = JoonerMethodUtils.executeCmd(cmd);  //处理命令 并传入放置数据的容器
        resultInfo = object + "";
        // 检测是否达到数据持久化的条件
        DataSaveUtils.dataSave(ConfigInfo.dataSave);

        state = DataUtils.SENDING; // 改变当前的状态
        selectionKey.interestOps(SelectionKey.OP_WRITE);  // 通过 KEY 改变事件注册的通道
        selectionKey.selector().wakeup();

    }

    private void doSend() throws IOException {

        resultInfo += "\n";
        ByteBuffer byteBuffer = ByteBuffer.wrap(resultInfo.getBytes());

        while (byteBuffer.hasRemaining()) {
            socketChannel.write(byteBuffer);    // 写回客户端
        }
        state = DataUtils.READING;
        resultInfo = "";
        selectionKey.interestOps(SelectionKey.OP_READ);
        selectionKey.selector().wakeup();
    }
}
