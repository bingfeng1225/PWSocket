package cn.qd.peiwen.socket.netty.listener;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.socket.PWSocketCilent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class MessageListener extends SimpleChannelInboundHandler {
    private WeakReference<PWSocketCilent> client;

    public MessageListener(PWSocketCilent client) {
        this.client = new WeakReference<>(client);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        if (null != this.client && null != this.client.get()) {
            this.client.get().onExceptionCaught(cause);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if (null != this.client && null != this.client.get()) {
            this.client.get().onChannelActive();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (null != this.client && null != this.client.get()) {
            this.client.get().onChannelInactive();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.READER_IDLE) {
            if (null != this.client && null != this.client.get()) {
                this.client.get().onReadTimeout(ctx);
            }
        }else if(event.state() == IdleState.WRITER_IDLE){
            if (null != this.client && null != this.client.get()) {
                this.client.get().onWriteTimeout(ctx);
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null != this.client && null != this.client.get()) {
            this.client.get().onChannelMessageReceived(ctx, msg);
        }
    }
}
