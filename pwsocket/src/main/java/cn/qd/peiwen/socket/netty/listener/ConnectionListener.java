package cn.qd.peiwen.socket.netty.listener;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.socket.PWSocketCilent;
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
        if (null != this.client && null != this.client.get()) {
            this.client.get().onConnectOperationCompleted(future);
        }
    }
}
