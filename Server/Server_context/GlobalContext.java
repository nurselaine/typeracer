package Server.Server_context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class GlobalContext {

    public UserCache userCache;
    public Queue<UserContext> waitingQueue;
    public GameCache gameCache;
    public double HighestScore;
    private Semaphore waitQueSemaphore;
    final int MAX_PLAYERS = 4;

    private int countPlayers;

    public GlobalContext(UserCache userCache, GameCache gameCache) {
        this.userCache = userCache;
        this.gameCache = gameCache;
        this.waitingQueue = new LinkedList<>();
        this.HighestScore = 0.0;
        this.countPlayers = 0;
    }

    private void incrementCountPlayers() {
        countPlayers++;
    }

    private void decrementCountPlayers() {
        countPlayers--;
    }

    private int numPlayerInQueue() {
        return waitingQueue.size();
    }

    public void joinWaitQueue(String user) {
        int waitQueueSize = waitingQueue.size();
        try {
            waitQueSemaphore.acquire();
            waitingQueue.add(userCache.getUser(user));
            waitQueSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(waitQueueSize % 4 == 0 && waitQueueSize != 0) {
            startGame();
        }
    }

    public void leaveWaitQueue(String user) {
        try {
            waitQueSemaphore.acquire();
            waitingQueue.remove(userCache.getUser(user));
            waitQueSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        // create new thread to process game
        Thread gameThread = new Thread(() -> {

            // pop 4 players from the queue and add to list players 
            List<UserContext> players = waitingQueue.stream().limit(MAX_PLAYERS) 
            .collect(Collectors.toList());
            
            players.forEach(player -> waitingQueue.remove(player));

            Game game = new Game(players);

            gameCache.addGame(game); 
            
            players.stream().forEach(player -> player.joinGame(game.getGameID()));
            
            game.startGame();
        });

        gameThread.start();
    }

    public void setHighestScore(double score) {
        this.HighestScore = score;
    }

    public double getHighestScore() {
        return this.HighestScore;
    }

    public void addUserToQueue(UserContext user) {
        waitingQueue.add(user);
    }

    public void removeUserFromQueue(UserContext user) {
        waitingQueue.remove(user);
    }

    public boolean isUserInQueue(UserContext user) {
        return waitingQueue.contains(user);
    }

    public boolean isUsernameExist(String userName) {
        return userCache.validateUsername(userName);
    }

    public boolean addUser(UserContext user) {
        if (userCache.validateUsername(user.getUsername())) {
            return false;
        }
        userCache.addNewUser(user);
        return true;
    }

    public boolean authenticateUser(String userName, String password) {
        return userCache.authenticateUser(userName, password);
    }
}
