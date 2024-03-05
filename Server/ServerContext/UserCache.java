package Server.ServerContext;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import Server.ServerContext.User.STATUS;

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

    public boolean isUserNameTaken(String username){
        return userList.stream().anyMatch(player -> player.getUsername().equals(username));
    }

    public User getUser(String username, SocketAddress socketId){
        User user = userList.stream().filter(player -> player.getUsername().equals(username))
                .findFirst().orElse(null);
        return user;
    }

    public boolean isUserInCache(String username){
        return userList.stream().anyMatch(player -> player.getUsername().equals(username));
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

    public int authenticateUser(String username, String password){
        //find user
        User user = userList.stream().filter(player -> player.getUsername().equals(username))
                .findFirst().orElse(null);

        // user already connected
        if(user.getStatus() != STATUS.DISCONNECTED) return 1;

        // user password incorrect
        else if(!user.getPassword().equals(password)) return 2;

        // user password correct
        else if(user.getPassword().equals(password)){
            user.updateStatus(STATUS.LOGGEDIN);
            return 3;
        }
        
        //unknown error
        else return -1;
    }

    public User getLastAdded(){
        return userList.get(userList.size() - 1);
    }

    public void logoutUser(String username){
        User user = userList.stream().filter(player -> player.getUsername().equals(username))
                .findFirst().orElse(null);
        user.updateStatus(STATUS.DISCONNECTED);
        user.setClinetHandler(null);
    }

    public boolean canEnterWaitList(User user){
        return user.getStatus() == STATUS.LOGGEDIN && user.getStatus() != STATUS.WAITING;
    }

    public boolean canLeaveWaitList(User user){
        STATUS status = user.getStatus();
        return status == STATUS.WAITING;
    }

    public boolean canEnterGame(User user){
        return user.getGameID() != -1;
    }
}
