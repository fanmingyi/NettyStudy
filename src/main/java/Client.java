import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channel;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {


    public static void main(String[] args) {

        Socket socket = null;

        try {
            socket = new Socket("127.0.0.1", 8100);


            System.out.println("客户端：链接");

            OutputStream inputStream1 = socket.getOutputStream();


            OutputStreamWriter osw = new OutputStreamWriter(inputStream1);

            InputStreamReader isr = new InputStreamReader(socket.getInputStream());

            BufferedReader bufferedReader = new BufferedReader(isr);


            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("客户端：请输入发送到服务器的内容");
//                String userInput = scanner.nextLine();
//                System.out.println("客服端：从控制台收到读取的内容 "+userInput);
                osw.write("1234\r\n");
                osw.flush();

//                Thread.sleep(1000);

                TimeUnit.SECONDS.sleep(5);
//                String receive = bufferedReader.readLine();

//                System.out.println("客户端：收到信息" + receive);

            }
//
//            byte[] buf = new byte[1024];
//            int read = -1;
//
//
//            while ((read = inputStream.read(buf)) != -1) {
//                System.out.println("服务端：读取到数据");
//            }


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
