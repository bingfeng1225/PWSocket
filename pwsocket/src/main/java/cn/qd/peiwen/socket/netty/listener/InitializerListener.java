package cn.qd.peiwen.socket.netty.listener;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.socket.PWSocketCilent;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by nick on 2018/6/18.
 */

public class InitializerListener extends ChannelInitializer<SocketChannel> {
    private WeakReference<PWSocketCilent> client;

    public InitializerListener(PWSocketCilent client) {
        this.client = new WeakReference<>(client);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        if (null != this.client && null != this.client.get()) {
            this.client.get().onInitChannel(ch);
        }
    }
}
