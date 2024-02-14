import context.GameSession;
import context.GlobalContext;
import context.UserCache;
import context.UserContext;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    // initialize context
    public static UserCache userCache = new UserCache();
    public static GameSession gameSession = new GameSession();
    public static GlobalContext globalContext = new GlobalContext(userCache, gameSession);

    public static void main(String[] args) {

        System.out.println("Single threaded server...");
        int PORT = 3001;

        try {
            ServerSocketService socketServer = new ServerSocketService(PORT);
            // create thread pool with 4 threads
            ExecutorService executorService = Executors.newFixedThreadPool(4);


            while(socketServer.isAccepting()){

                // socket accepts client request & creates client socket
                Socket clientSocket = socketServer.acceptConnection();

                // Use a thread from threadpool to notify client of connection status
                // and listen to incoming client messages
                executorService.execute(() -> {
                    ConnectRPC(clientSocket);
                    receiveMessage(clientSocket);
                });

                // create new thread object
//            ClientHandler clientThread = new ClientHandler(executorService, clientSocket);
            }
        } catch (Exception e) {
            System.out.println("Error starting server " + e.getMessage());
        }

    }



    /**
     * ValidateUsernameRPC will determine if username provided is unique and
     * not included in current list of users on the server
     * */
    private static void validateUsernameRPC(Socket clientSocket){

        try {
            // request for unique username
            String username;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            username = bufferedReader.readLine();

            // validate username
            PrintWriter outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
            if(!userCache.validateUsername(username)){
                // if username is not found in user cache then send success integer to client
                outputStream.println("Username is valid");
            } else {
                // if username is found in user cache then send failure integer to client
                outputStream.println("Username is taken");
            }
            System.out.println("Validated username successfully");

        } catch (IOException e) {
            System.out.println("VALIDATE_USERNAME_RPC: Error reading in client username " + e.getMessage());
        }
    }

    /**
     * ConnectRPC will handle notifying clientside that client has been connected
     * succesfully and then transfer over to ClientHandler thread for auth RPCs
     * */
    private static void ConnectRPC(Socket clientSocket){
        try {
            PrintWriter serverOutputStream =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            if(clientSocket == null){
                serverOutputStream.println(0);
            }
            serverOutputStream.println(1);
            System.out.println("Client successfully connected to server!" + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.out.println("Unsuccessful connect to server, please disconnect " +
                    "clientside and retry" + e.getMessage());
        }
    }

    private static void receiveMessage(Socket clientSocket){
        try {
            BufferedReader clientInputStream =
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientMessage;

            // continuously check for incoming messages and print to server
            while((clientMessage = clientInputStream.readLine()) != null){
                // depending on client message, route to specific RPC
                switch(clientMessage){
                    case "Login":

                        break;
                    case "New User":
                        validateUsernameRPC(clientSocket);

                        break;
                    case "Waiting":
                        break;
                    case "Game End":
                        break;
                    case "Disconnect":
                        break;
                    default:
                        sendMessage(clientSocket, "Server received invalid client request.");

                }
                System.out.println("Message recieved from client! " +
                        clientMessage + " : " + clientSocket.getInetAddress());
            }
        } catch (IOException e) {
            System.out.println("Client disconnected abruptly - " +
                    "unable to receive messages from client " + e.getMessage());
        }
    }

    private static void sendMessage(Socket clientSocket, String message){
        try {
            PrintWriter serverOutputStream =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            serverOutputStream.println(message);
        } catch (IOException e) {
            System.out.println("Error writing message to client!" + e.getMessage());
        }
    }

}