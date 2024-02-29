package Client;

import Client.RPC.GameRPC;
import Client.RPC.UserRPC;
import Client.ui.Menu;

import java.net.Socket;
import java.io.*;
import java.util.Scanner;

// Hello world!
public class Client {
    private Scanner input;
    Menu menu;
    boolean isLoggedIn;
    Socket soc;
    BufferedReader serverReader;
    PrintWriter serverWriter;
    UserRPC userAPI;
    GameRPC gameAPI;
    String connected;

    public Client() throws IOException {
        this.input = new Scanner(System.in); 
        this.menu = new Menu(input);
        isLoggedIn = false;
        try {
            soc = new Socket("localhost", 3001);
            serverReader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
             serverWriter =new PrintWriter(soc.getOutputStream(), true);
            userAPI = new UserRPC(input, serverWriter, serverReader);
            gameAPI = new GameRPC(serverWriter, serverReader);
            connected = serverReader.readLine();
            System.out.println("Connected to server: " + connected);
        } catch (Exception e){
            e.printStackTrace();
        }
        run();
    }


    public void run() throws IOException{
        while (soc.isConnected()) {

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
                        try {
                            soc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                        try {
                            soc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Program ending. See you next time!");
                        return; // end program
                    default:
                        System.out.println("> Invalid menu option. Please try again.");
                }

                // check if game is ready to start
                // server will notify client
            }

            soc.close();
            System.out.println("Socket disconnected & client will now shutdown");

        }
    }


    public static void main(String[] args) {
        try {
            Client client = new Client();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}