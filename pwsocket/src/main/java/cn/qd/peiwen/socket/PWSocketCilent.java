package cn.qd.peiwen.socket;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.qd.peiwen.socket.netty.codec.MessageDecoder;
import cn.qd.peiwen.socket.netty.codec.MessageEncoder;
import cn.qd.peiwen.socket.netty.listener.ConnectionListener;
import cn.qd.peiwen.socket.netty.listener.InitializerListener;
import cn.qd.peiwen.socket.netty.listener.MessageListener;
import cn.qd.peiwen.socket.netty.listener.ReleaseListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;

public class PWSocketCilent {
    private int state;
    private int port = 0;
    private final String name;
    private String host = null;
    private int readTimeout = 0;
    private int writeTimeout = 0;
    private int connectTimeout = 0;
    private boolean enable = false;

    private Channel channel;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;
    private WeakReference<IPWSocketClientListener> listener;

    public static final int PW_SOCKET_CLIENT_STATE_IDLE = 0;
    public static final int PW_SOCKET_CLIENT_STATE_CONNECTING = 1;
    public static final int PW_SOCKET_CLIENT_STATE_CONNECTED = 2;
    public static final int PW_SOCKET_CLIENT_STATE_DISCONNECTING = 3;
    public static final int PW_SOCKET_CLIENT_STATE_DISCONNECTED = 4;
    public static final int PW_SOCKET_CLIENT_STATE_RELEASEING = 5;
    public static final int PW_SOCKET_CLIENT_STATE_RELEASED = 6;

