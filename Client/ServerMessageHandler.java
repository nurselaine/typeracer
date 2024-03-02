package Client;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerMessageHandler implements Runnable {
    private BufferedReader serverReader;
    private volatile boolean running = true;

    public ServerMessageHandler(BufferedReader serverReader) {
        this.serverReader = serverReader;
    }
    @Override
    public void run() {
       try {
           while (running) {
               String serverMessage = serverReader.readLine();
               if (serverMessage == null) {
                   throw new IOException("Server lost connection");
               }
               // output server msg
               System.out.println("Server: " + serverMessage);
           }
       } catch (IOException e) {
           System.out.println("Lost connection to the server. Exiting...");
           running = false;
           System.exit(1);
       }
    }

    public void stop () {
        running = false;
    }
}
