package Server.Server_context;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class UserCache {

    /**
     * use dictionary type data structure that will write to file
     * for user credentials and every time server starts up
     * then read the file and add back all clients into dictionary
     * */
    
    private List<User> userList;

    public UserCache(){
        userList = new ArrayList<>();
    }

    public List<User> getAllUsers(){
        return userList;
    }

    public void addNewUser(User user){
        userList.add(user);
    }

    public User getUser(String username, SocketAddress socketId){
        User user = userList.stream().filter(player -> player.getUsername().equals(username))
                .findFirst().orElse(null);
        return user;
    }

    public User getUser(String username){
        User user = userList.stream().filter(player -> player.getUsername().equals(username))
                .findFirst().orElse(null);
        return user;
    }

    public boolean validateUsername(String username){
        if(username.isEmpty()) return false;
        return userList.stream().anyMatch(player -> player.getUsername().equals(username));
    }

    public boolean validatePassword(String username, String password){
        User user = userList.stream().filter(player -> player.getUsername().equals(username))
                .findFirst().orElse(null);
        if(user == null) return false;
        return user.getPassword().equals(password);
    }

    public boolean authenticateUser(String username, String password){
        User user = userList.stream().filter(player -> player.getUsername().equals(username))
                .findFirst().orElse(null);
        if(user == null) return false;
        return user.getPassword().equals(password);
    }

    public User getLastAdded(){
        return userList.get(userList.size() - 1);
    }
}
