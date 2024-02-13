import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * This class utilizes the thread pool from Server.java
 * to handle incoming client requests and allow the server
 * to be open to new incoming clients
 * */
public class ClientHandler implements Runnable{

    private ExecutorService executorService;
    private Socket clientSocket;

    public ClientHandler(ExecutorService executorService, Socket clientSocket){
        this.executorService = executorService;
        this.clientSocket = clientSocket;
    }
    @Override
    public void run(){
        executorService.submit(() -> {

        });
    }

    // accept connect with server socket and return socket object


    // Authenticate User

    // Create new user profile

}
