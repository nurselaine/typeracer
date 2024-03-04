package Client.RPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GameRPC {

    private PrintWriter serverWriter;
    private BufferedReader serverReader;
    private Scanner input;
    private String gameStr;
    public GameRPC(PrintWriter serverWriter, BufferedReader serverReader, Scanner input){
        this.serverWriter = serverWriter;
        this.serverReader = serverReader;
        this.input = input;
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

    public boolean checkWaitingTime(){
        try {
            serverWriter.println("Wait Time");
            String res;
            String updateClient = "";
            while((res = serverReader.readLine()) != null && Integer.parseInt(res) != 400){
                if(!res.equals(updateClient)){
                    System.out.println(res + " players in the wait queue.");
                    updateClient = res;
                }
                serverWriter.println("Wait Time");
            }
            return true;
        } catch (IOException e){
            System.out.println("ERROR: unable to join waiting queue " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("CHECK WAIT TIME OVER");
        return false;
    }

    public void startGame() throws IOException {
        System.out.println("Game is Starting now...");
        this.serverWriter.println("Start Game");
        // get game string from server
        this.gameStr = serverReader.readLine();

        // prompt user to start typing
        System.out.println("> Start typing!");
        System.out.println("> PROMPT: " + gameStr);

        Instant timeStart = Instant.now();
        Instant timeEnd = Instant.now();

        String userStr = "";
        while(!userStr.equals(gameStr)){
            System.out.println("Press ENTER when done typing: ");
            System.out.print("> ");
            userStr = input.nextLine();
            if(userStr.equals(gameStr)){
                timeEnd = Instant.now();
            } else {
                System.out.println("> Typo found! Please try again!! Times running out :(");
                System.out.println("> PROMPT: " + gameStr);
            }
        }

        // send back total time to
        long[] times = calculateTotalTime(timeStart, timeEnd);
        // format time to string
        String totalTime = times[0] + "." + (times[1] / 10);
        System.out.println("> Total time: " + totalTime);

        System.out.println("> Words per Minute: " + calculateWordPerMinute(times[0]));
    }

    private long[] calculateTotalTime(Instant start, Instant end){
        Duration duration = Duration.between(start, end);

        // get seconds and milliseconds
        long seconds = duration.getSeconds();
        long milliseconds = duration.toMillis() % 1000;

        return new long[]{seconds, milliseconds};
    }

    private double calculateWordPerMinute(long seconds){

        // get words / string
        int wordCount = gameStr.split("").length;

        // get words / second
        return (double) wordCount / (double) seconds;
    }

}
