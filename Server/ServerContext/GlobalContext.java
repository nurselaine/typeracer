package Server.ServerContext;

import Server.ServerContext.UserCache;

import Server.ServerContext.GlobalContext;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import javax.security.sasl.AuthorizeCallback;

import Server.Game.Game;
import Server.Game.GameCache;

import Server.ServerContext.RPC.Authenticator;
import Server.ServerContext.User.STATUS;
import Server.ServerContext.ClientHandler;

public class GlobalContext {

    GameCache gameCache;
    UserCache userCache;

    Authenticator authenticator;

    Semaphore userCacheSemaphore;

    Semaphore waitQueueSemaphore;

    Queue<User> waitingQueue;

    public GlobalContext(UserCache userCache, GameCache gameCache) {
        this.userCache = userCache;
        this.gameCache = gameCache;
        userCacheSemaphore = new Semaphore(1);
        waitQueueSemaphore = new Semaphore(1);
        waitingQueue = new LinkedList();
        

    }

    public void registerUser(ClientHandler clientHandler) throws Exception {

        System.out.println("Registering user");

        // get userName from client
        String userName = clientHandler.receiveMessage();
        System.out.println("Received username: " + userName);

        boolean isUserNameTaken = userCache.isUserNameTaken(userName);

        if (isUserNameTaken) {
            // send message to client that username is taken
            System.out.println("Username " + userName + " is taken");
            clientHandler.sendMessage("0");
            return;
        }

        // send message to client that username is available
        System.out.println("Username " + userName + " is available");
        clientHandler.sendMessage("1");

        // get password from client
        String password = clientHandler.receiveMessage();

        // create User object
        User user = new User(clientHandler.getSocketAddress(), userName, password);

        // add user to user cache
        userCache.addNewUser(user);

        // send message to client that user is registered successfully
        clientHandler.sendMessage("1");
    }

    public User login(ClientHandler clientHandler) throws IOException {
        // get userName from client
        String userName = clientHandler.receiveMessage();
        System.out.println("Received username: " + userName);

        // get password from client
        String password = clientHandler.receiveMessage();
        System.out.println("Received password: " + password);

        boolean isUserInCache = userCache.isUserInCache(userName);

        if (!isUserInCache) {
            // send message to client that username does not exist
            clientHandler.sendMessage("0");
            return null;
        }

        // make sure user is not already logged in and if not then log in user when
        // password is correct
        int authCode = userCache.authenticateUser(userName, password);

        if (authCode == 1) {
            // user already connected
            System.out.println("User already logged in");
            clientHandler.sendMessage("1");
            return null;
        }

        if (authCode == 2) {
            // incorrect password
            System.out.println("Invalid password");
            clientHandler.sendMessage("2");
        }

        if (authCode == 3) {
            // correct password
            System.out.println("User " + userName + " logged in successfully");
            clientHandler.sendMessage("3");
            User user = userCache.getUser(userName);
            clientHandler.setUser(user);
            return user;
        }

        if (authCode == -1) {
            // unknown error
            System.out.println("Unknown error");
            clientHandler.sendMessage("-1");
            return null;
        }

        return null;
    }

    /**
     * This method handles logging user out and updating user
     * state to CONNECTED state. It will also remove client from wait
     * queue if necessary.
     * 
     * @param clientHandler object that contains client context and 
     * handlers for sending and receiving client messages
    */
    public void logout(ClientHandler clientHandler) throws InterruptedException {
        System.out.println("Logging out user");
        User user = userCache.getUser(clientHandler.getUsername());
        userCache.logoutUser(clientHandler.getUsername()); // this method updates user to CONNECTED
        
        // remove client from wait queue if necessary
        waitQueueSemaphore.acquire(1);
        if(userCache.canLeaveWaitList(user)){
            waitingQueue.remove(user);
        }
        waitQueueSemaphore.release();
        clientHandler.sendMessage("1");
    }

    public void quit(ClientHandler clientHandler) throws InterruptedException {
        User user = userCache.getUser(clientHandler.getUsername());
        System.out.println(user.getUsername() + ": Quitting user");
        logout(clientHandler);
        // update status to disconnected
        user.updateStatus(STATUS.DISCONNECTED);
        clientHandler.sendMessage("1");
    }

    public void enterWaitList(ClientHandler clientHandler) throws InterruptedException {

        System.out.println("Entering user into wait list");
        String username = clientHandler.getUsername();

        User user = userCache.getUser(username);

        boolean canEnterWaitList = userCache.canEnterWaitList(user);

        if (!canEnterWaitList) {
            // send message to client that user is already in wait list
            System.out.println("User " + clientHandler.getUsername() + " is already in wait list");
            clientHandler.sendMessage("0");
            return;
        }
        System.out.println("User " + clientHandler.getUsername() + " entered into wait list");

        // add user to waiting queue and send message to client that user is in wait
        // list
        waitQueueSemaphore.acquire(1);
        waitingQueue.add(user);
        waitQueueSemaphore.release();
        user.updateStatus(STATUS.WAITING);
        clientHandler.sendMessage("1");
    }


    public void leaveWaitList(ClientHandler clientHandler) throws InterruptedException {
        System.out.println("Leaving user from wait list");
        String username = clientHandler.getUsername();

        User user = userCache.getUser(username);

        boolean canLeaveWaitList = userCache.canLeaveWaitList(user);

        if (!canLeaveWaitList) {
            // send message to client that user is not in wait list
            System.out.println("User " + clientHandler.getUsername() + " is not in wait list");
            clientHandler.sendMessage("0");
            return;
        }
        System.out.println("User " + clientHandler.getUsername() + " left wait list");

        // remove user from waiting queue and send message to client that user is not in
        // wait list
        waitQueueSemaphore.acquire(1);
        waitingQueue.remove(user);
        waitQueueSemaphore.release();
        clientHandler.sendMessage("1");
    }
}
