package cn.qd.peiwen.pwsocket.client;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import cn.qd.peiwen.pwlogger.PWLogger;
import cn.qd.peiwen.pwsocket.client.listener.ConnectionListener;
import cn.qd.peiwen.pwsocket.client.listener.InitializerListener;
import cn.qd.peiwen.pwsocket.client.listener.MessageListener;
import cn.qd.peiwen.pwsocket.client.listener.ReleaseListener;
import cn.qd.peiwen.pwtools.EmptyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;

public class PWSocketCilent {
    private int state;
    private int port = 0;
    private int timeout = 0;
    private final String name;
    private String host = null;
    private boolean enable = false;

    private Channel channel;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;
    private WeakReference<PWSocketClientListener> listener;

    public static final int PW_SOCKET_CLIENT_STATE_INITIALIZED = 0;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setListener(PWSocketClientListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    public synchronized void init() {
        if (this.canInitialize()) {
            this.bootstrap = new Bootstrap();
            this.eventLoopGroup = new NioEventLoopGroup();
            this.bootstrap.group(this.eventLoopGroup);
            this.bootstrap.channel(NioSocketChannel.class);
            this.bootstrap.handler(new InitializerListener(this));
            this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
            this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            this.changeSocketState(PW_SOCKET_CLIENT_STATE_INITIALIZED);
        }
    }

    public synchronized void enable() {
        if (!this.enable) {
            this.connect();
        }
    }

    public synchronized void disable() {
        if (this.enable) {
            this.disconnect();
        }
    }

    public synchronized void release() {
        if (this.canRelease()) {
            this.enable = false;
            this.changeSocketState(PW_SOCKET_CLIENT_STATE_RELEASEING);
            DefaultPromise promise = (DefaultPromise) this.eventLoopGroup.shutdownGracefully();
            promise.addListener(new ReleaseListener(this));
        }
    }

    public void write(Object msg) {
        if (this.canWrite()) {
            this.channel.write(msg);
        }
    }

    public void writeAndFlush(Object msg) {
        if (this.canWrite()) {
            this.channel.writeAndFlush(msg);
        }
    }

    public void reconnect() {
        PWLogger.e("PWSocket(" + name + ") will reconnect in two seconds");
        this.eventLoopGroup.schedule(new Runnable() {
            @Override
            public void run() {
                if (PWSocketCilent.this.canReconnect()) {
                    PWLogger.e("PWSocket(" + name + ") reconnect");
                    PWSocketCilent.this.connect();
                } else {
                    PWLogger.e("PWSocket(" + name + ") already disabled,can not reconnect");
                }
            }
        }, 2L, TimeUnit.SECONDS);
    }

    private void connect() {
        if (this.canConnect()) {
            this.enable = true;
            this.changeSocketState(PW_SOCKET_CLIENT_STATE_CONNECTING);
            if (this.timeout > 0) {
                this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.timeout);
            }
            ChannelFuture future = this.bootstrap.connect(this.host, this.port);
            future.addListener(new ConnectionListener(this));
            this.channel = future.channel();
        }
    }

    private void disconnect() {
        if (this.canDisconnect()) {
            this.enable = false;
            this.changeSocketState(PW_SOCKET_CLIENT_STATE_DISCONNECTING);
            this.channel.close();
        }
    }

    public void onInitChannel(SocketChannel channel) {
        PWLogger.e("PWSocket(" + this.name + ") init channel");
        channel.pipeline().addLast(new MessageListener(this));
    }

    public void onChannelActive() {
        PWLogger.e("PWSocket(" + this.name + ") channel active");
        this.changeSocketState(PW_SOCKET_CLIENT_STATE_CONNECTED);
    }

    public void onChannelInactive() {
        PWLogger.e("PWSocket(" + this.name + ") channel inactive");
        this.changeSocketState(PW_SOCKET_CLIENT_STATE_DISCONNECTED);
    }

    public void onReadTimeout(ChannelHandlerContext ctx) {
        PWLogger.e("PWSocket(" + this.name + ") read timeout");
        if (EmptyUtils.isNotEmpty(this.listener)) {
            this.listener.get().onSocketClientReadTimeout(this, ctx);
        }
    }

