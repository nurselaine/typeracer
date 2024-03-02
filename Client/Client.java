/*
package Client;

import Client.RPC.GameRPC;
import Client.RPC.UserRPC;
import Client.ui.Menu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.util.Scanner;

// Hello world!
public class Client {
    public static void main(String[] args) {

        // TODO: Create a thread to manage incoming server messages
        // TODO: Create a method to handle server messages ?? maybe

        try {

            System.out.println("Client Socket");
            Scanner input = new Scanner(System.in); // for reading client input
            // instantiate menu library
            Menu menu = new Menu(input);
            boolean isLoggedIn = false;

            // client creates new socket using host and port number that server is running
            // Once server accept the connection with client will socket object be created
            Socket soc = new Socket("localhost", 3001);
            // create client resources
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            PrintWriter serverWriter = new PrintWriter(soc.getOutputStream(), true);
            UserRPC userAPI = new UserRPC(input, serverWriter, serverReader);
            GameRPC gameAPI = new GameRPC(serverWriter, serverReader);

            String connected = serverReader.readLine(); // server is sending 1/0 from connectRPC when clienthanlder istnace is created on connection
            System.out.println("Connected to server: " + connected);

            while(soc.isConnected()){

                while(isLoggedIn == false){
                    System.out.println("Print non-validated menu: ");
                    // print menu options for login options
                    menu.nonValidatedUserMenu();
                    String menuOption = menu.getMenuInput(false);

                    // switch
                    switch(Integer.parseInt(menuOption)){
                        case 1: // New user
                            userAPI.newUser();
                            break;
                        case 2: // Login
                            isLoggedIn = userAPI.login();
                            break;
                        case 3:
                            serverWriter.println("Disconnect");
                            soc.close();
                            System.out.println("Program ending. See you next time!");
                            return; // end program
                        default:
                            // TODO: handle incorrect client input
                            System.out.println("> Invalid menu option. Please try again.");
                    }

                }

                while(isLoggedIn){
                    menu.validatedUserMenu();
                    String menuOption = menu.getMenuInput(true);

                    switch(Integer.parseInt(menuOption)){
                        case 1: // enter wait list
                            gameAPI.joinWaitingQueue();
                            break;
                        case 2: // check wait list time
                            gameAPI.checkWaitingTime();
                            break;
                        case 3: // leave wait list

                            break;
                        case 4: // logout
                            userAPI.logout();
                            isLoggedIn = false;
                            break;
                        case 5: // quit
                            serverWriter.println("Disconnect");
                            soc.close();
                            System.out.println("Program ending. See you next time!");
                            return; // end program
                        default:
                            // TODO: handle incorrect client input
                            System.out.println("> Invalid menu option. Please try again.");
                    }

                    // check if game is ready to start
                    // server will notify client
                }
                System.out.println("exited while loop for login");
            }

            //cloose socket and cleanup resources
            soc.close();
            serverWriter.close();
            serverReader.close();

        } catch (Exception e){
            // TODO: handle client disconnecting
            e.printStackTrace();
        }
            // TODO: handle server socket closing and client still connected - shut client process down
        System.out.println("Socket disconnected & client will now shutdown");
    }
}*/

package Client;

import Client.RPC.GameRPC;
import Client.RPC.UserRPC;
import Client.ui.Menu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static boolean isLoggedIn = false;
    private static boolean running = true; // Control the main loop

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Socket soc = null;
        BufferedReader serverReader = null;
        PrintWriter serverWriter = null;

        try {
            System.out.println("Client Socket");
            Menu menu = new Menu(input);

            soc = new Socket("localhost", 3001);
            serverReader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            serverWriter = new PrintWriter(soc.getOutputStream(), true);
            UserRPC userAPI = new UserRPC(input, serverWriter, serverReader);
            GameRPC gameAPI = new GameRPC(serverWriter, serverReader);

            // Listen for connection confirmation from the server
            String connected = serverReader.readLine();
            System.out.println("Connected to server: " + connected);

            // Thread for handling server messages
            BufferedReader finalServerReader = serverReader;
            Socket finalSoc = soc;
            Thread serverMessageHandler = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = finalServerReader.readLine()) != null) {
                        System.out.println("Message from server: " + serverMessage);
                        // Implement specific actions based on server messages if needed
                    }
                } catch (Exception e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                } finally {
                    System.out.println("Server connection lost. Exiting program.");
                    running = false; // Exit the loop
                    try {
                        if (finalSoc != null) finalSoc.close();
                    } catch (Exception ex) {
                        System.out.println("Error closing socket: " + ex.getMessage());
                    }
                    System.exit(0); // Exit the program
                }
            });

            serverMessageHandler.start();

            // Main loop for client interactions
            while (running && soc.isConnected()) {
                if (!isLoggedIn) {
                    System.out.println("Print non-validated menu: ");
                    menu.nonValidatedUserMenu();
                    String menuOption = menu.getMenuInput(false);

                    switch (Integer.parseInt(menuOption)) {
                        case 1: // New user
                            userAPI.newUser();
                            break;
                        case 2: // Login
                            isLoggedIn = userAPI.login();
                            break;
                        case 3: // Disconnect
                            serverWriter.println("Disconnect");
                            running = false; // Stop the loop
                            break;
                        default:
                            System.out.println("> Invalid menu option. Please try again.");
                    }
                } else {
                    menu.validatedUserMenu();
                    String menuOption = menu.getMenuInput(true);

                    switch (Integer.parseInt(menuOption)) {
                        case 1: // Enter wait list
                            gameAPI.joinWaitingQueue();
                            break;
                        case 2: // Check wait list time
                            gameAPI.checkWaitingTime();
                            break;
                        case 3: // Leave wait list
                            // Implement leave wait list logic
                            break;
                        case 4: // Logout
                            userAPI.logout();
                            isLoggedIn = false;
                            break;
                        case 5: // Quit
                            serverWriter.println("Disconnect");
                            running = false; // Stop the loop
                            break;
                        default:
                            System.out.println("> Invalid menu option. Please try again.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cleanup resources
            try {
                if (soc != null && !soc.isClosed()) soc.close();
                if (serverWriter != null) serverWriter.close();
                if (serverReader != null) serverReader.close();
                System.out.println("Cleanup completed. Client is shutting down.");
            } catch (Exception e) {
                System.out.println("Error during cleanup: " + e.getMessage());
            }
        }
    }
}
