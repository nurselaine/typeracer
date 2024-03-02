package Server;

import java.net.Socket;

import Server.Server_context.User;

import java.io.*;


public interface ServerInterface {

    boolean ConnectRPC();

    void CreateUserRPC() throws IOException;

    void ReceiveMessage();

    void LoginRPC() throws IOException;

    void DisconnectRPC();

    void LogoutRPC();
}