import aio.ServerAcceptCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * Create by yster@foxmail.com 2018/10/11 17:44
 */
public class AioServer {


    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public static void main(String[] args) throws IOException {
        new AioServer().startServer();

        try {
            TimeUnit.HOURS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws IOException {
        asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
        asynchronousServerSocketChannel.bind(new InetSocketAddress(8100));
        asynchronousServerSocketChannel.accept(asynchronousServerSocketChannel, new ServerAcceptCompletionHandler());
    }


}