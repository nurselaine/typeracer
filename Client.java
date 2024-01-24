import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.nio.Buffer;

public class Client {
    public static void main(String[] args) throws InterruptedException {

        try {
            System.out.println("Client Socket");

            // client creates new socket using host and port number that server is running
            // Once server accept the connection with client will socket object be created
            Socket soc = new Socket("localhost", 9001);

            // System.in is an input stream obj that takes a byte stream of data
            // Using input stream reader, it takes a byte stream and returns a character stream
            // Lastly bufferedreader will be able to read the entire string from the input
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            // prompt client to add message
            System.out.print("Enter message: ");
            String userStr = userInput.readLine(); // this method will capture user input

            // create output stream & send to server
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
            out.println(userStr);

            // create input stream & print out for user
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String serverMessage = in.readLine();
            System.out.println(serverMessage);

            // sleep for 5 seconds
            Thread.sleep(5000);

            soc.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}