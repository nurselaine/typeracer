package Server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Client.Client;
import Server.Server_context.GameSession;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserCache;

public class Server{

    // port number to listen on
    private int PORT;

    // server socket service
    private ServerSocketService ss;

    private static GlobalContext globalContext;

    UserCache userCache;

    private GameSession gameSession;

    public Server(int PORT){

        this.PORT = PORT;

        this.userCache = new UserCache();

        ss = new ServerSocketService(PORT);

        globalContext = new GlobalContext(userCache, gameSession);

        start(ss);

    }

    public void start(ServerSocketService ss) {
        while (ss.isAccepting()) {

            Socket clientSocket = ss.acceptConnection();


            Thread clientThread = new Thread(() -> {
                try {
                    ClientHandler clientHandler = new ClientHandler(clientSocket, globalContext);
                    while (clientHandler.clientStatus) {
                        clientHandler.ReceiveMessage();
                    }
                } catch (IOException e) {
                    System.out.println("ERROR: creating client handler " + e.getMessage());
                }
            });
            clientThread.start();
        }
    }

    public static void main(String[] args) {
        System.out.println("multi-threaded server...");
        int PORT = 3001;
        Server server = new Server(PORT);
    }
}