package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import Client.RPC.GameAPI;
import Client.RPC.API;
import Client.UserInterface.Menu;
import Server.ServerContext.User;

public class Client {

    public enum ClientState {
        DISCONNECTED,
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
    API userAPI;
    boolean readyToPlay;
    GameAPI gameAPI;
    String connected;
    User user;

    public Client(){
        try{
            this.soc = new Socket("localhost", 3001);
            this.serverReader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            this.serverWriter = new PrintWriter(soc.getOutputStream(), true);
            this.connected = serverReader.readLine();
            this.readyToPlay = false;
            this.state = ClientState.NOT_LOGGED_IN;
            this.input = new Scanner(System.in);
            this.menu = new Menu(input, state);
            this.userAPI = new API(input, serverWriter, serverReader);
            System.out.println("Connected to server: " + connected);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
    }

    // main client loop to handle user input and draw menus
    public void run()throws Exception{
        while(soc.isConnected() && this.state != ClientState.DISCONNECTED){
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
    private void submitRPC(int menuOption) throws Exception {
        switch (state) {
            case NOT_LOGGED_IN:
                switch (menuOption) {
                    case 1:
                        userAPI.register();
                        break;

                    case 2:
                        // submit login request to server and update client state if successful
                        this.state = userAPI.login() ? ClientState.LOGGED_IN : ClientState.NOT_LOGGED_IN;
                        break;

                    case 3:
                        closeClient();
                        break;

                    default:
                        System.out.println("Invalid command");
                        break;
                }
                break;

            case LOGGED_IN:
                switch (menuOption) {

                    // wait rpc
                    case 1:
                        this.state = userAPI.enterWaitList() ? ClientState.WAITING : ClientState.LOGGED_IN;
                        userAPI.waitForGameStart().thenRun(() -> {
                            readyToPlay = true;
                        });

                        break;

                    // check wait time rpc 
                    case 2:
                        userAPI.checkWaitTime();
                        break;

                    // logout rpc
                    case 3:
                        state = userAPI.Logout() ? ClientState.NOT_LOGGED_IN : ClientState.LOGGED_IN;
                        break;

                    // close client
                    case 4:
                        closeClient();
                        break;

                    default:
                        System.out.println("Invalid command");
                        break;
                }
                break;

            case WAITING:
                switch (menuOption) {
                    case 1:
                        this.state = userAPI.leaveWaitList() ? ClientState.LOGGED_IN : ClientState.WAITING;
                        break;

                    case 2:
                        if(readyToPlay){
                            this.state = userAPI.enterGame() ? ClientState.PLAYING : ClientState.WAITING;
                        }
                        else{
                            System.out.println("Game not ready yet");
                        }
                        break;

                        // check wait time rpc
                        case 3:
                        userAPI.checkWaitTime();
                        userAPI.waitForGameStart().thenRun(() -> {
                            readyToPlay = true;
                        });
                        break;

                        // logout rpc
                    case 4:
                        state = userAPI.Logout() ? ClientState.NOT_LOGGED_IN : ClientState.WAITING;
                        break;

                    // quit rpc
                    case 5:
                        closeClient();
                        break;
                    default:
                        System.out.println("Invalid command");
                        break;
                }
                break;

            case PLAYING:
                switch (menuOption) {
                    case 1:
                        //Menu.inGame();
                        userAPI.playGame();

                        // Output scores
                        System.out.println("Waiting for other players to finish");
                        String response = serverReader.readLine();
                        System.out.println(response);

                        // reset data for next game
                        readyToPlay = false;
                        state = ClientState.LOGGED_IN;
                        break;

                    default:
                        break;
                }
        }
    }

    public ClientState getStatus(){
        return state;
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client();
        client.run();
    }

    public void setClientState(ClientState state){
        this.state = state;
    }

    public void closeClient() throws Exception {
        userAPI.quit();
        soc.close();
        this.state = ClientState.DISCONNECTED;
    }

}