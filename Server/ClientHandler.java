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
import Server.Server_context.UserContext;

public class ClientHandler implements ServerInterface {
    // this method accepts new incoming client connections and
    // creates a new socket object or returns null if connection was unsuccessful

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GlobalContext globalContext;
    private UserCache userCache;
    private Semaphore globalContextSem;
    private Semaphore userCacheSem;
    private GameRPC gameAPI;
    private UserContext user;

    //path to database
    private final Path path =  Paths.get("Server", "utils", "user_database.txt");

    public boolean clientStatus;

    public ClientHandler(Socket clientSocket, GlobalContext globalContext, Semaphore globalContextSem, Semaphore userCacheSem) throws IOException {
        this.socket = clientSocket;
        this.clientStatus = true;
        this.globalContext = globalContext;
        this.userCache = globalContext.userCache;
        this.globalContextSem = globalContextSem;
        this.userCacheSem = userCacheSem;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ConnectRPC();
        this.gameAPI = new GameRPC(out, in);
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
                        ValidateUsernameRPC();
                        break;
                    case "New User":
                        System.out.println("Routing to new user RPC");
                        CreateUserRPC();
                        break;
                    case "Logout":
                        LogoutRPC();
                        break;
                    case "Waiting":
                        System.out.println("Join wait queue");
                        JoinWaitingQueueRPC();
                        break;
                    case "Wait Time":
                        System.out.println("Check wait queue time");
                        CheckWaitQueueRPC();
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

        // check whether user is in the userCache
        this.user = userCache.getUser(userName, socket.getRemoteSocketAddress());
        System.out.println("Validating user credentials");
        boolean isUser = false;
        if(user != null){
            isUser = user.getUsername().equals(userName) && user.getPassword().equals(password);
        }
        if(isUser && user.getStatus() != UserContext.STATUS.LOGGEDIN){
            this.user.updateStatus(UserContext.STATUS.LOGGEDIN);
            out.println(1);
            System.out.println(userName + " Login successful!");
        } else {
            out.println(0);
            System.out.println("Bad user credentials");
        }
    }

    @Override
    public UserContext CreateUserRPC() {
        try {
            System.out.println("Create user RPC");
            // get and validate username
            String username = readMessage();

            // get password
            String password = readMessage();

            // add use to user cache
            userCacheSem.acquire();
            userCache.addNewUser(new UserContext(socket.getLocalSocketAddress().toString(), username, password));
            userCacheSem.release();
            if(userCache.getLastAdded().getUsername().equals(username)){
                System.out.println("User " + username + " successfully created!");
                this.out.println(1);
            } else {
                System.out.println("User " + username + " unable to be created!");
                this.out.println(0);
            }
            // save credentials to user_database.txt
            saveUserCredentials(username, password);
        } catch (InterruptedException e) {
            System.out.println("ERROR: creating user profile RPC " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void ValidateUsernameRPC() throws IOException {
        // read in a username
        String username = this.in.readLine();
        System.out.println(username);
        // returns true if username is found in userList
        boolean validUsername = userCache.validateUsername(username);
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

        this.user.updateStatus(UserContext.STATUS.CONNECTED);
        removeFromWaitlistRPC();
    }

    public void JoinWaitingQueueRPC(){
        int waitingQueueSize = gameAPI.joinWaitQueue(globalContext, user, globalContextSem);
        System.out.println("wait queue size " + waitingQueueSize);
        // Replies to client with the wait queue size
        this.out.println(waitingQueueSize);
    }

    public void CheckWaitQueueRPC(){
        int playersNeeded = gameAPI.checkWaitTime(globalContext);
        // sending client the # of players in the wait queue
        this.out.println(playersNeeded);
    }

    @Override
    public void DisconnectRPC() {
        try{
            if(this.user != null){
                this.user.updateStatus(UserContext.STATUS.DISCONNECTED);
            }
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
        if(this.user != null){
            gameAPI.removeFromWaitQueue(globalContext, user);
        }
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
}