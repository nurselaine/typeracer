package Server.Server_context;

import java.util.ArrayList;
import java.util.List;

public class GameCache {

    public List<Game> gameList;

    public GameCache(){
        gameList = new ArrayList<>();
    }

    public void newGame(Game game){
        gameList.add(game);
    }

    public int totalGames(){
        return gameList.size();
    }

    public Game getGame(int gameID){
        return gameList.get(gameID);
    }

    public void AddGame(Game game){
        gameList.add(game);
    }

    public void initNewGame(int gameID){
        gameList.get(gameID).initGame();
    }


}
