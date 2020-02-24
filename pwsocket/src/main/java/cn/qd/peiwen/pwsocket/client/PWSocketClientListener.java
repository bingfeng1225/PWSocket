package cn.qd.peiwen.pwsocket.client;

import java.util.List;

import cn.qd.peiwen.pwlogger.PWLogger;
import cn.qd.peiwen.pwtools.EmptyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface PWSocketClientListener {
    void onSocketClientInitialized(PWSocketCilent client);

    void onSocketClientConnecting(PWSocketCilent client);

    void onSocketClientConnected(PWSocketCilent client);

    void onSocketClientDisconnecting(PWSocketCilent client);

    void onSocketClientDisconnected(PWSocketCilent client);

    void onSocketClientReleaseing(PWSocketCilent client);

    void onSocketClientReleased(PWSocketCilent client);

    void onSocketClientReadTimeout(PWSocketCilent client, ChannelHandlerContext ctx);

    void onSocketClientWriteTimeout(PWSocketCilent client, ChannelHandlerContext ctx);

    void onSocketClientMessageReceived(PWSocketCilent client, ChannelHandlerContext ctx, Object msg);

    void onSocketClientMessageEncode(PWSocketCilent client, ChannelHandlerContext ctx, Object msg, ByteBuf out);

    void onSocketClientMessageDecode(PWSocketCilent client, ChannelHandlerContext ctx, ByteBuf in, List<Object> out);
}
