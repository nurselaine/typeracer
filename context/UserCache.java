package context;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class UserCache {

    private List<UserContext> userList;

    public UserCache(){
        userList = new ArrayList<>();
    }

    public List<UserContext> getAllUsers(){
        return userList;
    }

    public void addNewUser(UserContext user){
        userList.add(user);
    }

    // update game status to playing
    public void joinGame(GameContext game){
        List<UserContext> players = game.getPlayers();
        players.forEach(player -> player.joinGame(game.gameID));
    }

    // update game status to not playing
    public void endGame(GameContext game){
        List<UserContext> players = game.getPlayers();
        players.forEach(player -> {
            player.endGame();
        });
    }

    public void joinWaitQueue(UserContext user){
        user.joinWaitQueue();
    }

    public UserContext getUser(String username, SocketAddress socketId){
        UserContext user = userList.stream().filter(player -> player.getUsername().equals(username))
                .findFirst().orElse(null);
        return user;
    }

    public boolean validateUsername(String username){
        if(username.getClass() != String.class) return false;
        return userList.stream().anyMatch(player -> player.getUsername().equals(username));
    }
}
