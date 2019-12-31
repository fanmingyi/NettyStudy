package aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ServerReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    AsynchronousSocketChannel channel;

    public ServerReadCompletionHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        System.out.println("服务端：读取到 result " + result);

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
            System.out.println("The time server receive order : " + req);
            Future<Integer> write = channel.write(ByteBuffer.wrap("我是服务端,收到您的信息了".getBytes()));
            try {
                write.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        attachment.clear();
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
