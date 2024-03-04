package Server.ServerContext;

public class User {

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

    private STATUS userStatus;

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

    public User(String socketID, String username, String password){
        this.socketID = socketID;
        this.username = username;
        this.password = password;
        this.userStatus = STATUS.DISCONNECTED;
    }

    public User(){
        this.username = "invalid";
        this.password = "invalid";
        this.socketID = "invalid";
        this.userStatus = STATUS.DISCONNECTED;
    }

    public void endGame(long score){
        this.lastScore = score;
        this.userStatus = STATUS.LOGGEDIN;
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

    public void updateStatus(STATUS status) {
        this.userStatus = status;
    }

    /**
     * Override equals method to compare user objects
     */
    @Override
    public boolean equals(Object obj){
        if(obj == this) return true;
        if(!(obj instanceof User)) return false;
        User user = (User) obj;
        return username == user.username 
            && password == user.password; 
    }     

    public void setSentinelValue(){
        this.username = "invalid";
        this.password = "invalid";
        this.socketID = "invalid";
    }
}
