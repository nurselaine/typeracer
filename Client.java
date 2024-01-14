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
            Socket soc = new Socket("localhost", 9001);

            while(soc.isConnected()){
                // System.in is an inputstream obj that takes a bytestream of data
                // Using inputstream reader, it takes a bytestream and returns a character stream
                // Lastly bufferedreader will be able to read the entire string from the input
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter Message: ");
                String userStr; // this method will capture user input

                while((userStr = userInput.readLine()) != null){
                    // printwriter obj will write any kind of data and passing in socket obj
                    PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
                    // send string to server
                    out.println(userStr);

                    BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    System.out.println(in.readLine());

                    System.out.print("Enter Message: ");
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getInput(){

    }
}