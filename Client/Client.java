package Client;

import Client.RPC.GameRPC;
import Client.RPC.UserRPC;
import Client.ui.Menu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
            boolean inGame = false;

            // client creates new socket using host and port number that server is running
            // Once server accept the connection with client will socket object be created
            Socket soc = new Socket("localhost", 3001);
            // create client resources
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            PrintWriter serverWriter = new PrintWriter(soc.getOutputStream(), true);
            UserRPC userAPI = new UserRPC(input, serverWriter, serverReader);
            GameRPC gameAPI = new GameRPC(serverWriter, serverReader, input);

            String connected = serverReader.readLine(); // server is sending 1/0 from connectRPC when clienthanlder istnace is created on connection
            System.out.println("Connected to server: " + connected);

            while(soc.isConnected()){

                while(isLoggedIn == false){


                    System.out.println("Print non-validated menu: ");
                    // print menu options for login options
                    menu.nonValidatedUserMenu();
                    String menuOption = menu.getMenuInput(false);

                    Thread gameStartListener = new Thread(() -> {
                       if(menuOption.equals("Start Game")){
                           System.out.println("Client game startin!");
                       }
                    });
                    gameStartListener.start();

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
                        case 1: // enter wait list & start game when enough players join wait list
                            gameAPI.joinWaitingQueue();
                            if(gameAPI.checkWaitingTime()){
                                // add start game RPC
                                gameAPI.startGame();
                            } else {
                                System.out.println("> Error starting game... Please rejoin wait list.");
                            }

                            break;
                        case 2: // logout
                            userAPI.logout();
                            isLoggedIn = false;
                            break;
                        case 3: // quit
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
}