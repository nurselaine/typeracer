package Server.Server_RPC;

import Server.Server_context.GameContext;
import Server.Server_context.GameSession;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserContext;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
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
                waitQueue.add(user);
            waitQueueSem.release();
            user.joinWaitQueue(); // update user status
            return waitQueue.size();
        } catch (InterruptedException e){
            System.out.println("Client unable to join waiting queue " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public int checkWaitTime(GlobalContext globalContext){
        int size = waitQueue.size();
        int playersNeeded = 0;
        if (size == 4) {
            return 0;
        } else if (size > 4){
            size = size % 4;
        }
        playersNeeded = 4 - size;

        return playersNeeded;
    }

    // start game
    public GameContext startGame(List<UserContext> players) throws InterruptedException {

        // use a future with timeout parameter

        ExecutorService pool = Executors.newSingleThreadExecutor();
        ScheduledExecutorService timeoutExecuter = Executors.newSingleThreadScheduledExecutor();

        // returns future object for players list with updated player user context
        // user context should have game scores once thread pool terminates or finishes executing
        Future<List<UserContext>> future = pool.submit(() -> {
                        // create new game context with players
            GameContext game = new GameContext(players);
            System.out.println("Game created and started!");
            System.out.println("Players: " + players.get(0) + " " + players.get(1));
//            System.out.println("Players: " + players.get(0) + " " + players.get(1) + " " + players.get(2) + " " + players.get(3));
            int gameID = game.gameID;
//            gameSession.newGame(game);

            String gameString = game.randomlyGenerateString();
            System.out.println("game string " + gameString);
            players.stream().forEach(player -> notifyGameStartCountdown(gameString));
            return players;
        });

        timeoutExecuter.schedule(() -> {
            if(!future.isDone()){
                future.cancel(true); // cancel task if its not done
                System.err.println("Game execution timed out");
            }
        }, game.TIMEOUT, TimeUnit.SECONDS);

        try {
            // get future object of the players list
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("ERROR: Game unable to retrieve players updated scores " + e.getMessage());
            e.printStackTrace();
        }

//        // create new thread to process game
//        Thread gameThread = new Thread(() -> {
//
//            // create new game context with players
//            GameContext game = new GameContext(players);
//            System.out.println("Game created and started!");
//            System.out.println("Players: " + players.get(0) + " " + players.get(1) + " " + players.get(2) + " " + players.get(3));
//            int gameID = game.gameID;
////            gameSession.newGame(game);
//
//            String gameString = game.randomlyGenerateString();
//            System.out.println("game string " + gameString);
//            players.stream().forEach(player -> notifyGameStartCountdown(gameString));
//        });
//        gameThread.start();

        return null; // return game to add to gameSessions in driver
    }

    // this method is not going to be used
    // count down will be initiated on clientside
    private void notifyGameStartCountdown(String gameString){
        int count = 3;
        String[] readySetGo = new String[]{"Ready", "Set", "Go!"};
        int rsgIndex = 0;
        try {
            while(count > 0){
                out.println(readySetGo[rsgIndex++] + "...");
                wait(1000);
            }
            // send string
            out.println(gameString);
        } catch (InterruptedException e){
            System.out.println("GAME START ERROR: occured during countdown " + e.getMessage());
            out.println("Error joining game. Please rejoin waiting queue");
        }
    }

    // endGame
    public void endGame(GameContext game){

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
