package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketService {

    public ServerSocket ss;
    public int PORT;
    public ServerSocketService(int PORT){
        try{
            this.PORT = PORT;
            ss = new ServerSocket(PORT);
        } catch (Exception e){
            System.out.println("Unable to create server socket service :(");
            e.printStackTrace();
        }
    }

    // this method accepts new incoming client connections and
    // creates a new socket object or returns null if connection was unsuccessful
    public Socket acceptConnection(){
        Socket newConnection;
        try {
            newConnection = ss.accept();
            return newConnection;
        } catch (IOException e) {
            System.out.println("Unable to accept new socket connection");
            e.printStackTrace();
        }
        return null;
    }

    public boolean isAccepting(){
        return ss.isBound();
    }
}
