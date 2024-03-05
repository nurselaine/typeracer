package Server.Game;
import java.util.ArrayList;

import Server.ServerContext.User;

public class GameCache {

    private static int totalGames;

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
     
    // public void initNewGame(ArrayList<User> players, int maxPlayers) {
    //     Thread gamThread = new Thread(new Runnable() {
    //         @Override
    //         public void run() {
    //             Game game = new Game(players, maxPlayers);
    //             addGame(game);
    //         }
    //     });
    //     Game game = new Game(players, maxPlayers);
    //     addGame(game);
    // }
}
