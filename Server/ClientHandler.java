package Server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private GameRPC gameAPI;
    private UserContext user;

    private List<UserContext> gamePlayer;

    //path to database
    private final Path path =  Paths.get("Server", "utils", "user_database.txt");

    public boolean clientStatus;

    public ClientHandler(Socket clientSocket, GlobalContext globalContext) throws IOException {
        this.socket = clientSocket;
        this.clientStatus = true;
        this.globalContext = globalContext;
        this.userCache = globalContext.userCache;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ConnectRPC();
        String clientSocketID = clientSocket.getRemoteSocketAddress().toString();
        // adding user context without name and password until authentication
        this.user = new UserContext(clientSocketID, "", "", this.in, this.out);
        this.gameAPI = new GameRPC(out, in, globalContext);
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
                    case "Start Game":
                        StartGameRPC();
                    case "Leave wait queue":
                        System.out.println("Leave wait queue");
                        removeFromWaitListRPC();
                        break;
                    case "Game End":
                        break;
                    case "Disconnect":
                        DisconnectRPC();
                        break;
                    default:
                        System.out.println("Unrecognized client message");
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
        UserContext possibleUser = userCache.getUser(userName, socket.getRemoteSocketAddress());
        if(possibleUser != null){
            this.user = possibleUser;
        }
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
            globalContext.userCacheSem.acquire();
            System.out.println("Adding client to user cache");
                // update user context information
                this.user.updateUsername(username);
                this.user.updatePassword(password);
                userCache.addNewUser(this.user);

            globalContext.userCacheSem.release();
            if(userCache.getLastAdded().getUsername().equals(username)){
                System.out.println("User " + username + " successfully created!");
                this.out.println(1);
            } else {
                System.out.println("User " + username + " unable to be created!");
                this.out.println(0);
            }
            // save credentials to user_database.txt
//            saveUserCredentials(username, password);
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
        removeFromWaitListRPC();
    }

    public void JoinWaitingQueueRPC(){
        int waitingQueueSize = gameAPI.joinWaitQueue(user);
        System.out.println("wait queue size " + waitingQueueSize);
        // Replies to client with the wait queue size
        this.out.println(waitingQueueSize);
    }

    public void CheckWaitQueueRPC() {

        try {
            int playersInQueue = gameAPI.checkWaitTime(globalContext);
            System.out.println(this.user.getUsername() + "CHECK WAIT QUEUE SIZE: " + playersInQueue);

            if(playersInQueue >= 2){ // 2 for testing purposes

                // create message queue to notify 4 clients from queue start
                globalContext.waitQueueSem.acquire();
                System.out.println("Sending message to all players in next game: ");
                    this.gamePlayer = new ArrayList<>();
                    for(int i = 0; i < 2; i++){ // 2 for testing purposes

                        // remove each client from wait queue
                        this.gamePlayer.add(globalContext.waitingQueue.remove());

                        // send game code to all clients
                        this.gamePlayer.get(i).startGameCode();
                    }
                globalContext.waitQueueSem.release();

                // STARTING GAME
                StartGameRPC();
            } else {

                // sending client the # of players in the wait queue
                this.out.println(playersInQueue);
            }
        } catch ( InterruptedException e){
            System.err.println("ERROR: check wait queue RPC - " + e.getMessage());
            e.printStackTrace();
        }
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
            this.user = null; // reset client handler user attribute to null as they are not online
            this.clientStatus = false;
            removeFromWaitListRPC();
        } catch (IOException e){
            System.out.println("Error disconnecting client");
            e.printStackTrace();
        }
    }

    private void removeFromWaitListRPC(){
        // handle if request comes from null user - it will break server if we do not
        if(this.user == null) {
            System.out.println("Client attempted to leave wait list without proper user validation...");
            this.out.println(1);
        }

        boolean userLeftSuccessfully = gameAPI.removeFromWaitQueue(user);
        if(userLeftSuccessfully){
            System.out.println("Client successfully removed from waitlist");
            this.out.println(0); // client removed from wait queue succesfully
        } else {
            System.out.println("Client unable to be removed from waitlist");
            this.out.println(1); // client unable to be removed from waitlist
        }
    }

    public void StartGameRPC() {
        System.out.println("START GAME RPC");
//        this.out.println("Start Game"); // testing client thread

//         send string to play game
        for(int i = 0; i < this.gamePlayer.size(); i++){
            this.gamePlayer.get(i).out.println("I love to code");
        }

        // TODO: get all player scores when each player finishes OR give them TIMEOUT as score if 300 seconds pass
        System.out.println("WAITING FOR EACH PLAYER TO FINISH AND SEND BACK SCORE");

        // reading in scores from each player
        // TODO: Issue with first client to join game start - an extra wait time rpc is called and causes
        // the client's game score to not be read in by the server
        System.out.println("Player " + this.gamePlayer.get(0).getUsername() + " " + this.gamePlayer.get(0).readMessage());
        System.out.println("Player " + this.gamePlayer.get(1).getUsername() + " " + this.gamePlayer.get(1).readMessage());

//        Thread player1Score = new Thread(() -> {
//            try {
//                this.gamePlayer.get(0).inLock.acquire();
//                while(this.gamePlayer.get(0).readMessage() == null){
//                    double lastScore = Double.parseDouble(this.gamePlayer.get(1).readMessage());
//                    gamePlayer.get(0).updateLastScore(lastScore);
//                }
//                this.gamePlayer.get(0).inLock.release();
//            } catch (InterruptedException e) {
//                System.err.println("ERROR: client play game thread issue with client buffered reader semaphore " + e.getMessage());
//                e.printStackTrace();
//            }
//        });
//        Thread player2Score = new Thread(() -> {
//            try {
//                this.gamePlayer.get(1).inLock.acquire();
//                    // loop until the client messages back with results
//                    while(this.gamePlayer.get(1).readMessage() == null){
//                        double lastScore = Double.parseDouble(this.gamePlayer.get(1).readMessage());
//                        gamePlayer.get(1).updateLastScore(lastScore);
//                    }
//                this.gamePlayer.get(1).inLock.release();
//            } catch (InterruptedException e) {
//                System.err.println("ERROR: client play game thread issue with client buffered reader semaphore " + e.getMessage());
//                e.printStackTrace();
//            }
//        });
//        Thread player3Score = new Thread(() -> {
//            this.gamePlayer.get(3).readMessage();
//        });
//        Thread player4Score = new Thread(() -> {
//            this.gamePlayer.get(4).readMessage();
//        });
//        player1Score.start();
//        player2Score.start();

        // check if server recieved clients lastest scores
//        this.gamePlayer.stream().forEach(player -> System.out.println(player.getUsername() + " " + player.getLastScore()));

        // will incorporate game thread once normal game works
//        try {
//            // start game thread
//            gameAPI.startGame(this.gamePlayer);
//        } catch (InterruptedException e){
//            System.err.println("ERROR: start game RPC error - " + e.getMessage());
//            e.printStackTrace();
//        }
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