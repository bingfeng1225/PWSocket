package cn.qd.peiwen.demo;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import cn.qd.peiwen.pwlogger.PWLogger;
import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import cn.qd.peiwen.pwsocket.client.PWSocketClientListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

public class MainActivity extends AppCompatActivity implements PWSocketClientListener {

    private PWSocketCilent client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.client = new PWSocketCilent("DEMO");
        this.client.setHost("123.56.76.216");
        this.client.setPort(60000);
        this.client.setReadTimeout(10);
        this.client.setWriteTimeout(10);
        this.client.setConnectTimeout(5000);
        this.client.setListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button://初始化
                this.client.init();
                break;
            case R.id.button2://打开链接
                this.client.enable();
                break;
            case R.id.button3://发送数据
                this.client.writeAndFlush("test");
                break;
            case R.id.button4://关闭连接
                this.client.disable();
                break;
            case R.id.button5://释放内存
                this.client.release();
                break;
        }
    }

    private long time = 0;
    @Override
    public void onSocketClientInitialized(PWSocketCilent client) {

    }

    @Override
    public void onSocketClientConnecting(PWSocketCilent client) {
        this.time = System.currentTimeMillis();
    }

    @Override
    public void onSocketClientConnected(PWSocketCilent client) {

    }

    @Override
    public void onSocketClientDisconnecting(PWSocketCilent client) {

    }

    @Override
    public void onSocketClientDisconnected(PWSocketCilent client) {
        PWLogger.e(client + " connect time " + (System.currentTimeMillis() - this.time));
        if(this.client.isEnabled()) {
            this.client.reconnect();
        }
    }

    @Override
    public void onSocketClientReleaseing(PWSocketCilent client) {

    }

    @Override
    public void onSocketClientReleased(PWSocketCilent client) {

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

    }

    @Override
    public void onSocketClientWriteTimeout(PWSocketCilent client, ChannelHandlerContext ctx) {

    }

    @Override
    public void onSocketClientMessageReceived(PWSocketCilent client, ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    @Override
    public void onSocketClientMessageEncode(PWSocketCilent client, ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.toString().getBytes());
    }

    @Override
    public void onSocketClientMessageDecode(PWSocketCilent client, ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }
}
