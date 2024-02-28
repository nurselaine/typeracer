package Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        InetSocketAddress address = new InetSocketAddress("localhost",3001);

        SocketChannel client = SocketChannel.open(address);

        System.out.println("Connecting to server...");

        List<String> test = new ArrayList<>();

        test.add("Hello");
        test.add("hello world");
        test.add("bye");
        test.add("good morning");
        test.add("Hello world");

        for(String str : test){
            byte[] message = new String(str).getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            client.write(buffer);

            System.out.println("Sending... " + str);
            buffer.clear();

            Thread.sleep(2000);
        }

        client.close();

    }
}
