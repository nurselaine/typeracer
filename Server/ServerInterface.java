package Server;

import java.net.Socket;

import Server.Server_context.UserContext;

import java.io.*;


public interface ServerInterface {

    boolean ConnectRPC();

    void CreateUserRPC() throws IOException;

    void ReceiveMessage();

    void SendMessage(String message) throws IOException;

    void LoginRPC() throws IOException;

    void DisconnectRPC();

    void LogoutRPC();
}