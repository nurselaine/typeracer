package Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static void main(String[]  args) throws IOException {

        // Selector is created
        Selector selector = Selector.open();

        // Create a server socket channel for stream-oriented listening sockets
        ServerSocketChannel socket = ServerSocketChannel.open();

        // insetsokcetaddress is the IP + port number
        InetSocketAddress socketAddress = new InetSocketAddress("localhost", 3001);

        // bind the socket to the address so the socket listens for connections to 3001
        socket.bind(socketAddress);

        // assign channel to be non-blocking
        socket.configureBlocking(false);

        int ops = socket.validOps();

        // token to register to a channel with a selector | select key is created each time a channel is registered with a selector
        SelectionKey selectKey = socket.register(selector, ops, null);

        // loop indefinitely
        while(true){

            System.out.println("Server is waiting for new client connections");

            selector.select(); // selects set of keys whose channels are open to i/o operation

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();

            // Tests whether this key's channel is ready to be read
            while(iterator.hasNext()){

                SelectionKey myKey = iterator.next();
                if(myKey.isAcceptable()){
                    SocketChannel client = socket.accept(); // accept client and create socket

                    // adjust client to be non-blocking
                    client.configureBlocking(false);

                    // Operation set but for read operations
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("Connection accepted: " + client.getLocalAddress());
                } else if (myKey.isReadable()) { // check whether key's channel can be read

                    SocketChannel client = (SocketChannel) myKey.channel();

                    // Byte container for data
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    client.read(buffer);

                    String result = new String(buffer.array()).trim();

                    System.out.println("Message received " + result);

                    if(result.equals("Hello world")){
                        client.close();
                        System.out.println("Closing connection. Server still up and running");
                    }
                }

                iterator.remove();
            }
        }
    }
}
