import aio.ClientReadCompletionHandler;
import aio.ServerAcceptCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Create by yster@foxmail.com 2018/10/11 17:44
 */
public class AioClient {


    private AsynchronousSocketChannel asynchronousSocketChannel;

    public static void main(String[] args) throws IOException {
        new AioClient().startServer();

        try {
            TimeUnit.HOURS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws IOException {
        asynchronousSocketChannel = AsynchronousSocketChannel.open();
        asynchronousSocketChannel.connect(new InetSocketAddress("127.0.0.1", 8100), asynchronousSocketChannel, new CompletionHandler<Void, AsynchronousSocketChannel>() {
            @Override
            public void completed(Void result, AsynchronousSocketChannel attachment) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                Future<Integer> write = attachment.write(ByteBuffer.wrap("你好帅".getBytes()));

                try {
                    write.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                attachment.read(byteBuffer,byteBuffer,new ClientReadCompletionHandler(attachment));
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                try {
                    asynchronousSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}