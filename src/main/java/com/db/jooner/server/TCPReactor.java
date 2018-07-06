package com.db.jooner.server;

import com.db.jooner.common.ConfigInfo;
import com.db.jooner.utils.ConfigFileUtils;
import com.db.jooner.utils.global.DataSaveUtils;
import com.db.jooner.utils.global.ExpireUtils;
import com.db.jooner.utils.global.JoonerKeyUtils;
import com.db.jooner.utils.global.JoonerMethodUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 *
 * @author 未绪
 * @time 2018/5/4 19:37
 */
public class TCPReactor implements Runnable {

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;
    //检测过期键的定时器
    private Timer expireTimer;
    private ConfigInfo configInfo;

    public TCPReactor(ConfigInfo configInfo) throws IOException {
        this.configInfo = configInfo;
        // 初始化定时器
        expireTimer = new Timer();
        selector = Selector.open();
        // 新键NIO通道
        serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(Integer.parseInt(configInfo.getHostPort()));
        serverSocketChannel.socket().bind(address);             // 绑定 ServerSocketChannel 的地址
        serverSocketChannel.configureBlocking(false);           // 设置 ServerSocketChannel 为非阻塞
        // ServerSocketChannel 向 Selector 注册一个 OP_ACCEPT 事件，然后返回该通道的KEY
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor(serverSocketChannel, selector));
    }

    @Override
    public void run() {
        while(!Thread.interrupted()){
            // 当线程未被阻塞
            try {
                if(selector.select() == 0){  // 没有事件
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();  // 得到所有已经就绪的事件的KEY
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                doDispatch(iterator.next()); // 根据事件的key进行调度
                iterator.remove();
            }
        }
    }
    private void doDispatch(SelectionKey key){
        // 根据事件的KEY绑定对象新的线程
        Runnable runnable = (Runnable)key.attachment();
        if(runnable!=null){
            runnable.run();
        }
    }


    // 检查当前的环境中是否有过期数据
    public void checkExpiredKey() {
        expireTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ExpireUtils.checkExpiredKey();
            }
        }, 0, configInfo.getCheckExpiredTime() * 1000);
    }



    // 加载持久化的数据
    public void loadJDBData() {
        try {
            File rdb = new File(configInfo.getDbFileName());
            if (!rdb.exists()) {
                rdb.createNewFile();
            } else {
                Scanner jin = new Scanner(rdb);
                while (jin.hasNext()) {
                    JoonerMethodUtils.executeCmd(jin.nextLine());
                }
                jin.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //初始化数据持久化的时间
        DataSaveUtils.LATE_PERSISTENCE_DATE = new Date();
    }
}
