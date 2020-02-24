package cn.qd.peiwen.pwsocket.client.listener;

import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GenericFutureListener;

public class ReleaseListener implements GenericFutureListener<DefaultPromise<?>> {
    private PWSocketCilent client;

    public ReleaseListener(PWSocketCilent client) {
        this.client = client;
    }

    @Override
    public void operationComplete(DefaultPromise promise) throws Exception {
        this.client.onReleaseOperationCompleted(promise);
    }
}
