package cn.qd.peiwen.demo;

import androidx.appcompat.app.AppCompatActivity;
import cn.qd.peiwen.pwsocket.client.PWSocketCilent;
import cn.qd.peiwen.pwsocket.client.PWSocketClientListener;
import io.netty.channel.ChannelHandlerContext;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements PWSocketClientListener {

    private PWSocketCilent client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.client = new PWSocketCilent("DEMO");
        this.client.setHost("123.56.76.216");
        this.client.setPort(60000);
        this.client.setTimeout(5000);
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
                this.client.write("test");
                break;
            case R.id.button4://关闭连接
                this.client.disable();
                break;
            case R.id.button5://释放内存
                this.client.release();
                break;
        }
    }

    @Override
    public void onSocketClientInitialized(PWSocketCilent client) {

    }

    @Override
    public void onSocketClientConnecting(PWSocketCilent client) {

    }

    @Override
    public void onSocketClientConnected(PWSocketCilent client) {

    }

    @Override
    public void onSocketClientDisconnecting(PWSocketCilent client) {

    }

    @Override
    public void onSocketClientDisconnected(PWSocketCilent client) {
        if(this.client.canReconnect()) {
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
    public void onSocketClientReadTimeout(PWSocketCilent client, ChannelHandlerContext ctx) {

    }

    @Override
    public void onSocketClientWriteTimeout(PWSocketCilent client, ChannelHandlerContext ctx) {

    }

    @Override
    public void onSocketClientMessageReceived(PWSocketCilent client, ChannelHandlerContext ctx, Object msg) {

    }
}
