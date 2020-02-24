package cn.qd.peiwen.pwsocket.client.listener;

import cn.qd.peiwen.pwlogger.PWLogger;
import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Created by nick on 2018/6/18.
 */

public class ConnectionListener implements ChannelFutureListener {
    private PWSocketCilent client;

    public ConnectionListener(PWSocketCilent client) {
        this.client = client;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        this.client.onConnectOperationCompleted(future);
    }
}
