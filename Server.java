import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * When the server gets a new connection request from client.
 * Server will then be in an endless loop servicing this client
 * until the client has disconnected.
 *
 * Once the client has disconnected, the server will then go
 * back to accepting a new connection.
 * This model is a single threaded server that can only
 * handle one client at a time.
 *
 * The client program should Establish a connection to the server
 * Execute the Connect call Sleep for a random amount of time
 * between 1 and 10 seconds. Execute the Disconnect call
 * */

public class Server{
    public static void main(String[] args) {

        System.out.println("Single threaded server...");

        try (ServerSocket ss = new ServerSocket(9001)) {
            // ServerSocket obj awaits client connectiona and creates socket obj when
            // client triggers connection request

            while(true){

                try{
                    Socket soc = ss.accept(); // server accpts connection and captures socket obj (soc)
                    System.out.println("Accepted connection from: " + soc.getInetAddress());

                    // receive string from client (input stream is reading data)
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()))) {
                        String clientStr;

                        // when server gets new server connection, server should begin endless loop
                        while((clientStr = in.readLine()) != null){
                            System.out.println("Message received from client1: " + clientStr);

                            // Send message to client output stream is from sending (writing) data to client
                            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
                            out.println("Client " + soc.getInetAddress() + ": " + clientStr);
                        }
                    } catch (Exception e){
                        System.out.print("Client abruptly disconnected...");
                        e.getMessage();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception exception){
            exception.printStackTrace();
        }

    }
}