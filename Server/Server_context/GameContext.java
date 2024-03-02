package Server.Server_context;

import java.util.List;

public class GameContext {

    /**
     * Checker class that validates the incoming client typed string
     * handles RPCs
     * - will have timeout if player does not type correctly
     *
     * Timer starts and client must type all of string - on submission is when
     * string type matching is done and the game results are calculated based off
     * of users time
     *
     * For each game created can be instantiated by threads and thread will process
     * game until it finishes
     * */

    // game identifier
    public static int gameID = 0;

    // players in game
    private List<UserContext> players;

    /**
     * After timeout period of no interaction - kick player out of game session
     * and put player back into LOGIN status
     * */
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

    /**
     * Use third-party library to generate random string or create a random string generator
     * */
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
    public String randomlyGenerateString(){
        // use this as test string for now
        return "I like to code.";
    }

    // Return list of players in game
    public List<UserContext> getPlayers(){
        return players;
    }

    // Add players that have been disconnected from game/server
    public void stopWatchingPlayer(UserContext user){

    }

    public int getGameID(){
        return this.gameID;
    }

    // TODO: create an RPC to check how many players have finished
    // expect each client to send a confirmation that they are done
    // playing (typing) their string
    // input: client times

    // TODO: Create RPC to notify ALL players of their score and place

}
