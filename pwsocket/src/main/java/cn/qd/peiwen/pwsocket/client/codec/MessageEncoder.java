package cn.qd.peiwen.pwsocket.client.codec;

import java.lang.ref.WeakReference;

import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import cn.qd.peiwen.pwtools.EmptyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Object> {
    private WeakReference<PWSocketCilent> client;

    public MessageEncoder(PWSocketCilent client) {
        this.client = new WeakReference<>(client);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (EmptyUtils.isNotEmpty(this.client)) {
            this.client.get().onMessageEncode(ctx, msg, out);
        }
    }
}

