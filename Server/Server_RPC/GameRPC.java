package Server.Server_RPC;

import Server.Server_context.GlobalContext;
import Server.Server_context.UserContext;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import Server.Server_context.GameContext; // Import the missing GameContext class

public class GameRPC {

    private PrintWriter clientWriter;
    private BufferedReader clientReader;

    public GameRPC (PrintWriter clientWriter, BufferedReader clientReader){
        this.clientWriter = clientWriter;
        this.clientReader = clientReader;
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
    public Game startGame(GlobalContext globalContext, GameSession gameSession){
        Queue<UserContext> waitQueue = globalContext.waitingQueue;
        if(waitQueue.size() < 4) return null;

        // create new thread to process game
        Thread gameThread = new Thread(() -> {
            List<UserContext> players = new ArrayList<>();
            GameContext game = new GameContext(players);
            int gameID = game.gameID;
            gameSession.addGame(game);
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
