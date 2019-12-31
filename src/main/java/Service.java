import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Service {


    public static void main(String[] args) {

        ServerSocket socket = null;

        try {
            socket = new ServerSocket(8872);


            System.out.println("服务端：开始等候一个链接");
            Socket accept = socket.accept();
            System.out.println("服务端：收到一个链接:" + socket.getLocalSocketAddress().toString());


            InputStream inputStream = accept.getInputStream();


            byte[] buf = new byte[1024];
            int read = -1;


            while ((read = inputStream.read(buf)) != -1) {
                System.out.println("服务端：读取到数据");
            }

            System.out.println("服务端：结束");

        } catch (Exception e) {
            System.out.println("服务端：异常");
            e.printStackTrace();
        } finally {

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
