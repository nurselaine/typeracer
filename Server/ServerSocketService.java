package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketService {

    public ServerSocket ss;
    public int PORT;
    private volatile boolean running = false;
    public ServerSocketService(int PORT){
        try{
            this.PORT = PORT;
            ss = new ServerSocket(PORT);
            running = true;
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

    public boolean running() {
        return running;
    }

    public void stop() {
        running = false;
        try {
            if (ss != null && !ss.isClosed()) {
                ss.close();
            }
        } catch (IOException e) {
            System.out.println("Error: closing server socket");
            e.printStackTrace();
        }
    }
}
