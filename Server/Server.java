package Server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import Server.Game.GameCache;
import Server.ServerContext.GlobalContext;
import Server.ServerContext.User;
import Server.ServerContext.UserCache;
import Server.ServerContext.ClientHandler;
import Server.ServerContext.DataBase;

public class Server {

    // port number to listen on
    private int port;

    // server socket service
    private ServerSocketService ss;

    public Server(int PORT){

        this.port = PORT;

        ss = new ServerSocketService(PORT);

        start(ss);
    }

    /**
     * Start the server
     * @param ss
     */
    public void start(ServerSocketService ss) {
        

        while (ss.isAccepting()) {

            Socket clientSocket = ss.acceptConnection();
            Thread clientThread = new Thread(() -> {
                try {
                    ClientHandler clientHandler = new ClientHandler(clientSocket, globalContext);
                    while (clientHandler.clientStatus) {
                        clientHandler.CommandHandler();
                    }
                } catch (SocketException e) {
                    System.out.println("Client has left the server!!");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            clientThread.start();

            // TODO: handle when client disconnects
        }
        
        // TODO: handle when server disconnects
    }

    public static void main(String[] args) {
        System.out.println("multi-threaded server...");
        int PORT = 9876;
        Server server = new Server(PORT);
    }
}