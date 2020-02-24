package cn.qd.peiwen.pwsocket.client.listener;

import cn.qd.peiwen.pwlogger.PWLogger;
import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by nick on 2018/6/18.
 */

public class InitializerListener extends ChannelInitializer<SocketChannel> {
    private PWSocketCilent client;

    public InitializerListener(PWSocketCilent client) {
        super();
        this.client = client;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        this.client.onInitChannel(ch);
    }
}
