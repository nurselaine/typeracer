import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Server_RPC.LoginRPC;
import Server_context.GameSession;

public class Server2{
    
    // port number to listen on 
    private int PORT;

    // server socket service
    private ServerSocketService ss;

    private ExecutorService executorService;

    private LoginRPC loginRPC;

    private GameSession gameSession;

    public Server2(int PORT){

        this.PORT = PORT;

        ss = new ServerSocketService(PORT);

        this.executorService = Executors.newFixedThreadPool(4);
    }
    

    public void start(){
        while(ss.isAccepting()){
            executorService.execute(() -> {
                Socket clientSocket = ss.acceptConnection();
                ConnectRPC(clientSocket);
                receiveMessage(clientSocket);
            });
            };
        }
            

    }
    
    public static void main(String[] args) {
        System.out.println("multi-threaded server...");
        int PORT = 3001;
        Server2 server = new Server2(PORT);
    }
    
}