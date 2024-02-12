package context;

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
        players.forEach(player -> player.joinGame(game.getId()));
    }

    // update game status to not playing
    public void endGame(GameContext game){
        List<UserContext> players = game.getPlayers();
        players.forEach(player -> {
            player.endGame();
        });
    }

    public void joinWaitQueue(){
        
    }

    private UserContext getUser(String username, String socketId){
        UserContext user = userList.stream().filter(player -> player.getUsername().equals(username))
                .findFirst().orElse(null);
        return user;
    }
}