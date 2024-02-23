import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import Server_RPC.LoginRPC;
import Server_context.GameSession;
import Server_context.GlobalContext;
import Server_context.UserCache;
import Server_context.UserContext;

public class Server2{
    
    // port number to listen on 
    private int PORT;

    // server socket service
    private ServerSocketService ss;

    private ExecutorService executorService;

    private static GlobalContext globalContext;

    UserCache userCache;

    private GameSession gameSession;

    public Server2(int PORT){

        this.PORT = PORT;

        this.userCache = new UserCache();

        ss = new ServerSocketService(PORT);

        this.executorService = Executors.newFixedThreadPool(4);

        globalContext = new GlobalContext(userCache, gameSession);

        start(ss, executorService);

    }

    public void start(ServerSocketService ss, ExecutorService executorService) {

        // Use threadpool to accept up to 4 clients simultaneously
        Semaphore sem = new Semaphore(4);

        while (ss.isAccepting()) {
            try {
                //
                Future<Socket> clientSocketFuture = connectRPC(ss, executorService, sem);
                Socket clientSocket = clientSocketFuture.get();
                if(clientSocket != null){
                    Thread clientThread = new Thread(() -> {
                        try {
                            ClientHandler clientHandler = new ClientHandler(clientSocket, globalContext);
                            System.out.println("Client successfully connected to server!" + clientSocket.getInetAddress());
                        } catch (IOException e) {
                            System.out.println("ERROR: Unable to create client thread" + e.getMessage());
                        }
                    });
                    clientThread.start();

                } else {
                    System.out.println("Unsuccessful connection to server");
                }

            } catch (InterruptedException | ExecutionException e){
                System.out.println("ERROR: unable to create client socket on server" + e.getMessage());
            }
        }
    }


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
    public static void main(String[] args) {
        System.out.println("multi-threaded server...");
        int PORT = 3001;
        Server2 server = new Server2(PORT);
    }
 }