    public void onWriteTimeout(ChannelHandlerContext ctx) {
        PWLogger.e("PWSocket(" + this.name + ") write timeout");
        if (EmptyUtils.isNotEmpty(this.listener)) {
            this.listener.get().onSocketClientWriteTimeout(this, ctx);
        }
    }

    public void onConnectOperationCompleted(ChannelFuture future) {
        PWLogger.e("PWSocket(" + this.name + ") connect operation completed:" + future.isSuccess());
        if (!future.isSuccess()) {
            this.changeSocketState(PW_SOCKET_CLIENT_STATE_DISCONNECTED);
        }
    }

    public void onReleaseOperationCompleted(DefaultPromise promise) {
        PWLogger.e("PWSocket(" + this.name + ") release operation completed:" + promise.isSuccess());
        this.changeSocketState(PW_SOCKET_CLIENT_STATE_RELEASED);
    }

    private synchronized boolean canWrite() {
        return this.state == PW_SOCKET_CLIENT_STATE_CONNECTED;
    }

    private synchronized boolean canConnect() {
        return (this.state == PW_SOCKET_CLIENT_STATE_INITIALIZED) || (this.state == PW_SOCKET_CLIENT_STATE_DISCONNECTED);
    }

    public synchronized boolean canReconnect() {
        return this.enable;
    }

    private synchronized boolean canDisconnect() {
        return (this.state == PW_SOCKET_CLIENT_STATE_CONNECTING) || (this.state == PW_SOCKET_CLIENT_STATE_CONNECTED);
    }

    private synchronized boolean canInitialize() {
        return this.state == PW_SOCKET_CLIENT_STATE_RELEASED;
    }

    private synchronized boolean canRelease() {
        return (this.state != PW_SOCKET_CLIENT_STATE_RELEASEING) && (this.state != PW_SOCKET_CLIENT_STATE_RELEASED);
    }

    private synchronized boolean isReleaseing() {
        return (this.state == PW_SOCKET_CLIENT_STATE_RELEASEING);
    }

    private synchronized void changeSocketState(int state) {
        if (this.state == state) {
            return;
        }
        switch (state) {
            case PW_SOCKET_CLIENT_STATE_INITIALIZED:
                this.state = state;
                PWLogger.e("PWSocket(" + name + ") state: INITIALIZED");
                if (EmptyUtils.isNotEmpty(this.listener)) {
                    this.listener.get().onSocketClientInitialized(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_CONNECTING:
                this.state = state;
                PWLogger.e("PWSocket(" + name + ") state: CONNECTING");
                if (EmptyUtils.isNotEmpty(this.listener)) {
                    this.listener.get().onSocketClientConnecting(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_CONNECTED:
                this.state = state;
                PWLogger.e("PWSocket(" + name + ") state: CONNECTED");
                if (EmptyUtils.isNotEmpty(this.listener)) {
                    this.listener.get().onSocketClientConnected(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_DISCONNECTING:
                this.state = state;
                PWLogger.e("PWSocket(" + name + ") state: DISCONNECTING");
                if (EmptyUtils.isNotEmpty(this.listener)) {
                    this.listener.get().onSocketClientDisconnecting(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_DISCONNECTED:
                this.state = state;
                PWLogger.e("PWSocket(" + name + ") state: DISCONNECTED");
                if (EmptyUtils.isNotEmpty(this.listener)) {
                    this.listener.get().onSocketClientDisconnected(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_RELEASEING:
                this.state = state;
                PWLogger.e("PWSocket(" + name + ") state: RELEASEING");
                if (EmptyUtils.isNotEmpty(this.listener)) {
                    this.listener.get().onSocketClientReleaseing(this);
                }
                break;
            case PW_SOCKET_CLIENT_STATE_RELEASED:
                this.state = state;
                PWLogger.e("PWSocket(" + name + ") state: RELEASED");
                if (EmptyUtils.isNotEmpty(this.listener)) {
                    this.listener.get().onSocketClientReleased(this);
                }
                break;
        }
    }
}
