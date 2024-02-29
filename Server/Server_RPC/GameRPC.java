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

    private PrintWriter clientWriter;
    private BufferedReader clientReader;

    public GameRPC (PrintWriter clientWriter, BufferedReader clientReader){
        this.clientWriter = clientWriter;
        this.clientReader = clientReader;
    }
    // join waiting queue
    public int joinWaitQueue(GlobalContext globalContext, UserContext user, Semaphore globalContextSem){
        Queue<UserContext> waitQueue = globalContext.waitingQueue;
        try{
            globalContextSem.acquire();
            user.joinWaitQueue(); // update user status
            waitQueue.add(user);
            globalContextSem.release();
            return waitQueue.size();
        } catch (InterruptedException e){
            System.out.println("Client unable to join waiting queue " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
//        if(waitQueue.size() % 4 != 0){
//            int remainPlayers = waitQueue.size() % 4;
//            if(remainPlayers == 1){
//                clientWriter.println("Waiting for " + remainPlayers + " player to join");
//            } else {
//                clientWriter.println("Waiting for " + remainPlayers + " players to join");
//            }
//        } else {
//            clientWriter.println("Game will begin in a few seconds...");
//        }
    }

    public int checkWaitTime(GlobalContext globalContext){
        Queue<UserContext> waitQueue = globalContext.waitingQueue;
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
        Queue<UserContext> waitQueue = globalContext.waitingQueue;
        if(waitQueue.size() < 4) return null;

        // create new thread to process game
        Thread gameThread = new Thread(() -> {
            List<UserContext> players = new ArrayList<>();
            GameContext game = new GameContext(players);
            int gameID = game.gameID;
            gameSession.newGame(game);
            players.stream().forEach(player -> player.joinGame(gameID));
            String gameString = game.randomlyGenerateString();
            players.stream().forEach(player -> notifyGameStartCountdown(gameString));
        });

        return null; // return game to add to gameSessions in driver
    }

    private void notifyGameStartCountdown(String gameString){
        int count = 3;
        String[] readySetGo = new String[]{"Ready", "Set", "Go!"};
        int rsgIndex = 0;
        try {
            while(count > 0){
                clientWriter.println(readySetGo[rsgIndex++] + "...");
                wait(1000);
            }
            // send string
            clientWriter.println(gameString);
        } catch (InterruptedException e){
            System.out.println("GAME START ERROR: occured during countdown " + e.getMessage());
            clientWriter.println("Error joining game. Please rejoin waiting queue");
        }
    }

    // endGame
    public void endGame(GameContext game){

    }

    public void removeFromWaitQueue(GlobalContext globalContext, UserContext user){
        user.updateStatus(UserContext.STATUS.LOGGEDIN);
        Queue<UserContext> waitQueue = globalContext.waitingQueue;
        if(waitQueue.contains(user)){
            waitQueue.remove(user);
        }
    }

    // game score

    // typing rpc
}
