package Server.Server_RPC;

import Server.Server_context.GameContext;
import Server.Server_context.GameSession;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserContext;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;

public class GameRPC {

    private PrintWriter out;
    private BufferedReader in;

    private final int MAX_PLAYERS = 4;

    private GlobalContext globalContext;
    private Queue<UserContext> waitQueue;
    private Semaphore waitQueueSem;

    private GameContext game;

    public GameRPC (PrintWriter clientWriter, BufferedReader clientReader, GlobalContext globalContext){
        this.out = clientWriter;
        this.in = clientReader;
        this.globalContext = globalContext;
        this.waitQueue = globalContext.waitingQueue;
        this.waitQueueSem = globalContext.waitQueueSem;
    }
    // join waiting queue
    public int joinWaitQueue(UserContext user){
        try{
            waitQueueSem.acquire();
            System.out.println("ADDING USER TO WAIT QUEUE");
                waitQueue.add(user);
            waitQueueSem.release();
            user.joinWaitQueue(); // update user status
            System.out.println("WAIT QUEUE SIZE : " + waitQueue.size());
            return waitQueue.size();
        } catch (InterruptedException e){
            System.out.println("Client unable to join waiting queue " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // returns # of users in game queue
    public int checkWaitTime(GlobalContext globalContext) throws InterruptedException {
        waitQueueSem.acquire();
        int size = waitQueue.size();
        waitQueueSem.release();

        return size;
    }

    // start game
    public void startGame(List<UserContext> gamePlayer)  {
        //         send string to play game
        for(int i = 0; i < gamePlayer.size(); i++){
            gamePlayer.get(i).out.println("I love to code");
        }

        // wait for each player to send score back
        UserContext player1 = gamePlayer.get(0);
        player1.updateLastScore(Double.parseDouble(player1.readMessage()));
        UserContext player2 = gamePlayer.get(1);
        player2.updateLastScore(Double.parseDouble(player2.readMessage()));

        // printout last scores
        for(int i = 0; i < gamePlayer.size(); i++){
            System.out.println("Score for player: " + gamePlayer.get(i).getUsername() + " : " + gamePlayer.get(i).getLastScore());
        }

        // sort game players by last score
        Collections.sort(gamePlayer, (a, b) -> Double.compare(a.getLastScore(), b.getLastScore()));

        // send each player results and scores (1st, 2nd, 3rd, 4th)
        processGameScore(gamePlayer, gamePlayer.get(0), gamePlayer.get(1));

        // update each players status
        endGame(gamePlayer);

    }

    private void processGameScore(List<UserContext> gamePlayer, UserContext player1, UserContext player2){
        try {
            // wait for all players to send in scores
            while(player1.getLastScore() == 0.0 && player2.getLastScore() == 0.0){
                TimeUnit.SECONDS.sleep(2);
            }

            // once all scores are read in by server then create score string
            String gameResults = "1st " + player1.getUsername() + " - " + player1.getLastScore() + ":"
                    + "2st " + player2.getUsername() + " - " + player2.getLastScore() + "\n";

            System.out.println(gameResults);
            // message each client the scores
            for(int i = 0; i < 2; i++){ // 2 for testing purposes
                gamePlayer.get(i).outLock.acquire();
                gamePlayer.get(i).out.println(gameResults); // sends client back the place first, seconds, third..
                gamePlayer.get(i).outLock.release();
            }
        } catch (InterruptedException e) {
            System.out.println("ERROR: sending game scores to game players " + e.getMessage());
            e.printStackTrace();
        }
    }

    // endGame
    public void endGame(List<UserContext> gamePlayers){
        // clean up and update player status
        for(int i = 0; i < gamePlayers.size(); i++){
            gamePlayers.get(i).endGame();
        }
    }

    public boolean removeFromWaitQueue(UserContext user){
        if(waitQueue.contains(user)){
            System.out.println("Removing client from wait queue");
            waitQueue.remove(user);
            user.updateStatus(UserContext.STATUS.LOGGEDIN);
            return true;
        }
        return false;
    }

    public void trackCompletion(){
        // client will call this RPC each time a user completes typing
        // it will track how many users are left and whether the timeout limit has been reached
        //
    }

    // game score
}
