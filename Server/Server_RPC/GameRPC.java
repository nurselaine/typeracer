package Server.Server_RPC;

import Server.Server_context.GameContext;
import Server.Server_context.GameSession;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserContext;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class GameRPC {

    private PrintWriter out;
    private BufferedReader in;

    private int MAX_PLAYERS = 4;

    private GlobalContext globalContext;
    private Queue<UserContext> waitQueue;

    public GameRPC (PrintWriter clientWriter, BufferedReader clientReader, GlobalContext globalContext){
        this.out = clientWriter;
        this.in = clientReader;
        this.globalContext = globalContext;
        this.waitQueue = globalContext.waitingQueue;
    }
    // join waiting queue
    public int joinWaitQueue(GlobalContext globalContext, UserContext user){
        try{
            globalContext.waitQueueSem.acquire();
            user.joinWaitQueue(); // update user status
            waitQueue.add(user);
            globalContext.waitQueueSem.release();
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
        if (size >= 4) {
            size = size % 4;
        }
        playersNeeded = 4 - size;

        return playersNeeded;
    }

    // start game
    public GameContext startGame(GlobalContext globalContext, GameSession gameSession){
        // check if at least 4 players are in wait queue
        if(waitQueue.size() < 4) return null;

        // remove 4 players from waiting queue


        List<UserContext> players = new ArrayList<>();

        // create new thread to process game
        Thread gameThread = new Thread(() -> {

            // create new game context with all of the game players

            GameContext game = new GameContext(players);
            int gameID = game.gameID;
            gameSession.newGame(game);
            players.stream().forEach(player -> player.joinGame(gameID));
            String gameString = game.randomlyGenerateString();
            players.stream().forEach(player -> notifyGameStartCountdown(gameString));
        });
        gameThread.start();

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

    public boolean removeFromWaitQueue(GlobalContext globalContext, UserContext user){
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
