package Server.ServerContext;

import Server.ServerContext.UserCache;

import Server.ServerContext.GlobalContext;

import java.io.IOException;
import java.util.ArrayList;
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

    // maximum number of players that can be in a game  
    private final int MAX_PLAYERS = 2;

    Authenticator authenticator;

    Semaphore userCacheSemaphore;

    Queue waitingQueue;

    public GlobalContext(UserCache userCache, GameCache gameCache) {
        this.userCache = userCache;
        this.gameCache = gameCache;
        userCacheSemaphore = new Semaphore(1);
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
            user.setClinetHandler(clientHandler);


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

    public void logout(ClientHandler clientHandler) {
        System.out.println("Logging out user");
        userCache.logoutUser(clientHandler.getUsername());
        clientHandler.sendMessage("1");
    }

    public void quit(ClientHandler clientHandler) {
        System.out.println("Quitting user");
        clientHandler.sendMessage("1");
    }

    public void enterWaitList(ClientHandler clientHandler) {

        System.out.println("Entering user into wait list");
        String username = clientHandler.getUsername();

        User user = userCache.getUser(username);

        boolean canEnterWaitList = userCache.canEnterWaitList(user);

        if (!canEnterWaitList) {
            // send message to client that user is already in wait list
            System.out.println("User " + clientHandler.getUsername() + " is already in wait list");

            // client message send failed
            clientHandler.sendMessage("0");
            return;
        }

        System.out.println("User " + clientHandler.getUsername() + " entered into wait list");

        // add user to waiting queue 
        user.updateStatus(STATUS.WAITING);
        waitingQueue.add(user);

        // client message send success
        clientHandler.sendMessage("1");

        // if enough players are in waiting queue then
        // intiate a new game
        if (isEnoughToStartGame()) {
            initNewGame();
        }
    }

    public boolean isEnoughToStartGame() {
        return waitingQueue.size() >= 2;
    }

public void initNewGame() {
        // pop users from waiting queue
        // and add them to a new game palyers list
        ArrayList<User> players = new ArrayList<User>();
        for (int i = 0; i < MAX_PLAYERS; i++) {
            User user = (User) waitingQueue.poll();
            user.updateStatus(STATUS.PLAYING);
            players.add(user);
        }
        
        // Create a new game instance and add it to the game cache
        Game game = new Game(players, MAX_PLAYERS);
        gameCache.addGame(game);
}


    public void leaveWaitList(ClientHandler clientHandler) {
        System.out.println("Leaving user from wait list");

        // send 0 to tell the wating thread in client to stop
        clientHandler.sendMessage("0");

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
        user.updateStatus(STATUS.LOGGEDIN);
        waitingQueue.remove(user);
        clientHandler.sendMessage("1");
    }

    public void enterGame(ClientHandler clientHandler) {
        String userName = clientHandler.getUsername();

        User user = userCache.getUser(userName);

        boolean canEnterGame = userCache.canEnterGame(user);

        if (!canEnterGame) {
            // send message to client that user is not allowed to enter game
            System.out.println("User " + clientHandler.getUsername() + " is not allowed to enter game");
            System.out.println("You will be notfied when game ready to start"); 
            clientHandler.sendMessage("0");
            return;
        }

        // send message to client that user is allowed to enter game
        System.out.println("Entering user into game");
        clientHandler.sendMessage("1");
    }

  
    public void playGame(ClientHandler clientHandler) throws IOException {
        String username = clientHandler.getUsername();

        User user = userCache.getUser(username);    

        int gameID = user.getGameID();

        Game game = gameCache.getGame(gameID);

        if(game == null){
            System.out.println("Game not found");

            // client message send plaly game failed 
            clientHandler.sendMessage("0");
            return;
        }

        System.out.println("Playing game");

        // client message send plaly game success
        clientHandler.sendMessage("1");

        String typeString = game.getTyppeString();

        // send typeString to client
        clientHandler.sendMessage(typeString);

        //get response from client
        String response;
        boolean isCorrect = false;

        do{
            response = clientHandler.receiveMessage();
            isCorrect = response.equals(typeString);

            if(isCorrect){
                System.out.println("Correct string inputed");
                clientHandler.sendMessage("1");
                return;
            }

            else{
                System.out.println("Incorrect string inputed");
                clientHandler.sendMessage("0");
            }

        } while(!isCorrect);


        if (response.equals(typeString)) {
            System.out.println("Correct string inputed");
            clientHandler.sendMessage("1");
            return;
        }
        else if(response.equals("0")){
            System.out.println("User is not in game");
            return;
        }

    }
}
