package Server.ServerContext;

import Server.ServerContext.UserCache;

import Server.ServerContext.GlobalContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.security.sasl.AuthorizeCallback;
import javax.xml.crypto.Data;

import Server.Game.Game;
import Server.Game.GameCache;

import Server.ServerContext.RPC.Authenticator;
import Server.ServerContext.User.STATUS;
import Server.ServerContext.ClientHandler;


public class GlobalContext {

    GameCache gameCache;

    // maximum number of players that can be in a game  
    private final int MAX_PLAYERS = 4;

    Queue waitingQueue;

    public GlobalContext(GameCache gameCache) {
        this.gameCache = gameCache;
        waitingQueue = new LinkedList();
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

        if (user.getStatus() != STATUS.PLAYING) {
            System.out.println("User is not in game");
            clientHandler.sendMessage("0");
            return;
        }

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

        if (game == null) {
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

        // get response from client
        String response;
        boolean isCorrect = false;

        do {
            response = clientHandler.receiveMessage();
            isCorrect = response.equals(typeString);

            if (isCorrect) {
                System.out.println("Correct string inputed");
                clientHandler.sendMessage("1");
                user.setStatus(STATUS.LOGGEDIN);
                // return;
            }

            else {
                System.out.println("Incorrect string inputed");
                clientHandler.sendMessage("0");
            }

        } while (!isCorrect);

        // receive score from user
        // get typing time
        String totalTime = clientHandler.receiveMessage();

        // validate whether total time is double values

        // cast to double value
        Double userScore = Double.parseDouble(totalTime);

        // update User last score
        user.updateLastScore(userScore);

        System.out.println(user.getUsername() + " has scored " + userScore);

        // output scores
        game.incrementFinishedPlayers();
        String finalScores;
        while (!game.finished()) {
            System.out.println("Game is finished " + game.finished());
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                // System.out.println("Game " + game.getGameID() + " timeout reached");
                e.printStackTrace();
            }
        }
        finalScores = game.getScoresForAll();
        clientHandler.sendMessage(finalScores);
    }

    public void checkWaitTime(ClientHandler clientHandler) {

        User user = userCache.getUser(clientHandler.getUsername());
        STATUS userStatus = user.getStatus();

        if (user.getStatus() == STATUS.WAITING) {
            // send message to bypass WaitForGameStart
            // thread in client listenig for game start
            clientHandler.sendMessage("0");
        }

        else if (userStatus == STATUS.PLAYING) {
            // user is in waiting state handle it in client side
            clientHandler.sendMessage("2");
            return;
        }

        System.out.println("Checking wait time");

        clientHandler.sendMessage("1");

        // get number of players required to start game
        int waitTime = MAX_PLAYERS - waitingQueue.size();

        // send wait time to client
        clientHandler.sendMessage(Integer.toString(waitTime));

    }

    public void setUserToLoginState(ClientHandler clientHandler) {
        System.out.println("Setting user to login state");
        clientHandler.getUsername();

        User user = userCache.getUser(clientHandler.getUsername());

        user.setStatus(STATUS.LOGGEDIN);

        user.setGameID(-1);
    }

}