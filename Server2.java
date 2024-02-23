import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    }

    private void test(){
        System.out.println("Server is running...");
    }

    public void start(ServerSocketService ss, ExecutorService executorService) {
        while (ss.isAccepting()) {

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    // socket accepts client request & creates client socket
                Socket clientSocket = ss.acceptConnection();
                try {

                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    if(clientHandler.ConnectRPC(clientSocket)){
                        System.out.println("Client successfully connected to server!" + clientSocket.getInetAddress());
                        
                    }
                }
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });

        }
    }



    public static void main(String[] args) {
        System.out.println("multi-threaded server...");
        int PORT = 3001;
        Server2 server = new Server2(PORT);
    }

 }