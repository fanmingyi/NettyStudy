import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

public class Client2 {


    private SocketChannel channel;
    private Selector selector;

    public static void main(String[] args) {
        Client2 client = new Client2();
        client.init();
    }

    private void init() {

        try {
            channel = SocketChannel.open();
            selector = SelectorProvider.provider().openSelector();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("127.0.0.1", 8100));
            channel.register(selector, SelectionKey.OP_CONNECT);

            while (true) {
                System.out.println("select key size " + selector.keys().size());
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {

                    SelectionKey key = iterator.next();

                    iterator.remove();

                    if (key.isReadable()) {
                        doReadable(key);
                    }
                    if (key.isConnectable()) {
                        doConnectable(key);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void doConnectable(SelectionKey key) {

        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()) {
            try {
                channel.finishConnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            channel.configureBlocking(false);
            String string = "你好服务器我是客户端\r\n";
            channel.write(ByteBuffer.wrap(string.getBytes()));
            SelectionKey register = channel.register(selector, SelectionKey.OP_READ);


            int interestOps = register.interestOps();
            boolean read = (interestOps & SelectionKey.OP_READ) == SelectionKey.OP_READ;
            boolean connect = (interestOps & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT;

            System.out.println("interestOps read " + read + " connect " + connect);
        } catch (IOException e) {
            e.printStackTrace();
            key.cancel();
            SelectableChannel channel1 = key.channel();

            if (channel1 != null) {
                try {
                    channel1.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    private void doReadable(SelectionKey key) {


        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        try {
            int read = channel.read(byteBuffer);

            byteBuffer.flip();
            System.out.println("客户端：收到服务端信息[" + read + " ]  read2  " +"    "+ new String(byteBuffer.array(), 0, byteBuffer.limit()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        byteBuffer.clear();
        byteBuffer.put("你\r\n".getBytes());
        byteBuffer.flip();
        try {
            channel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
