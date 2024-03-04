package Server.Server_context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Semaphore;

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
    public int TIMEOUT = 300;

    /**
     * Use third-party library to generate random string or create a random string generator
     * */
    // String to be typed by players
    private String originalString;

    private Semaphore checkNorrisSem;

    // Constructor
    public GameContext(List<UserContext> players){
        this.gameID++;
        this.players = players;
        // update user status for each player
        players.stream().forEach(player -> player.joinGame(gameID));
        this.startTime = System.currentTimeMillis();
        this.originalString = randomlyGenerateString();
    }

    // Generate a random string for players to type
    public String randomlyGenerateString() {
        // funny/random chuck norris quotes
        // https://api.chucknorris.io/jokes/random
//        try {
//
//            // ensure only 1 client can make a request at once
//            checkNorrisSem.acquire();
//                URL url = new URL("https://api.chucknorris.io/jokes/random");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.setRequestProperty("Content-type", "application/json");
//
//                connection.setConnectTimeout(5000);
//                connection.setReadTimeout(5000);
//
//                int status = connection.getResponseCode();
//
//                BufferedReader inAPI = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String response;
//                StringBuffer content = new StringBuffer();
//                while(status < 400 && (response = inAPI.readLine()) != null){
//                    content.append(response);
//                }
//
//                if(status >= 400){
//                    System.out.println("ERROR: Randomly generate string bad request to API.");
//                }
//                // clean up resources
//                inAPI.close();
//                connection.disconnect();
//            checkNorrisSem.release();
//        } catch (IOException | InterruptedException e){
//            System.out.println("Request to Joke API failed due to URL issues. " + e.getMessage());
//            e.printStackTrace();
//        }

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
