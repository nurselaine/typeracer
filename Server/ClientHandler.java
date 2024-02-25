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
        UserContext userContext;
        try {
            // get and validate username
            String userName = readMessage();

            // get password
            String password = readMessage();

            // add user to global context user cache
            if(userCache.validateUsername(userName)){
                userCache.addNewUser(new UserContext(socket.getLocalSocketAddress().toString(), userName, password));
                SendMessage("User successfully created!");
            } else {
                SendMessage("User already exists!");
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return null;
    }

    public void ValidateUsernameRPC() throws IOException {
        // read in a username
        String username = this.in.readLine();

        // validate username against userCache
        boolean validUsername = userCache.userNameExists(username);
        if(validUsername){
            this.out.println(1); // ok username
        } else {
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
            System.out.println("Error receiving message from client");
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
        SendMessage("Enter user name: ");
        String userName = readMessage();

        SendMessage("Enter password: ");
        String password = readMessage();

        // check whether user is in the userCache
        UserContext user = userCache.getUser(userName, socket.getRemoteSocketAddress());
        System.out.println("Validating user credentials");
        boolean isUser = false;
        if(user != null){
            isUser = user.getUsername().equals(userName) && user.getPassword().equals(password);
        }
        if(isUser){
            out.println("Login successful!");
        } else {
            out.println("Login failed!");
        }
    }

    @Override
    public void DisconnectRPC() {
        try{
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
        throw new UnsupportedOperationException("Unimplemented method 'LogoutRPC'");
    }

    public String readMessage() {
        try {
            return in.readLine().toString();
        } catch (IOException e) {
            System.out.println("Error reading message from client");
        }
        return null;
    }
}