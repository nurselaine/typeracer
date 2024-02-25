package Server;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import Client.Client;
import Server.Server_context.GameSession;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserCache;
import Server.Server_context.UserContext;

public class Server{

    // port number to listen on
    private int PORT;

    // server socket service
    private ServerSocketService ss;

    private static GlobalContext globalContext;

    // binary semaphore to manage access to global context
    public static Semaphore globalContextSem;

    // binary semaphore to manage access to user cache
    public static Semaphore userCacheSem;

    UserCache userCache;

    private GameSession gameSession;

    public Server(int PORT){

        this.PORT = PORT;

        this.userCache = new UserCache();

        ss = new ServerSocketService(PORT);

        globalContext = new GlobalContext(userCache, gameSession);

        // binary semaphore to manage access to global context
        globalContextSem = new Semaphore(1);

        // binary semaphore to manage access to user cache
        userCacheSem = new Semaphore(1);
        // binary semaphore to manage access to game session
        // binary semaphore to manage access to game context

        start(ss);

    }

    public void start(ServerSocketService ss) {

        saveUserData();

        while (ss.isAccepting()) {

            Socket clientSocket = ss.acceptConnection();


            Thread clientThread = new Thread(() -> {
                try {
                    ClientHandler clientHandler = new ClientHandler(clientSocket, globalContext,
                            globalContextSem, userCacheSem);
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

    public void saveUserData(){
        // on server start = read user database and create new user profile for each saved user
        Thread reloadClient = new Thread(() -> {
            File userDB = new File("C:\\Users\\Elain\\Projects\\typeracer\\Server\\utils\\user_database.txt");
            try {
                Scanner fileReader = new Scanner(userDB);
                while(fileReader.hasNextLine()){
                    String[] user_credentials = fileReader.nextLine().split(" ");

                    // get socket id - use localhost for development
                    int colon = user_credentials[0].indexOf(':');
                    String host = user_credentials[0].substring(0, colon);

                    // create new user context and add to user cache
                    userCache.addNewUser(new UserContext(host, user_credentials[1], user_credentials[2]));
                }
            } catch (IOException e){
                System.out.println("Unable to initialize user database");
                e.printStackTrace();
            }
        });
        reloadClient.start();
    }

    public static void main(String[] args) {
        System.out.println("multi-threaded server...");
        int PORT = 3001;
        Server server = new Server(PORT);
    }
}