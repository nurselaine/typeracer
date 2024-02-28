package Server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
import packages.MyUser;

public class ClientHandler implements ServerInterface {
    // this method accepts new incoming client connections and
    // creates a new socket object or returns null if connection was unsuccessful

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private ByteArrayInputStream byteArrayInput;
    private ObjectInputStream serializedInput;
    private GlobalContext globalContext;
    private UserCache userCache;
    private Semaphore globalContextSem;
    private Semaphore userCacheSem;
    //private LoginRPC loginAPI;
    private GameRPC gameAPI;
    private UserContext user;

    //path to database
    private final Path path =  Paths.get("Server", "utils", "user_database.txt");

    public boolean clientStatus;

    private ObjectInputStream serializedinput;

    public ClientHandler(Socket clientSocket, GlobalContext globalContext, Semaphore globalContextSem, Semaphore userCacheSem) throws IOException {
        this.socket = clientSocket;
        this.socket.setSoTimeout(10000);
        ConnectRPC();
        this.clientStatus = true;
        this.globalContext = globalContext;
        this.userCache = globalContext.userCache;
        this.globalContextSem = globalContextSem;
        this.userCacheSem = userCacheSem;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.gameAPI = new GameRPC(out, in);
        this.serializedInput = new ObjectInputStream(clientSocket.getInputStream());
    }

    @Override
    public void ReceiveMessage() {
        try {
            String clientMessage;
            while ((clientMessage = in.readLine()) != null || socket.isConnected()) {

                System.out.println(clientMessage);
                // depending on client message, route to specific RPC
                switch (clientMessage) {
                    case "Login":
//                        System.out.println("Client message: Login command");
                        LoginRPC();
                        break;
                    case "Valid Username":
//                        System.out.println("Validating username RPC");
                        ValidateUsernameRPC();
                        break;
                    case "New User":
//                        System.out.println("Routing to new user RPC");
                        CreateUserRPC();
                        break;
                    case "Logout":
                        System.out.println("LOgout RPC??");
                        LogoutRPC();
                        break;
                    case "Waiting":
//                        System.out.println("Join wait queue");
                        JoinWaitingQueueRPC();
                        break;
                    case "Wait Time":
//                        System.out.println("Check wait queue time");
                        CheckWaitQueueRPC();
                        break;
                    case "Game End":
                        break;
                    case "Disconnect":
                        DisconnectRPC();
                        break;
                    default:
                        System.out.println("Unrecognized client message");
                        throw new IOException("Invalid input");
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
        try {
            System.out.println();
            this.out = new PrintWriter(socket.getOutputStream(), true);
            if (socket == null) {
                out.println(0);
            }
            out.println(1);
            System.out.println("Client successfully connected to server!" + socket.getInetAddress());
            return true;

        } catch (IOException e) {
            System.out.println("Unsuccessful connect to server, please disconnect " +
                    "clientside and retry" + e.getMessage());
        }
        return false;
    }

    @Override
    public void LoginRPC() throws SocketException {
        try {
            System.out.println("Login RPC !");
            System.out.println("deserializing object");
            Object serializedObj = this.serializedInput.readObject();
            System.out.println(serializedObj);
            MyUser currUser = (MyUser) serializedObj;
            System.out.println("username" + currUser.username);

            // check whether user is in the userCache
            userCacheSem.acquire();
            this.user = userCache.getUser(currUser.username, socket.getRemoteSocketAddress());
            userCacheSem.release();
            System.out.println("Validating user credentials");
            boolean isUser = false;
            if(this.user != null){
                // validate username and password received from client
                isUser = user.getUsername().equals(currUser.username)
                        && user.getPassword().equals(currUser.password);
            }

            // checks whether user is already loggined in
            System.out.println("current status" + this.user.getStatus());
            if(isUser && this.user.getStatus() != UserContext.STATUS.LOGGEDIN){
                this.user.updateStatus(UserContext.STATUS.LOGGEDIN);
                out.println(1);
                System.out.println(currUser.username + " Login successful!");
            } else {
                out.println(0);
                System.out.println("Bad user credentials");
            }
        } catch(SocketTimeoutException e) {
            System.err.println("Timeout while waiting for client login credentials");
        } catch (IOException | InterruptedException | ClassNotFoundException | RuntimeException e) {
            System.err.println("ERROR: loginRPC " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.socket.setSoTimeout(10000); // reset timeout
        }
    }

    @Override
    public UserContext CreateUserRPC() {
        try {
            System.out.println("Create user RPC");
            // get and validate username

            MyUser newUser = (MyUser) this.serializedInput.readObject();
            System.out.println(newUser.username + " " + newUser.password);
            String username = newUser.username;
            String password = newUser.password;

            this.user = new UserContext(socket.getLocalSocketAddress().toString(), username, password);

            // add use to user cache
            userCacheSem.acquire();
            userCache.addNewUser(this.user);
            userCacheSem.release();
            if(userCache.getLastAdded().getUsername().equals(username)){
                System.out.println("User " + username + " successfully created!");
                this.out.println(1);
            } else {
                System.out.println("User " + username + " unable to be created!");
                this.out.println(0);
            }
            saveUserCredentials(username, password);
        } catch ( InterruptedException | IOException | ClassNotFoundException e) {
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
        System.out.println("Logout RPC");
        removeFromWaitlistRPC(); // will update user's status to loggedin
        this.user.updateStatus(UserContext.STATUS.CONNECTED); // re-updates user status to connected
        System.out.println("Post logout status " + this.user.getStatus());
    }

    public void JoinWaitingQueueRPC(){
        int waitingQueueSize = gameAPI.joinWaitQueue(globalContext, user, globalContextSem);
        this.user.joinWaitQueue(); // update status
        System.out.println("wait queue size " + waitingQueueSize);
        this.out.println(waitingQueueSize);
    }

    public void CheckWaitQueueRPC(){
        int playersNeeded = gameAPI.checkWaitTime(globalContext);
        this.out.println(playersNeeded);
    }

    @Override
    public void SendMessage(String message) throws IOException{
        out.println(message);
    }

    @Override
    public void DisconnectRPC() {
        try{
            this.user.updateStatus(UserContext.STATUS.DISCONNECTED);
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
        this.user.updateStatus(UserContext.STATUS.LOGGEDIN); // not waiting anymore
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