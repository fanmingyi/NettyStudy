
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create by yster@foxmail.com 2018/10/11 17:44
 */
public class Server {
    private Selector selector;

    private ExecutorService tp = Executors.newCachedThreadPool();

    class HandleMsg implements Runnable {
        ByteBuffer byteBuffer;
        SelectionKey key;

        public HandleMsg(ByteBuffer byteBuffer, SelectionKey key) {
            this.byteBuffer = byteBuffer;
            this.key = key;
        }

        @Override
        public void run() {
            byteBuffer.flip();

            //byte[] bytes = new byte[byteBuffer.remaining()];
            //byteBuffer.get(bytes);
            //System.out.println(new String(bytes, 0, bytes.length));
            System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit()));

            //将此键的 interest 集合设置为给定值
            key.interestOps(SelectionKey.OP_WRITE);
            //强迫selector返回, 使尚未返回的第一个选择操作立即返回, 即取消selector.select()的阻塞
            selector.wakeup();
        }
    }

    private void doAccept(SelectionKey key) {
        //返回创建此键的通道
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel;
        try {
            //生成和客户端的通信的通道
            clientChannel = server.accept();
            //设置非阻塞模式
            clientChannel.configureBlocking(false);
            //注册选择器, 读就绪
            clientChannel.register(selector, SelectionKey.OP_READ);
            InetAddress clientAddress = clientChannel.socket().getInetAddress();
            System.out.println("连接到客户端, 客户端ip: " + clientAddress.getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            int readBytes = channel.read(byteBuffer);
            if (readBytes > 0) {
                tp.execute(new HandleMsg(byteBuffer, key));
            }
        } catch (IOException e) {
            //请求取消此键的通道到其选择器的注册
            key.cancel();
            if (key.channel() != null) {
                key.channel().close();
            }
            e.printStackTrace();
        }

    }

    private void doWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            byteBuffer.put("客户端，我服务端收到消息了".getBytes());
            byteBuffer.flip();
            channel.write(byteBuffer);
        } catch (Exception e) {
            key.channel();
            if (key.channel() != null) {
                key.channel().close();
            }
            e.printStackTrace();
        }
        //将此键的 interest 集合设置为给定值
        key.interestOps(SelectionKey.OP_READ);
    }

    private void startServer() throws IOException {
        selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //设置为非阻塞模式
        ssc.configureBlocking(false);

        InetSocketAddress isa = new InetSocketAddress(8100);
        ssc.socket().bind(isa);

        //让Selector为这个Channel服务, 接收连接继续事件，表示服务器监听到了客户连接，服务器可以接收这个连接了
        // ServerSocketChannel只有OP_ACCEPT可用，OP_CONNECT,OP_READ,OP_WRITE用于SocketChannel
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            //阻塞方法
            System.out.println("select key size "+selector.keys().size());
            selector.select(1000);
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();

                //避免重复处理相同的SelectionKey
                iterator.remove();

                //测试此键的通道是否已准备好接受新的套接字连接(socket连接)
                if (key.isAcceptable()) {
                    System.out.println("服务端：isAcceptable");

                    doAccept(key);
                    //此键是否有效&&此键的通道是否已准备好进行读取
                } else if (key.isValid() && key.isReadable()) {
                    System.out.println("服务端：isReadable");

                    doRead(key);
                    //此键是否有效&&此键的通道是否已准备好进行写入
                } else if (key.isValid() && key.isWritable()) {
                    System.out.println("服务端：isWritable");

                    doWrite(key);
                } else if (key.isValid() && key.isConnectable()) {
                    System.out.println("服务端：isConnectable");
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server().startServer();
    }
}