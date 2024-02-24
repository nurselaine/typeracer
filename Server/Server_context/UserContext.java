package Server.Server_context;

import java.net.SocketAddress;

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

    private long lastScore;

    // STATUS represents the user's current game condition
    // (connect, playing, waiting, disconnected)
    public enum STATUS {
        PLAYING, // joined active game
        WAITING, // entered waiting queue
        LOGGEDIN, // user has created/entered credentials
        CONNECTED, // use just got accepted to server
        DISCONNECTED // no longer connected to server
    }

    private STATUS userStatus;

    // Game status is an ID that is assigned when user enters a game
    private int gameID = -1;

    //
    public UserContext(String socketID, String username, String password){
        this.socketID = socketID;
        this.username = username;
        this.password = password;
        this.userStatus = STATUS.CONNECTED;
    }

    public void joinGame(int gameID){
        this.gameID = gameID;
        this.userStatus = STATUS.PLAYING;
        this.gameCount++;
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

    public String getPassword(){
        return password;
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
}
