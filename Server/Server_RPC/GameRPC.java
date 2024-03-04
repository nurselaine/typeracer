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


//        int playersNeeded = 0;
//        if (size == 2) {
//            return 0;
//        } else if (size > 2){
//            size = size % 2;
//        }
//        playersNeeded = 2 - size;

        return size;
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
            players.stream().forEach(player -> player.joinGame(game.gameID));
//            gameSession.newGame(game);

            // generate game string and send to each client
            String gameString = game.randomlyGenerateString();
            System.out.println("Send game string to each player...");
            players.stream().forEach(player -> player.out.println(gameString));
            System.out.println("game string " + gameString);

            return players;
        });

        timeoutExecuter.schedule(() -> {
            if(!future.isDone()){
                future.cancel(true); // cancel task if its not done
                System.err.println("Game execution timed out");
            }
        }, 300, TimeUnit.SECONDS);

//        try {
//            // get future object of the players list
//            future.get();
//        } catch (InterruptedException | ExecutionException e) {
//            System.err.println("ERROR: Game unable to retrieve players updated scores " + e.getMessage());
//            e.printStackTrace();
//        }

        return null; // return game to add to gameSessions in driver
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
