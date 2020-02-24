package cn.qd.peiwen.pwsocket.client.listener;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import cn.qd.peiwen.pwtools.EmptyUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GenericFutureListener;

public class ReleaseListener implements GenericFutureListener<DefaultPromise<?>> {
    private WeakReference<PWSocketCilent> client;

    public ReleaseListener(PWSocketCilent client) {
        this.client = new WeakReference<>(client);
    }

    @Override
    public void operationComplete(DefaultPromise promise) throws Exception {
        if(EmptyUtils.isNotEmpty(this.client)){
            this.client.get().onReleaseOperationCompleted(promise);
        }
    }
}
