package Server_context;

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

    public void setHighestScore(double score){
        this.HighestScore = score;
    }

    public double getHighestScore(){
        return this.HighestScore;
    }

    public void addUserToQueue(UserContext user){
        waitingQueue.add(user);
    }

    public void removeUserFromQueue(UserContext user){
        waitingQueue.remove(user);
    }

    public boolean isUserInQueue(UserContext user){
        return waitingQueue.contains(user);
    }

    public boolean isUsernameExist(String userName){
        return userCache.validateUsername(userName);
    }

    public boolean addUser(UserContext user){
        if(userCache.validateUsername(user.getUsername())){
            return false;
        }
        userCache.addUser(user);
        return true;
    }
}
