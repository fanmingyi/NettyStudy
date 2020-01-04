package aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerAcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {


    @Override
    public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {

        //继续接受下一个链接
        attachment.accept(attachment, this);
        try {
            System.out.println("服务端：收到一个链接" + result.getRemoteAddress() + ":::" + Thread.currentThread().getName());
//          new ServerReadCompletionHandler();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            result.read(byteBuffer, byteBuffer, new ServerReadCompletionHandler(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {

    }
}
