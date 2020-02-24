package cn.qd.peiwen.pwsocket.client.codec;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import cn.qd.peiwen.pwtools.EmptyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageDecoder extends ByteToMessageDecoder {
    private WeakReference<PWSocketCilent> client;

    public MessageDecoder(PWSocketCilent client) {
        this.client = new WeakReference<>(client);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (EmptyUtils.isNotEmpty(this.client)) {
            this.client.get().onMessageDecode(ctx, in, out);
        }
    }
}