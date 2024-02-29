package Server.Server_context;

import java.util.ArrayList;
import java.util.List;

public class GameSession {

    public List<GameContext> gameList;

    public GameSession(){
        gameList = new ArrayList<>();
    }

    public void newGame(GameContext game){
        gameList.add(game);
    }

    public int totalGames(){
        return gameList.size();
    }

    public GameContext getGame(int gameID){
        return gameList.get(gameID);
    }

}
