package Server.Game;
import Server.ServerContext.ClientHandler;
import Server.ServerContext.User;

public class Game {

    User[] players;

     
    public Game() {

    }

    public void setPlayers(User[] players) {
        this.players = players;
        for(int i = 0; i < 3; i++){
            User user = players[i];
            ClientHandler clientHandler =  user.getClientHandler();

            clientHandler.sendMessage(null);
        }
    }

}
