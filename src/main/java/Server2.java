

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * Create by yster@foxmail.com 2018/10/11 17:44
 */
public class Server2 {


    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public static void main(String[] args) throws IOException {
        new Server2().startServer();
    }


    private void startServer() throws IOException {
        selector = SelectorProvider.provider().openSelector();
        serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress isa = new InetSocketAddress(8100);
        serverSocketChannel.bind(isa);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            System.out.println("select key size "+selector.keys().size());

            selector.select(1000);
            Set<SelectionKey> keys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = keys.iterator();


            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                iterator.remove();
                //链接
                if (key.isAcceptable()) {
                    System.out.println("服务端：收到一个链接请求");
                    doAcceptable(key);
                } else if (key.isReadable()) {
                    System.out.println("服务端：收到一个读取请求");
                    doReadable(key);
                }

            }

        }
    }

    private void doReadable(SelectionKey key) {

        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);


        try {
            int readCount = channel.read(buffer);
            buffer.flip();
            if (readCount > 0) {
                System.out.println(new java.lang.String(buffer.array(), 0, buffer.limit()));

                System.out.println("服务端：开始写入信息");
                buffer.clear();
                buffer.put("我爱你\n".getBytes());
                buffer.flip();
                channel.write(buffer);
            }


        } catch (IOException e) {
            System.out.println("服务端：获取读取请求的时候发生了异常关闭链接");
            e.printStackTrace();
            key.cancel();
        }
    }

    private void doAcceptable(SelectionKey key) {


        ServerSocketChannel channel = (ServerSocketChannel) key.channel();

        try {
            SocketChannel accept = channel.accept();
            accept.configureBlocking(false);
            accept.register(selector, SelectionKey.OP_READ);
            System.out.println("服务端：收到一个客户端到的链接:" + accept.socket().getInetAddress());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}