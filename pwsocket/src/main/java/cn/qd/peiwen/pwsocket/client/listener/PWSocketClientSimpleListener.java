package cn.qd.peiwen.pwsocket.client.listener;

import java.util.List;

import cn.qd.peiwen.pwlogger.PWLogger;
import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

public class PWSocketClientSimpleListener implements IPWSocketClientListener {
    @Override
    public void onSocketClientInitialized(PWSocketCilent client) {
        PWLogger.d("" + client + " initialized");
    }

    @Override
    public void onSocketClientConnecting(PWSocketCilent client) {
        PWLogger.d("" + client + " connecting");
    }

    @Override
    public void onSocketClientConnected(PWSocketCilent client) {
        PWLogger.d("" + client + " connected");
    }

    @Override
    public void onSocketClientDisconnecting(PWSocketCilent client) {
        PWLogger.d("" + client + " disconnecting");
    }

    @Override
    public void onSocketClientDisconnected(PWSocketCilent client) {
        PWLogger.d("" + client + " disconnected");
    }

    @Override
    public void onSocketClientReleaseing(PWSocketCilent client) {
        PWLogger.d("" + client + " releaseing");
    }

    @Override
    public void onSocketClientReleased(PWSocketCilent client) {
        PWLogger.d("" + client + " released");
    }

    @Override
    public boolean onSocketClientInitDecoder(PWSocketCilent client, SocketChannel channel) {
        return false;
    }

    @Override
    public boolean onSocketClientInitEncoder(PWSocketCilent client, SocketChannel channel) {
        return false;
    }

    @Override
    public void onSocketClientReadTimeout(PWSocketCilent client, ChannelHandlerContext ctx) {
        PWLogger.d("" + client + " read timeout");
    }

    @Override
    public void onSocketClientWriteTimeout(PWSocketCilent client, ChannelHandlerContext ctx) {
        PWLogger.d("" + client + " write timeout");
    }

    @Override
    public void onSocketClientMessageReceived(PWSocketCilent client, ChannelHandlerContext ctx, Object msg) throws Exception {
        PWLogger.d("" + client + " message received");
    }

    @Override
    public void onSocketClientMessageEncode(PWSocketCilent client, ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        PWLogger.d("" + client + " message encode");
    }

    @Override
    public void onSocketClientMessageDecode(PWSocketCilent client, ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        PWLogger.d("" + client + " message decode");
    }
}
