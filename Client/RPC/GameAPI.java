package Client.RPC;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import Client.Client;
import Client.Client.ClientState;

public class GameAPI{
    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    AtomicInteger requestID;
    ClientState state;
    
    // constructor
    public GameAPI(PrintWriter clientWriter, BufferedReader clientReader, ClientState state){
        this.clientWriter = clientWriter;
        this.clientReader = clientReader;
        this.requestID = new AtomicInteger(0);
        this.state = state;
    }

    // join waiting queue
    public boolean joinWaitQueue() throws Exception{
        // send message to server
        clientWriter.println("Join Wait Queue");

        // parse response from server
        String res = this.clientReader.readLine(); 

        if(res.equals("1")){
            System.out.println("User added to wait queue");
            return true;
            
        } else {
            System.out.println("User already in wait queue");
            return false;
        }
        // update client UI

        // Start a new thread to handle the response
        // Thread responseThread = new Thread(() -> {
        //     try {
        //         // Wait for the response from the server
        //         String response = clientReader.readLine();

        //         // Parse the response and update the client UI
        //         parseResponse(response);

        //         // If the response is 1, set the client status to "game begin"
        //         if (response.equals("1")) {
        //             state = ClientState.PLAYING;
        //         }
        //     } catch (Exception e) {
        //         e.printStackTrace();
        // //     }
        // });
        // responseThread.start();
    }

    public boolean leaveWaitQueue() throws Exception{
        // send message to server
        clientWriter.println("Leave Wait Queue");
        if(clientReader.readLine().equals("1")){
            System.out.println("User removed from wait queue");
            return true;
        } else {
            System.out.println("User not in wait queue");
            return false;
        }
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