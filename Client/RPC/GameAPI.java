package Client.RPC;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class GameAPI{
    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    AtomicInteger requestID;
    
    // constructor
    public GameAPI(PrintWriter clientWriter, BufferedReader clientReader){
        this.clientWriter = clientWriter;
        this.clientReader = clientReader;
        this.requestID = new AtomicInteger(0);
    }

    // join waiting queue
    public void joinWaitQueue(){
        // send message to server
        clientWriter.println("Join Wait Queue");

    }

    public void checkWaitTime(){
        // send message to server
        clientWriter.println("Check Wait Time");
    }

    private void parseResponse(String response){
        // parse response from server
        // update client UI
    }
}