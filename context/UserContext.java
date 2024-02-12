package context;

public class UserContext {

    // unique user identifier
    private String username;

    // encrypted passcode for user authentication
    private String password;

    // user's socket that connected to server
    private String socketID;

    // represent total games user has played
    private int gameCount;

    // Represents cumulative wins user has
    private int totalWins;
    // STATUS represents the user's current game condition
    // (connect, playing, waiting, disconnected)
    private enum STATUS {
        PLAYING,
        WAITING,
        CONNECTED,
        DISCONNECTED
    }

    public STATUS userStatus;



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

    public void endGame(){
        this.userStatus = STATUS.CONNECTED;
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
}
