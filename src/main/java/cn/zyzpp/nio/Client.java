package cn.zyzpp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 * Create by yster@foxmail.com 2018/10/11 16:36
 */
public class Client {

    private Selector selector;

    public void init(String ip, int port) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        this.selector = SelectorProvider.provider().openSelector();
        channel.connect(new InetSocketAddress(ip, port));
        //连接就绪，表示客户与服务器的连接已经建立成功
        channel.register(selector, SelectionKey.OP_CONNECT);
    }

    public void connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            //如果正在连接, 则完成连接
            if(channel.isConnectionPending()) {
                //完成套接字通道的连接过程
                channel.finishConnect();
            }
            channel.configureBlocking(false);
            channel.write(ByteBuffer.wrap(new String("hello server, I am client!\r\n").getBytes()));
            //注册选择器，读就绪
            channel.register(this.selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
            channel.close();
            key.selector().close();
        }

    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        //创建读取的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        try {
            channel.read(buffer);
            byte[] data = buffer.array();
            String msg = new String(data).trim();
            System.out.println("客户端收到信息: " + msg);
        } catch (IOException e) {
            channel.close();
            key.selector().close();
        }
    }

    public void working() throws IOException {
        while(true) {
            if(!selector.isOpen()) {
                break;
            }
            selector.select();
            Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
            while(ite.hasNext()) {
                SelectionKey key = ite.next();
                ite.remove();
                //连接事件发生
                if(key.isConnectable()) {
                    connect(key);
                } else if(key.isReadable()) {
                    read(key);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Client c = new Client();
        c.init("127.0.0.1", 8080);
        c.working();
    }

}