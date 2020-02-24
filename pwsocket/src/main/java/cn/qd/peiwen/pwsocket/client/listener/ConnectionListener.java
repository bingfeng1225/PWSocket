package cn.qd.peiwen.pwsocket.client.listener;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.pwlogger.PWLogger;
import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import cn.qd.peiwen.pwtools.EmptyUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Created by nick on 2018/6/18.
 */

public class ConnectionListener implements ChannelFutureListener {
    private WeakReference<PWSocketCilent> client;

    public ConnectionListener(PWSocketCilent client) {
        this.client = new WeakReference<>(client);
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if(EmptyUtils.isNotEmpty(this.client)) {
            this.client.get().onConnectOperationCompleted(future);
        }
    }
}
