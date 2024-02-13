import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.nio.Buffer;

public class Client {
    public static void main(String[] args) {

        try {
            System.out.println("Client Socket");

            // client creates new socket using host and port number that server is running
            // Once server accept the connection with client will socket object be created
            Socket soc = new Socket("localhost", 3001);

            while(soc.isConnected()){
                // System.in is an inputstream obj that takes a bytestream of data
                // Using inputstream reader, it takes a bytestream and returns a character stream
                // Lastly bufferedreader will be able to read the entire string from the input
                String userStr = getInput();

                // send message to server
                sendInput(userStr, soc);

                // recieve message from server
                receiveMessage(soc);

            }

        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("Socket disconnected & client ");
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

    public static void receiveMessage(Socket soc){
        try {
            BufferedReader serverInputStream = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            System.out.println(serverInputStream.readLine());
        } catch (IOException e) {
            System.out.println("Error receiving message from server " + e.getMessage());
        }
    }
}