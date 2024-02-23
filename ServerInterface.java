import java.net.Socket;
import java.io.*;

public interface ServerInterface {

    boolean ConnectRPC(Socket clientSocket);

    void CreateUserRPC(Socket clientSocket);

    void ReceiveMessage(Socket clientSocket);

    void LoginRPC(Socket clientSocket);

    void DisconnectRPC(Socket clientSocket);

    void LogoutRPC(Socket clientSocket);
}
