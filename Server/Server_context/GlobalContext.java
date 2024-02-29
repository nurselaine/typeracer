package Server.Server_context;

import java.io.IOError;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class GlobalContext {

    public UserCache userCache;
    public Queue<UserContext> waitingQueue;
    public GameCache gameCache;
    public double HighestScore;
    Semaphore createUserSemaphore;

    public GlobalContext(UserCache userCache, GameCache gameCache){
        this.userCache = userCache;
        this.gameCache = gameCache;
        this.waitingQueue = new LinkedList<>();
        this.HighestScore = 0.0;

        createUserSemaphore = new Semaphore(1);
    }

    public UserContext creatUser(String socketId, String username, String password)throws InterruptedException, IOException 
    {
        createUserSemaphore.acquire();
        if(!isValidUserName(username)){
            return new UserContext("null", "null", "null");
        }

        UserContext user = new UserContext(socketId, username, password);
        userCache.addNewUser(user);
        createUserSemaphore.release();
        return user;
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

    public boolean isValidUserName(String username){
        return userCache.isValidUserName(username);
    }

    public boolean addUser(UserContext user){
        if(userCache.isValidUserName(user.getUsername())){
            return false;
        }
        userCache.addNewUser(user);
        return true;
    }
    
    public boolean authenticateUser(String userName, String password){
        return userCache.authenticateUser(userName, password);
    }

    
    void addUserToWaitingQueue(UserContext user){
        waitingQueue.add(user);
    }

    void removeUserFromWaitingQueue(UserContext user){
        waitingQueue.remove(user);
    }

    void initNewGame(){
        // gameCache.addGame();
    }


}
