package Server.Server_context;

import java.util.ArrayList;
import java.util.List;

public class GameCache {

    public List<Game> gameList;


    public GameCache(){
        gameList = new ArrayList<>();
    }

    public void addGame(Game game){
        gameList.add(game);
    }

    public int totalGames(){
        return gameList.size();
    }

    public Game getGame(int gameID){
        return gameList.get(gameID);
    }

}
