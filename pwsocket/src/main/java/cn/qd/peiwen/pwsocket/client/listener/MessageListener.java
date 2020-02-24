package cn.qd.peiwen.pwsocket.client.listener;

import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class MessageListener extends SimpleChannelInboundHandler {
    private PWSocketCilent cilent;

    public MessageListener(PWSocketCilent cilent) {
        this.cilent = cilent;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.cilent.onChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.cilent.onChannelInactive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.READER_IDLE) {
            this.cilent.onReadTimeout(ctx);
        }else if(event.state() == IdleState.WRITER_IDLE){
            this.cilent.onWriteTimeout(ctx);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
