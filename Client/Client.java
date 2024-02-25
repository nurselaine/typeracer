package Client;

import Client.RPC.UserRPC;
import Client.ui.Menu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.util.Scanner;


public class Client {
    public static void main(String[] args) {

        try {

            System.out.println("Client Socket");
            Scanner input = new Scanner(System.in); // for reading client input
            // instantiate menu library
            Menu menu = new Menu(input);
            boolean isLoggedIn = false;

            // client creates new socket using host and port number that server is running
            // Once server accept the connection with client will socket object be created
            Socket soc = new Socket("localhost", 3001);

            while(soc.isConnected()){

                // create client resources
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                PrintWriter serverWriter = new PrintWriter(soc.getOutputStream(), true);
                UserRPC userAPI = new UserRPC(input, serverWriter, serverReader);

                while(!isLoggedIn){
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
                            System.out.println("> Invalid menu option. Please try again.");
                    }

                }

                while(isLoggedIn){
                    menu.validatedUserMenu();
                    String menuOption = menu.getMenuInput(true);

                    switch(Integer.parseInt(menuOption)){
                        case 1: // enter wait list
                            userAPI.newUser();
                            break;
                        case 2: // check wait list time
                            isLoggedIn = userAPI.login();
                            break;
                        case 3: // leave wait list
                            break;
                        case 4: // logout
                            break;
                        case 5: // quit
                            serverWriter.println("Disconnect");
                            soc.close();
                            System.out.println("Program ending. See you next time!");
                            return; // end program
                        default:
                            System.out.println("> Invalid menu option. Please try again.");
                    }
                }

                // System.in is an inputstream obj that takes a bytestream of data
                // Using inputstream reader, it takes a bytestream and returns a character stream
                // Lastly bufferedreader will be able to read the entire string from the input
                String userStr = getInput();
                // send message to server
                sendInput(userStr, soc);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("Socket disconnected & client will now shutdown");
    }

    public static String getInput(){
        try {
            // System.in is an inputstream obj that takes a bytestream of data
            // Using inputstream reader, it takes a bytestream and returns a character stream
            // Lastly bufferedreader will be able to read the entire string from the input
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter Message: ");
            String userStr;
            if((userStr = userInput.readLine())  != null){ // this method will capture user input
                return userStr;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException occurred while getting input from user");
        }
        return null;
    }

    public static void sendInput(String userStr, Socket soc){
        try {
            PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
            // send string to server
            out.println(userStr);
        } catch (IOException e){
            System.out.println("Error sending message to server");
        } catch (Exception e) {
            System.out.println("Socket disconnected abruptly " + e.getMessage());
        }
    }

    public static Thread receiveMessage(Socket soc){
        Thread thread = new Thread(() -> {
            try {
                BufferedReader serverInputStream =
                        new BufferedReader(new InputStreamReader(soc.getInputStream()));
                String serverStr;

                while((serverStr = serverInputStream.readLine()) != null){
                    System.out.println("");
                    System.out.println("Server Message: " + serverStr);
                }
            } catch (IOException e) {
                System.out.println("Unable to receive message from server " + e.getMessage());
            }
        });
        return thread;
    }
}