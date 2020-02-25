package cn.qd.peiwen.pwsocket.client.listener;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import cn.qd.peiwen.pwtools.EmptyUtils;
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
        if(EmptyUtils.isNotEmpty(this.client)) {
            this.client.get().onInitChannel(ch);
        }
    }
}
