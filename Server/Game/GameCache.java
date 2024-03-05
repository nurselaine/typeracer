package Server.Game;

import java.util.ArrayList;

public class GameCache {

    private int totalGames;

    ArrayList<Game> games;

    public GameCache() {
        this.games = new ArrayList<Game>();
    }

    public void addGame(Game game) {
        games.add(game);
    }
    
    public Game getGame(int gameID) {
        for (Game game : games) {
            if (game.getGameID() == gameID) {
                return game;
            }
        }
        return null;
    }
}
