package cn.qd.peiwen.pwsocket.client.listener;

import java.util.List;

import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

public interface IPWSocketClientListener {
    void onSocketClientInitialized(PWSocketCilent client);

    void onSocketClientConnecting(PWSocketCilent client);

    void onSocketClientConnected(PWSocketCilent client);

    void onSocketClientDisconnecting(PWSocketCilent client);

    void onSocketClientDisconnected(PWSocketCilent client);

    void onSocketClientReleaseing(PWSocketCilent client);

    void onSocketClientReleased(PWSocketCilent client);

    boolean onSocketClientInitDecoder(PWSocketCilent client, SocketChannel channel);

    boolean onSocketClientInitEncoder(PWSocketCilent client, SocketChannel channel);

    void onSocketClientReadTimeout(PWSocketCilent client, ChannelHandlerContext ctx);

    void onSocketClientWriteTimeout(PWSocketCilent client, ChannelHandlerContext ctx);

    void onSocketClientMessageReceived(PWSocketCilent client, ChannelHandlerContext ctx, Object msg) throws Exception;

    void onSocketClientMessageEncode(PWSocketCilent client, ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception;

    void onSocketClientMessageDecode(PWSocketCilent client, ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;

}