    public PWSocketCilent(String name) {
        this.name = name;
        this.state = PW_SOCKET_CLIENT_STATE_RELEASED;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setListener(IPWSocketClientListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    public void init() {
        if (this.isReleased()) {
            this.bootstrap = new Bootstrap();
            this.eventLoopGroup = new NioEventLoopGroup();
            this.bootstrap.group(this.eventLoopGroup);
            this.bootstrap.channel(NioSocketChannel.class);
            this.bootstrap.handler(new InitializerListener(this));
            this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
            this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            this.changeSocketState(PW_SOCKET_CLIENT_STATE_IDLE);
        }
    }

    public void enable() {
        if (this.isInitialized() && !this.enable) {
            this.enable = true;
            this.connect();
        }
    }

    public void disable() {
        if (this.isInitialized() && this.enable) {
            this.enable = false;
            this.disconnect();
        }
    }

    public void release() {
        if (this.isInitialized()) {
            this.changeSocketState(PW_SOCKET_CLIENT_STATE_RELEASEING);
            DefaultPromise promise = (DefaultPromise) this.eventLoopGroup.shutdownGracefully();
            promise.addListener(new ReleaseListener(this));
        }
    }

    public void write(Object msg) {
        if (this.isWriteAble()) {
            this.channel.write(msg);
        }
    }

    public void writeAndFlush(Object msg) {
        if (this.isWriteAble()) {
            this.channel.writeAndFlush(msg);
        }
    }

    private void connect() {
        if (this.isConnectAble()) {
            this.changeSocketState(PW_SOCKET_CLIENT_STATE_CONNECTING);
            if (this.connectTimeout > 0) {
                this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.connectTimeout);
            }
            ChannelFuture future = this.bootstrap.connect(this.host, this.port);
            this.channel = future.channel();
            future.addListener(new ConnectionListener(this));
        }
    }

    public void reconnect() {
        this.eventLoopGroup.schedule(new Runnable() {
            @Override
            public void run() {
                if (PWSocketCilent.this.isEnabled()) {
                    PWSocketCilent.this.connect();
                }
            }
        }, 2L, TimeUnit.SECONDS);
    }

    private void disconnect() {
        if (this.isDisonnectAble()) {
            this.changeSocketState(PW_SOCKET_CLIENT_STATE_DISCONNECTING);
            this.channel.close();
        }
    }

    public boolean isEnabled() {
        return this.enable;
    }

    private boolean isReleased() {
        return this.state == PW_SOCKET_CLIENT_STATE_RELEASED;
    }

    private boolean isInitialized() {
        return this.state != PW_SOCKET_CLIENT_STATE_RELEASEING
                && this.state != PW_SOCKET_CLIENT_STATE_RELEASED;
    }

    private boolean isWriteAble() {
        return this.state == PW_SOCKET_CLIENT_STATE_CONNECTED;
    }

    private boolean isConnectAble() {
        return (this.state == PW_SOCKET_CLIENT_STATE_IDLE)
                || (this.state == PW_SOCKET_CLIENT_STATE_DISCONNECTED);
    }

    private boolean isDisonnectAble() {
        return (this.state == PW_SOCKET_CLIENT_STATE_CONNECTING)
                || (this.state == PW_SOCKET_CLIENT_STATE_CONNECTED);
    }

    private synchronized void changeSocketState(int state) {
        if (this.state == state) {
            return;
        }
        switch (state) {
            case PW_SOCKET_CLIENT_STATE_IDLE:
                this.state = state;
                this.enable = false;
                if (null != this.listener && null != this.listener.get()) {
                    this.listener.get().onSocketClientInitialized(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_CONNECTING:
                this.state = state;
                if (null != this.listener && null != this.listener.get()) {
                    this.listener.get().onSocketClientConnecting(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_CONNECTED:
                this.state = state;
                if (null != this.listener && null != this.listener.get()) {
                    this.listener.get().onSocketClientConnected(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_DISCONNECTING:
                this.state = state;
                if (null != this.listener && null != this.listener.get()) {
                    this.listener.get().onSocketClientDisconnecting(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_DISCONNECTED:
                this.state = state;
                if (null != this.listener && null != this.listener.get()) {
                    this.listener.get().onSocketClientDisconnected(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_RELEASEING:
                this.state = state;
                this.enable = false;
                if (null != this.listener && null != this.listener.get()) {
                    this.listener.get().onSocketClientReleaseing(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_RELEASED:
                this.state = state;
                if (null != this.listener && null != this.listener.get()) {
                    this.listener.get().onSocketClientReleased(this);
                }
                break;
        }
    }

    public void onInitChannel(SocketChannel channel) {
        /**
         *  用于用户自定义编解码器
         *  如果用户自定义编解码器,返回True
         *  如果不定义返回False,使用默认编解码器
         *  使用默认编解码器需实现:
         *  onMessageDecode：将接收信息解析
         *  onMessageEncode：将发送信息封装成字节流写入
         */
        boolean result = false;
        if (null != this.listener && null != this.listener.get()) {
            result = this.listener.get().onSocketClientInitDecoder(this, channel);
        }
        if (!result) {
            channel.pipeline().addLast(new MessageDecoder(this));
        }

        if (null != this.listener && null != this.listener.get()) {
            result = this.listener.get().onSocketClientInitEncoder(this, channel);
        }
        if (!result) {
            channel.pipeline().addLast(new MessageEncoder(this));
        }
        channel.pipeline().addLast(new MessageListener(this));
        channel.pipeline().addFirst(new IdleStateHandler(this.readTimeout, this.writeTimeout, 0));
    }

    public void onChannelActive() {
        this.changeSocketState(PW_SOCKET_CLIENT_STATE_CONNECTED);
    }

    public void onChannelInactive() {
        this.changeSocketState(PW_SOCKET_CLIENT_STATE_DISCONNECTED);
    }

    public void onReadTimeout(ChannelHandlerContext ctx) {
        if (null != this.listener && null != this.listener.get()) {
            this.listener.get().onSocketClientReadTimeout(this, ctx);
        }
    }

    public void onWriteTimeout(ChannelHandlerContext ctx) {
        if (null != this.listener && null != this.listener.get()) {
            this.listener.get().onSocketClientWriteTimeout(this, ctx);
        }
    }

    public void onExceptionCaught(Throwable throwable){
        if (null != this.listener && null != this.listener.get()) {
            this.listener.get().onSocketClientExceptionCaught(this, throwable);
        }
    }

    public void onConnectOperationCompleted(ChannelFuture future) {
        if (!future.isSuccess()) {
            this.changeSocketState(PW_SOCKET_CLIENT_STATE_DISCONNECTED);
        }
    }

    public void onReleaseOperationCompleted(DefaultPromise promise) {
        this.changeSocketState(PW_SOCKET_CLIENT_STATE_RELEASED);
    }

    public void onChannelMessageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null != this.listener && null != this.listener.get()) {
            this.listener.get().onSocketClientMessageReceived(this, ctx, msg);
        }
    }

    public void onMessageEncode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (null != this.listener && null != this.listener.get()) {
            this.listener.get().onSocketClientMessageEncode(this, ctx, msg, out);
        }
    }

    public void onMessageDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (null != this.listener && null != this.listener.get()) {
            this.listener.get().onSocketClientMessageDecode(this, ctx, in, out);
        }
    }

    @Override
    public String toString() {
        return "PWSocket(" + this.name + ")";
    }
}
