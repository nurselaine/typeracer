import java.net.Socket;

import Server_context.UserContext;

import java.io.*;


public interface ServerInterface {

    UserContext CreateUserRPC() throws IOException;

    void ReceiveMessage(Socket clientSocket);

    void SendMessage(String message) throws IOException;

    void LoginRPC(Socket clientSocket) throws IOException;

    void DisconnectRPC(Socket clientSocket);

    void LogoutRPC(Socket clientSocket);
}
