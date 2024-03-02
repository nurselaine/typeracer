package Server;

import java.net.Socket;

import Server.Server_context.UserContext;

import java.io.*;


public interface ServerInterface {

    boolean ConnectRPC();

    UserContext CreateUserRPC() throws IOException;

    void ReceiveMessage();

    void removeFromWaitListRPC();

    void LoginRPC() throws IOException;

    void DisconnectRPC();

    void LogoutRPC();
}