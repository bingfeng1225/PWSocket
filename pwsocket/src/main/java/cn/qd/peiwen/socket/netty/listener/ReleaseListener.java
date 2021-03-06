package cn.qd.peiwen.socket.netty.listener;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.socket.PWSocketCilent;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GenericFutureListener;

public class ReleaseListener implements GenericFutureListener<DefaultPromise<?>> {
    private WeakReference<PWSocketCilent> client;

    public ReleaseListener(PWSocketCilent client) {
        this.client = new WeakReference<>(client);
    }

    @Override
    public void operationComplete(DefaultPromise promise) throws Exception {
        if (null != this.client && null != this.client.get()) {
            this.client.get().onReleaseOperationCompleted(promise);
        }
    }
}
