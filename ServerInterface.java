import java.net.Socket;
import java.io.*;

public interface ServerInterface {

    private static void ConnectRPC(Socket clientSocket){
        try {
            PrintWriter serverOutputStream =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            if(clientSocket == null){
                serverOutputStream.println(0);
            }
            serverOutputStream.println(1);
            System.out.println("Client successfully connected to server!" + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.out.println("Unsuccessful connect to server, please disconnect " +
                    "clientside and retry" + e.getMessage());
        }
    }

    public static void receiveMessage(Socket clientSocket){

    } 
    


    public static void LoginRPC(Socket clientSocket){

    }

}