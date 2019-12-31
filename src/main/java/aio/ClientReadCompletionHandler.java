package aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ClientReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    AsynchronousSocketChannel channel;

    public ClientReadCompletionHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("客户端：读取到 result " + result);

        if (result == -1) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        try {
            String req = new String(body, "UTF-8");
            System.out.println("客户端收到信息 : " + req);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        Future<Integer> write = channel.write(ByteBuffer.wrap("我是客户端我就要会信息".getBytes()));
        try {
            write.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        attachment.clear();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.read(attachment, attachment, this);

    }


    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

        try {
            System.out.println("异常出现了");

            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
