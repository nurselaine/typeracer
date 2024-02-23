package Server.Server_RPC;

import Server.Server_context.GameContext;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserContext;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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
    public GameContext startGame(GlobalContext globalContext){
        Queue<UserContext> waitQueue = globalContext.waitingQueue;
        if(waitQueue.size() < 4) return null;

        List<UserContext> players = new ArrayList<>();
        GameContext game = new GameContext(players);
        int gameID = game.gameID;
        players.stream().forEach(player -> player.joinGame(gameID));
        return game; // return game to add to gameSessions in driver
    }

    // endGame
    public void endGame(GameContext game){

    }

    // game score

    // typing rpc
}
