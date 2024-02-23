import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

    public class ClientHandler implements Runnable, ServerInterface{
    // this method accepts new incoming client connections and
    // creates a new socket object or returns null if connection was unsuccessful

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    
    public ClientHandler(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        ConnectRPC(clientSocket);

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
    public void CreateUserRPC(Socket clientSocket) {
        
    }

    @Override
    public void ReceiveMessage(Socket clientSocket) {
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                // depending on client message, route to specific RPC
                switch (clientMessage) {
                    case "Login":
                        LoginRPC(clientSocket);
                        break;
                    case "New User":
                        CreateUserRPC(clientSocket);
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
    public void LoginRPC(Socket clientSocket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'LoginRPC'");
    }

    @Override
    public void DisconnectRPC(Socket clientSocket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'DisconnectRPC'");
    }   

    @Override
    public void LogoutRPC(Socket clientSocket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'LogoutRPC'");
    }



}