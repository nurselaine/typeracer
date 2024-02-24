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
import java.util.concurrent.TimeUnit;

public class GamePRC {

    private PrintWriter clientWriter;
    private BufferedReader clientReader;

    public GamePRC (PrintWriter clientWriter, BufferedReader clientReader){
        this.clientWriter = clientWriter;
        this.clientReader = clientReader;
    }
    // join waiting queue
    public void joinWaitQueue(GlobalContext globalContext, UserContext user){
        Queue<UserContext> waitQueue = globalContext.waitingQueue;
        user.joinWaitQueue(); // update user status
        if(waitQueue.size() % 4 != 0){
            int remainPlayers = waitQueue.size() % 4;
            if(remainPlayers == 1){
                clientWriter.println("Waiting for " + remainPlayers + " player to join");
            } else {
                clientWriter.println("Waiting for " + remainPlayers + " players to join");
            }
        } else {
            clientWriter.println("Game will begin in a few seconds...");
        }
    }

    public void checkWaitTime(){

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

    // game score

    // typing rpc
}
