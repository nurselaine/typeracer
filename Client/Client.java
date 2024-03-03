package Client;

import Client.RPC.GameAPI;
import Client.RPC.UserAPI;
import Client.ui.Menu;

import Server.Server_context.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.util.Scanner;

// Hello world!
public class Client {

    public enum ClientState {
        NOT_LOGGED_IN,
        LOGGED_IN,
        WAITING,
        PLAYING
    }

    private ClientState state;
    private Scanner input;
    Menu menu;
    boolean isLoggedIn;
    Socket soc;
    BufferedReader serverReader;
    PrintWriter serverWriter;
    UserAPI userAPI;
    GameAPI gameAPI;
    String connected;
    User user;

    public Client(){
        this.state = ClientState.NOT_LOGGED_IN;
        this.input = new Scanner(System.in);
        this.menu = new Menu(input, state);
        try{
            this.soc = new Socket("localhost", 3001);
            this.serverReader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            this.serverWriter = new PrintWriter(soc.getOutputStream(), true);
            this.userAPI = new UserAPI(input, serverWriter, serverReader);
            this.gameAPI = new GameAPI(serverWriter, serverReader, state);
            this.connected = serverReader.readLine();
            System.out.println("Connected to server: " + connected);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // main client loop to handle user input and draw menus
    public void run()throws Exception{
        while(soc.isConnected()){
            //mapUserStateToClienState();
            menu.run(state);
            int menuOption = menu.getMenuInput(state);
            submitRPC(menuOption);
        }
    }

    /**
     * This method handles the menu option selected by the user
     * and submits the appropriate RPC to the server
     * based on state the menu option selected will submit the 
     * a differnet RPC to the server
     * 
     * @param menuOption the menu option selected by the user
     * @throws Exception 
     */
    private void submitRPC(int menuOption) throws Exception{
        switch(state){
            case NOT_LOGGED_IN:
                if(menuOption == 1){
                    userAPI.newUser();
                } else if(menuOption == 2){
                    state = userAPI.login() ? ClientState.LOGGED_IN : ClientState.NOT_LOGGED_IN;
                } else if(menuOption == 3){
                    serverWriter.println("Disconnect");
                    soc.close();
                    System.out.println("Program ending. See you next time!");
                    return; // end program
                } else {
                    System.out.println("> Invalid menu option. Please try again.");
                }
                // handle non-validated user menu
                break;
            case LOGGED_IN:
                if (menuOption == 1) {
                    if(gameAPI.joinWaitQueue()){
                        setClientState(state.WAITING);
                    }
                } else if(menuOption == 2){
                    gameAPI.checkWaitTime();
                } else if(menuOption == 3){
                    userAPI.logout();
                    state = ClientState.NOT_LOGGED_IN;
                    
                } else if(menuOption == 4){
                    serverWriter.println("Disconnect");
                    soc.close();
                    System.out.println("Program ending. See you next time!");
                    return; // end program
                } else {
                    System.out.println("> Invalid menu option. Please try again.");
                }
                // handle validated user menu
                break;
            case WAITING:
                //leave wait queue
                if(menuOption == 1){
                    if(gameAPI.leaveWaitQueue())
                        setClientState(state.LOGGED_IN);
                } else if(menuOption == 2){
                    gameAPI.checkWaitTime();
                } else if(menuOption == 3){
                    userAPI.logout();
                    state = ClientState.NOT_LOGGED_IN;
                } else if(menuOption == 4){
                    serverWriter.println("Disconnect");
                    soc.close();
                    System.out.println("Program ending. See you next time!");
                    return; // end program
                } else {
                    System.out.println("> Invalid menu option. Please try again.");
                }
                // handle waiting user menu
                break;
            case PLAYING:
                // handle playing user menu
                break;
            default:
                // handle invalid state
                break;
        }
    }

    public ClientState getStatus(){
        return state;
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client();
        client.run();
    }

    private void mapUserStateToClienState(){
        if(user == null){
            state = ClientState.NOT_LOGGED_IN;
            return;
        }
        switch(user.getStatus()){
            case LOGGEDIN:
                state = ClientState.LOGGED_IN;
                break;
            case WAITING:
                state = ClientState.WAITING;
                break;
            case PLAYING:
                state = ClientState.PLAYING;
                break;
            default:
                state = ClientState.NOT_LOGGED_IN;
                break;
        }
    }

    public void setClientState(ClientState state){
        this.state = state;
    }
}