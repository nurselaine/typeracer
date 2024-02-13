package context;

import java.util.List;

public class GameContext {

    // game identifier
    public static int gameID = 0;

    // players in game
    private List<UserContext> players;

    // players inactive/disconnected players
    private List<UserContext> ghostPlayers;

    // game start time
    private long startTime;

    // game end time
    private long endTime;

    // Elapsed time total
    private long timeElapsed;

    // Maximum time to play - 500 seconds is 5 minutes
    private int TIMEOUT = 300;

    // String to be typed by players
    private String originalString;

    // Constructor
    public GameContext(List<UserContext> players){
        this.gameID++;
        this.players = players;
        this.startTime = System.currentTimeMillis();
        this.originalString = randomlyGenerateString();
    }

    // Generate a random string for players to type
    private String randomlyGenerateString(){
        // use this as test string for now
        return "I like to code.";
    }

    // Return list of players in game
    public List<UserContext> getPlayers(){
        return players;
    }

    // Add players that have been disconnected from game/server
    public void stopWatchingPlayer(UserContext user){
        if(user.getStatus() == UserContext.STATUS.DISCONNECTED){
            ghostPlayers.add(user);
        }

    }


}
