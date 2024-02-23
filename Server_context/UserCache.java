package Server_context;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class UserCache {

    /**
     * use dictionary type data structure that will write to file
     * for user credentials and every time server starts up
     * then read the file and add back all clients into dictionary
     * */

    private List<UserContext> userList;

    public UserCache(){
        userList = new ArrayList<>();
    }

    public List<UserContext> getAllUsers(){
        return userList;
    }

    public void addUser(UserContext user){
        userList.add(user);
    }

    /**
     * Create a new class to handle game funcationaly and user state updates when
     * joining/playing/leaving game to reduce coupling between classes
     * */
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
        if(username.getClass() != String.class || username.isEmpty()) return false;
        return userList.stream().anyMatch(player -> player.getUsername().equals(username));
    }

    public boolean validatePassword(String username, String password){
        UserContext user = userList.stream().filter(player -> player.getUsername().equals(username))
                .findFirst().orElse(null);
        if(user == null) return false;
        return user.getPassword().equals(password);
    }

    public boolean userNameExists(String username){
        return userList.contains(username);
    }
}
