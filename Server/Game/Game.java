package Server.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import Server.ServerContext.ClientHandler;
import Server.ServerContext.User;

public class Game {

    private int gameID;
    AtomicInteger counter = new AtomicInteger(0);
    private boolean isGameRunning;
    private int maxPlayers;
    private int finishedPlayers;

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
        StringBuilder sb = new StringBuilder();

        Collections.sort(players,(a, b) -> Double.compare(a.getLastScore(), b.getLastScore()));

        for (int i = 0; i < players.size(); i++) {
            sb.append((i + 1) + ". " + players.get(i).getUsername() + " : " + players.get(i).getLastScore() + "\n");
        }

        return sb.toString();
    }

    public void incrementFinishedPlayers() {
        finishedPlayers++;
    }

    public boolean finished() {
        return finishedPlayers == maxPlayers;
    }
}
