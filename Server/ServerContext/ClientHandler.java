package Server.ServerContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

import Server.ServerContext.User.STATUS;

public class ClientHandler {
    // this method accepts new incoming client connections and
    // creates a new socket object or returns null if connection was unsuccessful

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GlobalContext globalContext;
    private String username;
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

    public void CommandHandler() throws Exception{
        String command;
        while ((command = in.readLine()) != null || socket.isConnected()){
            switch(command){
                case "Register":
                    globalContext.registerUser(this);
                    break;

                case "Login":
                    globalContext.login(this);
                    //register
                    break;

                case "Logout":
                    globalContext.logout(this);
                    break;
                    
                case "Quit":
                    globalContext.quit(this);
                    socket.close();
                    break;

                case "EnterWaitList":
                    globalContext.enterWaitList(this);
                    break;

                case "LeaveWaitList":
                    globalContext.leaveWaitList(this);
                    break;

                    case "EnterGame":
                    globalContext.enterGame(this);
                    break;

                    case "PlayGame":
                    globalContext.playGame(this);
                    globalContext.setUserToLoginState(this);
                    break;

                    case "CheckWaitTime":
                    globalContext.checkWaitTime(this);
                    break;

                default:
                    System.out.println("Invalid command");
                    break;
            }
        }
    }
    public boolean ConnectRPC() {

        if (socket == null) {
            this.clientStatus = false;
            return false;
        }
        out.println(1);
        System.out.println("Client successfully connected to server!" + socket.getInetAddress());
        return true;
    }

    public void sendMessage(String message) {
        out.println(message);
    }
    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public String getSocketAddress(){
        return socket.getRemoteSocketAddress().toString();
    }

    public String getUsername(){
        return username;
    }

    public void setUser(User user){
        this.username = user.getUsername();
    }

    public void closeClientHandler() throws IOException{
        this.in.close();
        this.out.close();

        this.socket.close();

    }
}