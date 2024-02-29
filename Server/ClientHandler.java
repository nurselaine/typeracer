package Server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

import Server.Server_RPC.GameRPC;
//import Server.Server_RPC.LoginRPC;
import Server.Server_context.GlobalContext;
import Server.Server_context.UserCache;
import Server.Server_context.UserContext;

public class ClientHandler {
    // this method accepts new incoming client connections and
    // creates a new socket object or returns null if connection was unsuccessful

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GlobalContext globalContext;
    //private LoginRPC loginAPI;
    private GameRPC gameAPI;
    private UserContext user;

    //path to database
    private final Path path =  Paths.get("Server", "utils", "user_database.txt");

    public boolean clientStatus;

    public ClientHandler(Socket clientSocket, GlobalContext globalContext) throws IOException {
        this.socket = clientSocket;
        ConnectRPC();
        this.clientStatus = true;
        this.globalContext = globalContext;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        //this.loginAPI = new LoginRPC(out, in);
        this.gameAPI = new GameRPC(out, in);
    }

    // @Override
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
                    case "Validate Username":
                        System.out.println("Validating username RPC");
                        break;
                    case "New User":
                        System.out.println("Routing to new user RPC");
                        CreateUserRPC();
                        break;
                    case "Logout":
                        LogoutRPC();
                        break;
                    // case "Waiting":
                    //     System.out.println("Join wait queue");
                    //     JoinWaitingQueueRPC();
                    //     break;
                    // case "Wait Time":
                    //     System.out.println("Check wait queue time");
                    //     CheckWaitQueueRPC();
                    //     break;
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

    // @Override
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


    // @Override
    public void CreateUserRPC() throws IOException {
        try {
            System.out.println("Create user RPC");

            // get and validate username
            String username = readMessage();

            //response to be returned to client
            String response;

            if(isUserNameAvailabel(username))
            {
                System.out.println("Username available for use!");
                response = "1";
            }
            else{
                System.out.println("Username already exists in system");
                // 0 for invalid username
                response = "0";
                return;
            }

            // get password
            String password = readMessage();

            // add use to user cache
            UserContext newUser = globalContext.creatUser(socket.getLocalSocketAddress().toString(), username, password);

            // verfiy user was created successfully
            if(newUser.getUsername() != null){
                System.out.println("User " + username + " successfully created!");
                saveUserCredentials(username, password);
                response = "2";
            } else {
                System.out.println("User " + username + " unable to be created!");
            }

            out.println(response);
            
        } catch (InterruptedException e) {
            System.out.println("ERROR: creating user profile RPC " + e.getMessage());
            e.printStackTrace();
        }
    }

    int ValidateUsernameRPC(String username) {
        // check if username is in the userCache
        if (!globalContext.isValidUserName(username)) {
            return 1;
        }
        return 0;
    }

    // @Override
    public void LoginRPC() throws IOException{
        System.out.println("Login RPC !");
        String userName = readMessage();
        System.out.println("client login username: " + userName);
        String password = readMessage();
        System.out.println("client login password: " + password);

        // check whether user is in the userCache
        //this.user = getUser(userName, socket.getRemoteSocketAddress());
        System.out.println("Validating user credentials");
        boolean isUser = false;
        if(user != null){
            isUser = user.getUsername().equals(userName) && user.getPassword().equals(password);
        }
        if(isUser){
            this.user.updateStatus(UserContext.STATUS.LOGGEDIN);
            out.println(0);
            System.out.println(userName + " Login successful!");
        } else {
            out.println(-1);
            System.out.println("Bad user credentials");
        }
    }


    public boolean isUserNameAvailabel(String userName) throws IOException {

        // read in a username
        System.out.println(userName);
        // returns true if username is found in userList
        if (globalContext.isValidUserName(userName)) {
            return true;

        }
        return false;

    //     if(validUsername == false){
    //         System.out.println("Username is not in system");
    //         this.out.println(1); // ok username
    //     } else {
    //         System.out.println("Username already in use");
    //         this.out.println(0); // request new username
    //     }
    }

    public void SendMessage(String message) {
        out.println(message);
    }

    // @Override
    public void LogoutRPC() {

        this.user.updateStatus(UserContext.STATUS.CONNECTED);
        removeFromWaitlistRPC();
    }

    // @Override
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

            String credential = socket.getRemoteSocketAddress().toString() + " " + username + " " + password + "\n";
            Files.write(path, Collections.singletonList(credential), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            // client credentials string
        } catch (IOException e){
            System.out.println("ERROR: unable to save user credential to database" + e.getMessage());
        }
    }
}