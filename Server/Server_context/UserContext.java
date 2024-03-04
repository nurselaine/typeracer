package Server.Server_context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.Buffer;

public class UserContext {

    // unique user identifier
    private String username;

    // passcode for user authentication
    private String password;

    // user's socket that connected to server
    private String socketID;

    // represent total games user has played
    private int gameCount;

    // Represents cumulative wins user has
    private int totalWins;
    private double lastScore;

    private STATUS userStatus;

    public BufferedReader in;
    public PrintWriter out;

    /**
     * Put enum in its own class so it can be available to all classes for status updates
     * */
    // STATUS represents the user's current game condition
    // (connect, playing, waiting, disconnected)
    public enum STATUS {
        PLAYING, // joined active game
        WAITING, // entered waiting queue
        LOGGEDIN, // user has created/entered credentials
        CONNECTED, // use just got accepted to server
        DISCONNECTED // no longer connected to server
    }


    // Game status is an ID that is assigned when user enters a game
    private int gameID = -1;

    //
    public UserContext(String socketID, String username, String password, BufferedReader in, PrintWriter out){
        this.socketID = socketID;
        this.username = username;
        this.password = password;
        this.userStatus = STATUS.CONNECTED;
        this.in = in;
        this.out = out;
    }

    public void joinGame(int gameID){
        // only users in wait queue can join game
        if(userStatus == STATUS.WAITING){
            this.gameID = gameID;
            this.userStatus = STATUS.PLAYING;
            this.gameCount++;
        }
    }

    public void login(){ this.userStatus = STATUS.LOGGEDIN; }

    public void endGame(long score){
        this.lastScore = score;
        this.userStatus = STATUS.LOGGEDIN;
    }

    public void joinWaitQueue(){
        this.userStatus = STATUS.WAITING;
    }

    public void disconnectUser(){
        this.userStatus = STATUS.DISCONNECTED;
    }

    public void updateTotalWin(){
        this.totalWins++;
    }

    public String getUsername(){
        return username;
    }

    public void updateUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }
    public void updatePassword(String password){
        this.password = password;
    }

    public String getSocketID(){
        return this.socketID;
    }

    public STATUS getStatus(){
        return this.userStatus;
    }

    public int getGameCount(){
        return this.gameCount;
    }

    public int getTotalWins(){
        return this.totalWins;
    }

    public void updateStatus(STATUS status) { this.userStatus = status; }

    // using words/min as score
    public void updateLastScore(double score){
        this.lastScore = score;
    }

    public double getLastScore(double score){
        return this.lastScore;
    }

    public void startGameCode(){

        this.out.println(400);
    }
}
