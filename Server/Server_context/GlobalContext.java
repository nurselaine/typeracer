package Server.Server_context;
import java.util.concurrent.Semaphore;

public class GlobalContext{
    private UserCache userCache;
    private GameCache gameCache;
   
    private Semaphore createUserSemaphore;
    private Semaphore authenticatUserSemaphore;
    
    public GlobalContext(UserCache userCache, GameCache gameCache){
        this.userCache = userCache;
        this.gameCache = gameCache;
        this.createUserSemaphore = new Semaphore(1);
        this.authenticatUserSemaphore = new Semaphore(1);
    }

    public boolean validateUsername(String username) throws InterruptedException
    {

        createUserSemaphore.acquire();
        boolean isValidUserName = userCache.validateUsername(username);
        createUserSemaphore.release();
        return isValidUserName;
    }

    public boolean addUser(User user) throws InterruptedException{
        createUserSemaphore.acquire();
        userCache.addNewUser(user);
        
        // validate that user was added to userCache successfully 
        boolean isAdded = getUser(user.getUsername()).equals(user);
        createUserSemaphore.release();
        return isAdded;
    }

    public User getUser(String username){
        return userCache.getUser(username);
    }

    public boolean authenticateUser(String username, String password) throws InterruptedException{
        authenticatUserSemaphore.acquire();
        boolean isValidCredentials = userCache.authenticateUser(username, password);
        authenticatUserSemaphore.release();
        return isValidCredentials;
    } 
}