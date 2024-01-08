import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {

        System.out.println("waiting for client...");

        try {
            // ServerSocket obj awaits client connectiona and creates socket obj when
            // client triggers connection request
            ServerSocket ss = new ServerSocket(9001);
            Socket soc = ss.accept(); // server accpts connection and captures socket obj (soc)

            // receive string from client (input stream is reading data)
            BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            String clientStr = in.readLine();

            // Send message to client output stream is from sending (writing) data to client
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

            out.println("Server says:" + clientStr);

            System.out.println("Socket terminating...");
        } catch (Exception exception){
            exception.printStackTrace();
        }


    }
}