package Server.Server_RPC;

import Server.Server_context.UserCache;
import Server.Server_context.UserContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginRPC {

    private PrintWriter clientWriter;
    private BufferedReader clientReader;

    public LoginRPC(PrintWriter clientWriter, BufferedReader clientReader){
        this.clientReader = clientReader;
        this.clientWriter = clientWriter;
    }

    /**
     * this is for after this course ends and we continue to build this project:
     * Feature: auth
     *
     * Use cookies to store user credentials
     * can set policy to delete cookie within X time
     * if user
     * */

    /**
     * Creates a user profile for new users joining the server for the first time
     *
     * Create PrintWriter objects to share between threads
     * */
    public void newUserRPC(Socket clientSocket, UserCache userCache){
//        System.out.println("New User RPC");
        // validate username is unique
        String[] userCredentials;
        String username = "", password = "";
        int validUsername = -1;
        while(validUsername != 1){
            clientWriter.println("Provide username + password");

            // helper function to prompt client for user and password
            userCredentials = getUserCredentials(clientSocket);
            username = userCredentials[0];
            password = userCredentials[1];

            // username validation
            validUsername = validateUsername(clientSocket, userCache, username);
        }

        // create new user context
        if(!username.isEmpty() && !password.isEmpty()){
            UserContext newUser = new UserContext(clientSocket.getRemoteSocketAddress(), username, password);
            userCache.addNewUser(newUser);
            clientWriter.println("Successfully created new user profile" +
                    username+ " " + password +  "  - please Login now");
        }
    }

    /**
     * Checks user credentials of client with list of all clients on server and
     * validates whether username and password of client matches with one inside
     * user cache
     * */
    public void Login(Socket clientSocket, UserCache userCache){

//        System.out.println("Attempting Login");

        // client sends username and passwrod
        clientWriter.println("Provider username + password");
        String[] userCredentials = getUserCredentials(clientSocket);
        String username = userCredentials[0];
        String password = userCredentials[1];
        /**
         * ?? Refactor
         * */
        // check whether user is in the userCache
        UserContext user = userCache.getUser(username, clientSocket.getRemoteSocketAddress());
        boolean isUser = false;
        if(user != null){
            isUser = user.getUsername().equals(username) && user.getPassword().equals(password);
        }

        String clientMessage = isUser ? "Successfully logged in as " + username : "Login Failed - please attempt to login again";
        clientWriter.println(clientMessage);

    }

    /**
     * ValidateUsernameRPC will determine if username provided is unique and
     * not included in current list of users on the server
     * */
    private int validateUsername(Socket clientSocket, UserCache userCache, String username){

//        System.out.println("Validating Username");

        // validate username
        if(!userCache.validateUsername(username)){
            // if username is not found in user cache then send success integer to client
            clientWriter.println(1);
            return 1;
        } else {
            // if username is found in user cache then send failure integer to client
            clientWriter.println(0);
            return 0;
        }

    }

    /**
     * Prompts client for username and password
     * */
    private String[] getUserCredentials(Socket clientSocket){
        try {
            // get username and password
            clientWriter.println("Enter username: ");
            String username = clientReader.readLine();
            clientWriter.println("Enter password: ");
            String password = clientReader.readLine();
            return new String[]{username, password};
        } catch (IOException e) {
            System.out.println("GET_USER_CREDENTIALS Error: unable to read input stream of username & password");
        }
        return null;
    }
}
