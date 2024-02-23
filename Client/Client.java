package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.*;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
    public static void main(String[] args) {

        try {

            System.out.println("Client Socket");

            // client creates new socket using host and port number that server is running
            // Once server accept the connection with client will socket object be created
            Socket soc = new Socket("localhost", 3001);
            Thread thread = receiveMessage(soc);
            thread.start();

            while(soc.isConnected()){

                // System.in is an inputstream obj that takes a bytestream of data
                // Using inputstream reader, it takes a bytestream and returns a character stream
                // Lastly bufferedreader will be able to read the entire string from the input
                String userStr = getInput();
                // send message to server
                sendInput(userStr, soc);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("Socket disconnected & client will now shutdown");
    }

    public static String getInput(){
        try {
            // System.in is an inputstream obj that takes a bytestream of data
            // Using inputstream reader, it takes a bytestream and returns a character stream
            // Lastly bufferedreader will be able to read the entire string from the input
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter Message: ");
            String userStr;
            if((userStr = userInput.readLine())  != null){ // this method will capture user input
                return userStr;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException occurred while getting input from user");
        }
        return null;
    }

    public static void sendInput(String userStr, Socket soc){
        try {
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
            // send string to server
            out.println(userStr);
        } catch (IOException e){
            System.out.println("Error sending message to server");
        } catch (Exception e) {
            System.out.println("Socket disconnected abruptly " + e.getMessage());
        }
    }

    public static Thread receiveMessage(Socket soc){
        Thread thread = new Thread(() -> {
            try {
                BufferedReader serverInputStream =
                        new BufferedReader(new InputStreamReader(soc.getInputStream()));
                String serverStr;

                while((serverStr = serverInputStream.readLine()) != null){
                    System.out.println("");
                    System.out.println("Server Message: " + serverStr);
                }
            } catch (IOException e) {
                System.out.println("Unable to receive message from server " + e.getMessage());
            }
        });
        return thread;
    }
}