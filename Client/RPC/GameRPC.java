package Client.RPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class GameRPC {

    private PrintWriter serverWriter;
    private BufferedReader serverReader;
    public GameRPC(PrintWriter serverWriter, BufferedReader serverReader){
        this.serverWriter = serverWriter;
        this.serverReader = serverReader;
    }

    public void joinWaitingQueue(){
        try {
            serverWriter.println("Waiting");
            String res = serverReader.readLine();
            if (res != null) {
                int playerAhead = Integer.parseInt(res);
                System.out.println("> Joined waiting queue! " + (playerAhead - 1) + " other players in queue...");
            } else {
                System.out.println("> Unable to join waiting queue. Please try again.");
            }
        } catch (IOException e){
            System.out.println("ERROR: unable to join waiting queue " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void leaveWaitQueue(){
        try {
            serverWriter.println("Leave wait queue");
            String res = serverReader.readLine();
            if(Integer.parseInt(res) == 0){
                System.out.println("> Successfully left wait queue.");
            } else {
                System.out.println("> Unable to leave wait queue.");
            }
        } catch (IOException e) {
            System.out.println("> Unable to leave wait queue. Issues with server connection..." + e.getMessage());
            e.printStackTrace();
        }
    }

    public void checkWaitingTime(){
        try {
            serverWriter.println("Wait Time");
            String res = serverReader.readLine();
            if (res != null) {
                int playerNeeded = Integer.parseInt(res);
                System.out.println("> Awaiting " + playerNeeded + " other players to join queue...");
            } else {
                System.out.println("> Unable to check queue time. Please try again.");
            }
        } catch (IOException e){
            System.out.println("ERROR: unable to join waiting queue " + e.getMessage());
            e.printStackTrace();
        }
    }

}
