package Server.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import Server.ServerContext.ClientHandler;
import Server.ServerContext.User;

public class Game {

    private int gameID;
    AtomicInteger counter = new AtomicInteger(0);
    private boolean isGameRunning;
    private int maxPlayers;

    private String typeString;

    ArrayList<User> players;

    public Game(ArrayList<User> players, int maxPlayers) {
        gameID = counter.incrementAndGet();
        maxPlayers = maxPlayers;
        typeString = "test";
        this.players = players;
        this.isGameRunning = true;
        notifyUsersOfGameReady();
    }

    public void notifyUsersOfGameReady() {
        for (User player : players) {
            ClientHandler client = player.getClientHandler();

            player.setGameID(gameID);

            // notify client that game is ready
            client.sendMessage("GameStart");
        }
    }

    public void generateRandomString() {
        // funny/random chuck norris quotes
        // https://api.chucknorris.io/jokes/random
        try {

            // ensure only 1 game can make a request at once
            URL url = new URL("https://api.chucknorris.io/jokes/random");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json");

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            BufferedReader inAPI = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response;
            StringBuffer content = new StringBuffer();
            while ((response = inAPI.readLine()) != null) {
                content.append(response);
            }

            if (status != 200) {
                System.out.println("ERROR: Randomly generate string bad request to API.");
            }
            // clean up resources
            inAPI.close();
            connection.disconnect();
            System.out.println(content.toString());
        } catch (IOException e) {
            System.out.println("Request to Joke API failed due to URL issues. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getGameID() {
        return gameID;
    }

    public String getTyppeString() {
        return typeString;
    }

    public String getScoresForAll() {
        // once all users are finished typing 
        // or timeout has been reached
        // Each user's attribute for last score should be updated
        // to the last game total typnig time
        // sort the user list by total typing time
        // create a string to send back to each client
        // that displays the score to them
        
        return "";
    }

}
