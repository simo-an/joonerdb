package com.db.jooner.common;

import java.nio.channels.SocketChannel;

/**
 * 〈连接到服务器的客户端信息〉<br>
 *
 * @author 未绪
 * @time 2018/5/16 16:12
 */
public class ClientInfo {
    private SocketChannel socketChannel;
    private String clientRole;

    @Override
    public int hashCode() {
        // 用于客户端的特殊性 我们认为端口一样的客户端是同一客户端
        return socketChannel.socket().getPort();
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public String getClientRole() {
        return clientRole;
    }

    public void setClientRole(String clientRole) {
        this.clientRole = clientRole;
    }
}
