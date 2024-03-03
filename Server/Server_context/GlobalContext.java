package Server.Server_context;
import java.io.ObjectInputFilter.Status;
import java.lang.Thread.State;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class GlobalContext{
    private UserCache userCache;
    private GameCache gameCache;

    private Queue<User> waitQueue;

    private Semaphore createUserSemaphore;
    private Semaphore authenticatUserSemaphore;
    
    public GlobalContext(UserCache userCache, GameCache gameCache){
        this.userCache = userCache;
        this.gameCache = gameCache;
        this.createUserSemaphore = new Semaphore(1);
        this.authenticatUserSemaphore = new Semaphore(1);
        this.waitQueue = new LinkedList<>(); 
    }

    /**
     * Validate username
     * @param username
     * @return true if username is valid
     * @throws InterruptedException
     */
    public boolean validateUsername(String username) throws InterruptedException{


        boolean isValidUserName = userCache.validateUsername(username);


        return isValidUserName;
    }

    /**
     * Add new user to userCache
     * @param user
     * @return true if user was added successfully
     * @throws InterruptedException
     */
    public boolean addUser(User user) throws InterruptedException{
        createUserSemaphore.acquire();

        userCache.addNewUser(user);
        
        // validate that user was added to userCache successfully 
        boolean isAdded = getUser(user.getUsername()).equals(user);

        createUserSemaphore.release();

        return isAdded;
    }
    
    /**
     * Get user from userCache
     * @param username
     * @return User object
     */
    public User getUser(String username){
        return userCache.getUser(username);
    }

    /**
     * Authenticate user credentials
     * 
     * @param username
     * @param password
     * @return true if user credentials are valid
     * @throws InterruptedException
     */
    public boolean authenticateUser(String username, String password) throws InterruptedException {
        authenticatUserSemaphore.acquire();
        boolean isValidCredentials = userCache.authenticateUser(username, password);
        authenticatUserSemaphore.release();
        return isValidCredentials;
    }

    /**
     * add a user to the wait queue
     * 
     * @param user
     * @return
     * @throws InterruptedException
     */
    public boolean joinWaitQueue(User user) throws InterruptedException {
        if(!waitQueue.contains(user)){
            this.waitQueue.add(user);
            user.updateStatus(User.STATUS.WAITING); 
            System.out.println("User added to wait queue");
            return true;
        }

        System.out.println("User already in wait queue");
        return false;
    }

    public boolean removeFromWaitlist(User user) throws InterruptedException{
        if(waitQueue.contains(user)){
            waitQueue.remove(user);
            user.updateStatus(User.STATUS.LOGGEDIN);
            return true;
        }

        return false;
    }
}