
import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        int port = 1234; // Port number
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on Port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received: " + inputLine);
                    out.println("Echo: " + inputLine);

                    if (inputLine.equals("bye")) {
                        break;
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port);
            System.out.println(e.getMessage());
        }
    }
}

