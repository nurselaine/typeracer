package Server;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import Server.Server_context.GameCache;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserCache;
import Server.Server_context.UserContext;

public class Server {

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

    private GameCache gameCache;

    // path to user database
    private final Path path = Paths.get("Server", "utils", "user_database.txt");

    public Server(int PORT){

        this.PORT = PORT;

        this.userCache = new UserCache();

        ss = new ServerSocketService(PORT);

        globalContext = new GlobalContext(userCache, gameCache);

        // binary semaphore to manage access to global context
        globalContextSem = new Semaphore(1);

        // binary semaphore to manage access to user cache
        userCacheSem = new Semaphore(1);
        // binary semaphore to manage access to game session
        // binary semaphore to manage access to game context

        start(ss);

    }

    public void start(ServerSocketService ss) {

        loadUserData();

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

            // TODO: handle when client disconnects
        }
        // TODO: handle when server disconnects
    }

    /*
     * Load user data from the user database
     */
    public void loadUserData() {
        // on server start = read user database and create new user profile for each
        // saved user
        Thread reloadClient = new Thread(() -> {

            // get the file path to the database

            try {
                Scanner fileReader = new Scanner(Files.newInputStream(path));
                while (fileReader.hasNextLine()) {

                    String[] user_credentials = fileReader.nextLine().split(" ");

                    if (user_credentials.length != 3) {
                        throw new IOException("Invalid user database format");
                    }

                    // get socket id - use localhost for development
                    int colon = user_credentials[0].indexOf(':');
                    String host = user_credentials[0].substring(0, colon);

                    // create new user context and add to user cache
                    globalContext.addUser(new UserContext(host, user_credentials[1], user_credentials[2]));
                }
                fileReader.close();
            } catch (IOException e) {
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