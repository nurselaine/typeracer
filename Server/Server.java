package Server;

import Server.Server_RPC.LoginRPC;
import Server.Server_context.GameSession;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserCache;
import Server.Server_context.UserContext;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;


public class Server{

    // initialize context
    public static int clientThreadCount = 0;
    private static UserCache userCache = new UserCache();
    private static GameSession gameSession = new GameSession();
    private static GlobalContext globalContext = new GlobalContext(userCache, gameSession);

    public static void main(String[] args) {

        System.out.println("multi-threaded server...");
        int PORT = 3001;
        initializeSavedUsers();

        try {
            ServerSocketService socketServer = new ServerSocketService(PORT);
            // create thread pool with 4 threads
            ExecutorService executorService = Executors.newFixedThreadPool(4);

            // Creating Semaphore object to share client reader and writers for connecting to server
            Semaphore sem = new Semaphore(4);

            while(socketServer.isAccepting()){

                Future<Socket> clientSocketFuture = connectRPC(socketServer, executorService, sem);
                Socket clientSocket = clientSocketFuture.get();
                if(clientSocket!= null){
                    handleClient(clientSocket);
                } else {
                    System.out.println("client failed to connect to server");
                }

            }
        } catch (Exception e) {
            System.out.println("Error starting server " + e.getMessage());
        }

    }

    /**
     * ConnectRPC will handle notifying clientside that client has been connected
     * succesfully and then transfer over to ClientHandler thread for auth RPCs
     * */
    private static Future<Socket> connectRPC(ServerSocketService socketServer, ExecutorService executorService, Semaphore sem) {
        // Use future object to asynchronously get the
        CompletableFuture<Socket> futureSocket = new CompletableFuture<>();
        executorService.execute(() -> {
            try {
                Socket clientSocket = socketServer.acceptConnection();
                sem.acquire();

                // create a client reader and writer objects for each new thread created
                PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                if (clientSocket == null) {
                    clientWriter.println(0);
                    System.out.println("Client socket not connected to server!" + clientSocket.getInetAddress());
                } else {
                    clientWriter.println(1);
                    futureSocket.complete(clientSocket);
                }

                sem.release();
                clientWriter.close();

            } catch (IOException | InterruptedException e){
                System.out.println("Unsuccessful connect to server, please disconnect " +
                        "client-side and retry" + e.getMessage());
            }
        });
        return futureSocket;
    }

    /**
     * Creates a new thread for every accepted client socket and handles input/output streams
     * and client requests
     * */
    private static void handleClient(Socket clientSocket){
        clientThreadCount++;

        // create and start client thread for each new accepted socket
        Thread clientThread = new Thread(() -> {
            System.out.println("New client thread being created");
            try{

                // create individual client writer and reader object
                PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // handle incoming requests and map client to the correct RPC
                receiveMessage(clientSocket, clientWriter, clientReader, "client" + clientThreadCount);
            } catch (IOException e){
                System.out.println("SERVER ERROR: ISSUES CREATING INDIVIDUAL CLIENT THREAD");
                e.printStackTrace();
            }
        }, Integer.toString(clientThreadCount));
        clientThread.start();

        clientThreadCount--;
    }

    /**
     * Manages client requests and routes client to correct request handler
     * */
    private static void receiveMessage(Socket clientSocket, PrintWriter clientWriter,
                                       BufferedReader clientReader, String clientName) throws IOException {
        try {

            String clientMessage;
            LoginRPC loginHandler = new LoginRPC(clientWriter, clientReader);

            // continuously check for incoming messages and print to server
            while((clientMessage = clientReader.readLine()) != null){
                // depending on client message, route to specific RPC
                switch(clientMessage){
                    case "Login":
                        loginHandler.Login(clientSocket, userCache);
                        break;
                    case "New User":
                        loginHandler.newUserRPC(clientSocket, userCache);
                        break;
                    case "Waiting":
                        break;
                    case "Game End":
                        break;
                    case "Disconnect":
                        break;
                    default:
                        clientWriter.println(" Server received invalid client request.");

                }
                System.out.println("Message recieved from "+ clientName +"! " + clientMessage + " : ");
            }
        } catch (IOException e) {
            System.out.println("Client disconnected abruptly - " +
                    "unable to receive messages from client " + e.getMessage());
        } finally {
            if(clientReader!=null){
                clientReader.close();
            }
            if(clientWriter != null){
                clientWriter.close();
            }
        }
    }

    /**
     * Reads user database and creates new user contexts to add into user cache for
     * users to skip the new user step each time the server shuts down
     * */
    private static void initializeSavedUsers(){
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

}