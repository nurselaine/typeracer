package context;

import java.util.LinkedList;
import java.util.Queue;

public class GlobalContext {

    public UserCache userCache;
    public Queue<UserContext> waitingQueue;
    public GameSession gameCache;
    public double HighestScore;

    public GlobalContext(UserCache userCache, GameSession gameCache){
        this.userCache = userCache;
        this.gameCache = gameCache;
        this.waitingQueue = new LinkedList<>();
        this.HighestScore = 0.0;
    }


}
