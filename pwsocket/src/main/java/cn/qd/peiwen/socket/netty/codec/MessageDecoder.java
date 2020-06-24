package cn.qd.peiwen.socket.netty.codec;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.qd.peiwen.socket.PWSocketCilent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MessageDecoder extends ByteToMessageDecoder {
    private WeakReference<PWSocketCilent> client;

    public MessageDecoder(PWSocketCilent client) {
        this.client = new WeakReference<>(client);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (null != this.client && null != this.client.get()) {
            this.client.get().onMessageDecode(ctx, in, out);
        }
    }
}