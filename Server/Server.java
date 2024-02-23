package Server;

import Server.Server_RPC.LoginRPC;
import Server.Server_context.GameSession;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserCache;

import java.io.*;
import java.net.Socket;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server{

    // initialize context
    private static UserCache userCache = new UserCache();
    private static GameSession gameSession = new GameSession();
    private static GlobalContext globalContext = new GlobalContext(userCache, gameSession);

    public static void main(String[] args) {

        System.out.println("multi-threaded server...");
        int PORT = 3001;

        try {
            ServerSocketService socketServer = new ServerSocketService(PORT);
            // create thread pool with 4 threads
            ExecutorService executorService = Executors.newFixedThreadPool(4);


            // Non-blocking socket that allows clients to be accepted and
            while(socketServer.isAccepting()){

                // socket accepts client request & creates client socket
                Socket clientSocket = socketServer.acceptConnection();

                // Use a thread from thread pool to notify client of connection status
                // and listen to incoming client messages
                executorService.execute(() -> {

                    // create a client reader and writer objects for each new thread created
                    try {
                        PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        ConnectRPC(clientSocket, clientWriter, clientReader);
                    } catch (IOException e){
                        System.out.println("Unsuccessful connect to server, please disconnect " +
                                "client-side and retry" + e.getMessage());
                    }
                    System.out.println("Thread returning to pool");
                });

                Thread clientThread = new Thread(() -> {
                    System.out.println("New client thread being created");
                    try{
                        PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        receiveMessage(clientSocket, clientWriter, clientReader);
                    } catch (IOException e){
                        System.out.println("SERVER ERROR: ISSUES CREATING INDIVIDUAL CLIENT THREAD");
                        e.printStackTrace();
                    }
                });
                clientThread.start();
            }
        } catch (Exception e) {
            System.out.println("Error starting server " + e.getMessage());
        }

    }

    /**
     * ConnectRPC will handle notifying clientside that client has been connected
     * succesfully and then transfer over to ClientHandler thread for auth RPCs
     * */
    private static void ConnectRPC(Socket clientSocket, PrintWriter clientWriter, BufferedReader clientReader) {
        if (clientSocket == null) {
            clientWriter.println(0);
            System.out.println("Client socket not connected to server!" + clientSocket.getInetAddress());
        } else {
            clientWriter.println(1);
            System.out.println("Client successfully connected to server!" + clientSocket.getInetAddress());
        }
    }

    private static void receiveMessage(Socket clientSocket, PrintWriter clientWriter, BufferedReader clientReader) throws IOException {
        try {
            System.out.println("receive message");

            String clientMessage;
            LoginRPC loginHandler = new LoginRPC();

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
                        sendMessage(clientSocket, " Server received invalid client request.", clientWriter, clientReader);

                }
                System.out.println("Message recieved from client! " +
                        clientMessage + " : " + clientSocket.getInetAddress());
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

    private static void sendMessage(Socket clientSocket, String message,  PrintWriter clientWriter, BufferedReader clientReader){
        clientWriter.println(message);
    }

}