package Server;

import java.io.*;
import java.net.Socket;

import Server.Server_RPC.LoginRPC;
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
    private LoginRPC loginAPI;
    private UserContext user;

    public boolean clientStatus;

    public ClientHandler(Socket clientSocket, GlobalContext globalContext) throws IOException {
        this.socket = clientSocket;
        ConnectRPC();
        this.clientStatus = true;
        this.globalContext = globalContext;
        this.userCache = globalContext.userCache;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.loginAPI = new LoginRPC(out, in);
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
    public UserContext CreateUserRPC() throws IOException {
        System.out.println("Create user RPC");
            // get and validate username
            String username = readMessage();

            // get password
            String password = readMessage();

            // add use to user cache
            userCache.addNewUser(new UserContext(socket.getLocalSocketAddress().toString(), username, password));
            if(userCache.getLastAdded().getUsername().equals(username)){
                System.out.println("User " + username + " successfully created!");
                this.out.println(1);
            } else {
                System.out.println("User " + username + " unable to be created!");
                this.out.println(0);
            }
            saveUserCredentials(username, password);
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
    public void SendMessage(String message) throws IOException{
        out.println(message);
    }

    @Override
    public void LoginRPC() throws IOException{
        System.out.println("Login RPC !");
        String userName = readMessage();
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
        if(isUser){
            this.user.updateStatus(UserContext.STATUS.LOGGEDIN);
            out.println(1);
            System.out.println(userName + " Login successful!");
        } else {
            out.println(0);
            System.out.println("Bad user credentials");
        }
    }

    @Override
    public void DisconnectRPC() {
        try{
            this.user.updateStatus(UserContext.STATUS.DISCONNECTED);
            socket.close();
            this.in.close();
            this.out.close();
            this.clientStatus = false;
        } catch (IOException e){
            System.out.println("Error disconnecting client");
            e.printStackTrace();
        }
    }

    @Override
    public void LogoutRPC() {
        // TODO Auto-generated method stub
        // update user status
        this.user.updateStatus(UserContext.STATUS.CONNECTED);
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
            // write user credentials to file to use for future server restarts
            FileWriter fileWriter =
                    new FileWriter("C:\\Users\\Elain\\Projects\\typeracer\\Server\\utils\\user_database.txt", true);

            // client credentials string
            String credential = socket.getRemoteSocketAddress().toString() + " " + username + " " + password + "\n";
            fileWriter.write(credential);

            fileWriter.close();
        } catch (IOException e){
            System.out.println("ERROR: unable to save user credential to database" + e.getMessage());
        }
    }
}