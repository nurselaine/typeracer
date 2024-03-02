package Server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.concurrent.Semaphore;

import Server.Server_RPC.GameRPC;
//import Server.Server_RPC.LoginRPC;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserCache;
import Server.Server_context.User;

public class ClientHandler implements ServerInterface {
    // this method accepts new incoming client connections and
    // creates a new socket object or returns null if connection was unsuccessful

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GlobalContext globalContext;
    private User user;
    //path to database
    private final Path path =  Paths.get("Server", "utils", "user_database.txt");

    public boolean clientStatus;

    public ClientHandler(Socket clientSocket, GlobalContext globalContext)  throws IOException {
        this.socket = clientSocket;
        this.clientStatus = true;
        this.globalContext = globalContext;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ConnectRPC();
    }

    @Override
    public void ReceiveMessage() {
        try {
            String clientMessage;
            while ((clientMessage = in.readLine()) != null || socket.isConnected()) {
                // depending on client message, route to specific RPC
                System.out.println("client message: " + clientMessage);
                switch (clientMessage) {
                    case "Login":
                        System.out.println("Client message: Login command");
                        LoginRPC();
                        break;
                    case "Valid Username":
                        System.out.println("Validating username RPC");
                        try{
                            ValidateUsernameRPC();
                        } catch (Exception e){
                            System.out.println("Error validating username RPC");
                        }
                        break;
                    case "New User":
                        System.out.println("Routing to new user RPC");
                        CreateUserRPC();
                        break;

                    case "Join Wait Queue":
                        joinWaitQueueRPC();
                        break;

                    case "Logout":
                        LogoutRPC();
                        break;
                    case "Game End":
                        break;
                    case "Disconnect":
                        DisconnectRPC();
                        break;
                    default:
                        System.out.println("Unrecognized client message");
                        // TODO: code -1000 to let clientside know that input was bad
                }
            }
        } catch (IOException e) {
            System.out.println("Client socket lost connection.");
            this.clientStatus = false;
        } finally {
            DisconnectRPC();
        }
    }

    public void joinWaitQueueRPC() {
        try {
            System.out.println("Join waiting queue RPC");
            globalContext.joinWaitQueue(user.getUsername());
        } catch (Exception e) {
            System.out.println("ERROR: joining waiting queue RPC " + e.getMessage());
        }
    }

    @Override
    public boolean ConnectRPC() {

        if (socket == null) {
            this.clientStatus = false;
            return false;
        }
        out.println(1);
        System.out.println("Client successfully connected to server!" + socket.getInetAddress());
        return true;
    }

    @Override
    public void LoginRPC() throws IOException{
        System.out.println("Login RPC !");
        String userName = readMessage(); //
        System.out.println("client login username: " + userName);
        String password = readMessage();
        System.out.println("client login password: " + password);

        boolean isUser = false;
        
        try {
            isUser = globalContext.authenticateUser(userName, password);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user = globalContext.getUser(userName);

        if(isUser && user.getStatus() != User.STATUS.LOGGEDIN){
            user.updateStatus(User.STATUS.LOGGEDIN);
            out.println(1);
            System.out.println(userName + " Login successful!");
        } else {
            //reset user data to invalid 
            user.setSentinelValue();
            out.println(0);
            System.out.println("Bad user credentials");
        }
    }

    @Override
    public void CreateUserRPC() {
        boolean userCreated;
            System.out.println("Create user RPC");
            // get and validate username
            String username = readMessage();

            // get password
            String password = readMessage();

            this.user = new User(socket.getRemoteSocketAddress().toString(), username, password);
            // add use to user cache
            try{
            userCreated = globalContext.addUser(user);
            } catch (Exception e){
                System.out.println("Error adding user to user cache");
                userCreated = false;
            }

            if(userCreated){
                System.out.println("User " + username + " successfully created!");
                this.out.println(1);
            } else {
                System.out.println("User " + username + " unable to be created!");
                this.out.println(0);
            }
            // save credentials to user_database.txt
            saveUserCredentials(username, password);
    }

    public void ValidateUsernameRPC() throws IOException, InterruptedException {
        // read in a username
        String username = this.in.readLine();
        System.out.println(username);
        // returns true if username is found in userList
        boolean validUsername = globalContext.validateUsername(username);
        if(validUsername == false){
            System.out.println("Username is not in system");
            this.out.println(1); // ok username
        } else {
            System.out.println("Username already in use");
            this.out.println(0); // request new username
        }
    }

    @Override
    public void LogoutRPC() {

        this.user.updateStatus(User.STATUS.CONNECTED);
        removeFromWaitlistRPC();
    }


    public void CheckWaitQueueRPC(){
        int playersNeeded = gameAPI.checkWaitTime(globalContext);
        // sending client the # of players in the wait queue
        this.out.println(playersNeeded);
    }

    @Override
    public void DisconnectRPC() {
        try{
            this.user.updateStatus(User.STATUS.DISCONNECTED);
            socket.close();
            this.in.close();
            this.out.close();
            this.clientStatus = false;
            removeFromWaitlistRPC();
        } catch (IOException e){
            System.out.println("Error disconnecting client");
            e.printStackTrace();
        }
    }

    private void removeFromWaitlistRPC(){
        // remove client from waitlist
        gameAPI.removeFromWaitQueue(globalContext, user);
        // TODO: send client feedback ?? maybe not
    }

    public String readMessage() {
        try {
            return in.readLine().toString();
        } catch (IOException e) {
            System.out.println("Error reading message from client");
        }
        return null;
    }

    private void saveUserCredentials(String username, String password){
        try {

            String credential = socket.getRemoteSocketAddress().toString() + " " + username + " " + password;
            Files.write(path, Collections.singletonList(credential), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            // client credentials string
        } catch (IOException e){
            System.out.println("ERROR: unable to save user credential to database" + e.getMessage());
        }
    }

    public void sendMessage(String message){
        out.println(message);
    }
}