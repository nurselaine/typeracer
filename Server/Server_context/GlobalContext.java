package Server.Server_context;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class GlobalContext {

    public UserCache userCache;
    public Queue<UserContext> waitingQueue;
    public GameSession gameCache;
    public double HighestScore;

    // binary semaphore to manage access to global context
    public static Semaphore globalContextSem;

    // binary semaphore to manage access to user cache
    public static Semaphore userCacheSem;

    // binary semaphore to manage access to waiting queue
    public static Semaphore waitQueueSem;

    public GlobalContext(UserCache userCache, GameSession gameCache){
        this.userCache = userCache;
        this.gameCache = gameCache;
        this.waitingQueue = new LinkedList<>();
        this.HighestScore = 0.0;
        // binary semaphore to manage access to global context
        globalContextSem = new Semaphore(1);
        // binary semaphore to manage access to user cache
        userCacheSem = new Semaphore(1);
        // binary semaphore to manage access to user cache
        waitQueueSem = new Semaphore(1);
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
        userCache.addNewUser(user);
        return true;
    }
    
    public boolean authenticateUser(String userName, String password){
        return userCache.authenticateUser(userName, password);
    }
}
