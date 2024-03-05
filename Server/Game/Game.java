package Server.Game;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import Server.ServerContext.ClientHandler;
import Server.ServerContext.User;

public class Game {

    private static int gameID;
    AtomicInteger counter = new AtomicInteger(0);
    private boolean isGameRunning;
    private int maxPlayers;

    private String typeString;

    ArrayList<User> players;

     
    public Game(ArrayList<User> players, int maxPlayers) {
        gameID = counter.incrementAndGet();
        maxPlayers = maxPlayers;
        this.players = players;
        this.isGameRunning = true;

    }

    public void notifyUsersOfGameReady() {
        for (User player : players) {
            ClientHandler client = player.getClientHandler();

            // notify client that game is ready
            client.sendMessage("GameStart");

        }
    }

    public int getGameID() {
        return gameID;
    }   

    public String getTyppeString() {
        return typeString;
    }

}
