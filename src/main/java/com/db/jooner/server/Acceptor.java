package com.db.jooner.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 〈接受客户端连接请求的线程〉<br>
 *
 * @author 未绪
 * @time 2018/5/4 19:57
 */
public class Acceptor implements Runnable {

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;

    public Acceptor(ServerSocketChannel channel, Selector selector) {
        this.serverSocketChannel = channel;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            //接受客户端的请求
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                //设置为非阻塞
                socketChannel.configureBlocking(false);
                // SocketChannel 向 selector 注册一个 OP_READ 事件 ，然后返回该通道的KEY
                SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
                // 使一个阻塞的Selector操作立即返回
                selector.wakeup();
                TCPHandler tcpHandler = new TCPHandler(selectionKey, socketChannel);
                // 给 KEY 添加一个 TCPHandle
                selectionKey.attach(tcpHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
