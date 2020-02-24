package cn.qd.peiwen.pwsocket.client;

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
}
