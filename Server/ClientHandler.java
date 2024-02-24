package Server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Server.Server_context.GlobalContext;
import Server.Server_context.UserCache;
import Server.Server_context.UserContext;

public class ClientHandler implements Runnable, ServerInterface {
    // this method accepts new incoming client connections and
    // creates a new socket object or returns null if connection was unsuccessful

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GlobalContext globalContext;
    private UserCache userCache;

    public ClientHandler(Socket clientSocket, GlobalContext globalContext) throws IOException {
        this.socket = clientSocket;
        ConnectRPC(clientSocket);
        this.globalContext = globalContext;
        this.userCache = globalContext.userCache;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            while (true) {
                String input = in.readLine();
                if (input == null) {
                    break;
                }
                out.println(input);
            }
        } catch (IOException e) {
            System.out.println("Error handling client");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Couldn't close a socket");
            }
        }
    }

    @Override
    public boolean ConnectRPC(Socket clientSocket) {
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            if (clientSocket == null) {
                out.println(0);
            }
            out.println(1);
            System.out.println("Client successfully connected to server!" + clientSocket.getInetAddress());
            return true;

        } catch (IOException e) {
            System.out.println("Unsuccessful connect to server, please disconnect " +
                    "clientside and retry" + e.getMessage());
        }
        return false;
    }

    @Override
    public UserContext CreateUserRPC() throws IOException {

        UserContext userContext;
        try {
            // get user name
            SendMessage("Enter user name:");
            String userName = readMessage();

            // get password
            SendMessage("Enter password:");
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

    @Override
    public void ReceiveMessage(Socket clientSocket) {
        try {
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                // depending on client message, route to specific RPC
                switch (clientMessage) {
                    case "Login":
                        LoginRPC(clientSocket);
                        break;
                    case "New User":
                        CreateUserRPC();
                        break;
                    case "Waiting":
                        break;
                    case "Game End":
                        break;
                    case "Disconnect":
                        break;
                    default:
                        System.out.println("Unrecognized client message");
                }
            }
        } catch (IOException e) {
            System.out.println("Error receiving message from client");
        }
    }

    @Override
    public void SendMessage(String message) throws IOException{
        out.println(message);
    }

    @Override
    public void LoginRPC(Socket clientSocket) throws IOException{
        SendMessage("Enter user name: ");
        String userName = readMessage();

        SendMessage("Enter password: ");
        String password = readMessage();

        // check whether user is in the userCache
        UserContext user = userCache.getUser(userName, clientSocket.getRemoteSocketAddress());
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
    public void DisconnectRPC(Socket clientSocket) {
        try{
            clientSocket.close();
        } catch (IOException e){
            System.out.println("Error disconnecting client");
            e.printStackTrace();
        }
    }

    @Override
    public void LogoutRPC(Socket clientSocket) {
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