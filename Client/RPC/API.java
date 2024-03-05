package Client.RPC;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.ObjectInputFilter.Status;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class API {
    private Scanner input;
    private PrintWriter serverWriter;
    private BufferedReader serverReader;

    public API(Scanner input, PrintWriter serverWriter, BufferedReader serverReader) {
        this.input = input; 
        this.serverReader = serverReader;
        this.serverWriter = serverWriter;
    }

    public void register() {
        // send register request to server
        serverWriter.println("Register");
        System.out.println("New user RPC");

        String username = getInputFromUser("Enter username");

        // submit user name to server
        sendMessage(username);

        // wait for server response
        int response = Integer.parseInt(receiveMessage());

        if (response == 1) {
            System.out.println("username " + username + " is available");
        } else {
            System.out.println("User registration failed\nUsername " + username + " is taken");
            return;
        }

        // get password from user
        String password = getInputFromUser("Enter password");

        // submit password to server
        sendMessage(password);

        // wait for server response
        response = Integer.parseInt(receiveMessage());

        if (response == 1) {
            System.out.println("User " + username + " registered successfully");
        } else {
            System.out.println("User registration failed");
            return;
        }
    }

    public boolean login() {
        System.out.println("LOGIN HANDLER CLIENT SIDE");
        serverWriter.println("Login");

        String username = getInputFromUser("Enter username");

        // submit user name to server
        sendMessage(username);

        String password = getInputFromUser("Enter password");

        // submit password to server
        sendMessage(password);

        // wait for server response
        int response = Integer.parseInt(receiveMessage());

        // if username not found
        if (response == 0) {
            System.out.println("Username not found");
        }

        // user already connected
        else if (response == 1) {
            System.out.println("User already connected");
        }

        // user password incorrect
        else if (response == 2) {
            System.out.println("Invalid password");
        }

        // user password correct
        else if (response == 3) {
            System.out.println("User " + username + " logged in successfully");
            return true;
        }

        // unknown error
        else if (response == -1) {
            System.out.println("Unknown error");
        }

        return false;
    }

    public boolean Logout() {
        // send logout request to server
        serverWriter.println("Logout");

        // wait for server response
        int response = Integer.parseInt(receiveMessage());
        boolean isLoggedout = false;
        if (response == 1) {
            System.out.println("User logged out successfully");
            isLoggedout = true;
        } else {
            System.out.println("User logout failed");
        }

        return isLoggedout;
    }

    public void quit() {
        serverWriter.println("Quit");
        String response = receiveMessage();
        if (response.equals("1")) {
            System.out.println("User quit successfully");
        }
    }

    public String getInputFromUser(String prompt) {
        System.out.println("> " + prompt + ": ");
        String username = this.input.nextLine();
        return username;
    }

    public void sendMessage(String message) {
        serverWriter.println(message);
    }

    public String receiveMessage() {
        String message = "";
        try {
            message = serverReader.readLine();
            // check if message is game start and update client state
            // print out game start status
            if(message.equals("GameStart")){
                if(serverReader.ready()){
                    String message2 = serverReader.readLine();
                    System.out.println("> Game start. You can enter game with option 2.");
                    return message2;
                } 

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    public void close() {
        try {
            serverReader.close();
            serverWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean enterWaitList() {
        sendMessage("EnterWaitList");

        // wait for success message
        String response = receiveMessage();

        if (response.equals("1")) {
            System.out.println("User entered into wait list");

            return true;
        }

        if (response.equals("0")) {
            System.out.println("User is already in wait list");
            return false;
        }

        System.out.println("unknown error entering user into wait list");
        return false;

    }

    public boolean leaveWaitList() {
        sendMessage("LeaveWaitList");

        String response = receiveMessage();

        if (response.equals("1")) {
            System.out.println("User left wait list");
            return true;
        }

        System.out.println("User is not in wait list");
        return false;
    }

    /**
     * This method handles checking the size of the
     * current wait queue and calculating how many players
     * are needed to join before user can enter game
     * 
     * Precondition: User status is LOGGED_IN
    */
    public void checkWaitTime(){
        // send RPC to server
        sendMessage("CheckWaitList");

        // wait for response
        String response = receiveMessage();

        // validate response
        if(!response.matches("[0-9]+")){
            System.out.println("> Please check again in a few seconds. Server unable to process request.");
        }
        int queueSize = Integer.parseInt(response);
        int playersLeft = queueSize;
        
        // calculate how many players are left
        if(queueSize > 4){
            playersLeft = queueSize % 4;
        } else {
            playersLeft = 4 - queueSize;
        }

        // print results to client
        String plural = playersLeft == 1 ? "player" : "players";
        System.out.println("> Wait list players currently waiting on " + playersLeft 
        + " " + plural + " to join");
    }

    public boolean enterGame() {
        sendMessage("EnterGame");

        String response = receiveMessage();

        if (response.equals("1")) {
            System.out.println("User entered into game");
            return true;
        }

        System.out.println("User is not in wait list");
        return false;
    }

    public boolean playGame() {
        sendMessage("PlayGame");

        String response = receiveMessage();

        if (response.equals("1")) {
            System.out.println("User is playing game");
        }
        else if(response.equals("0")){
            System.out.println("User is not in game");
            return false;
        }   

        String str = receiveMessage();

        

        do{

        System.out.println("\n" + str + "\033[0m");
        String input = getInputFromUser("Enter string");
        sendMessage(input);
        
        } while(receiveMessage().equals("0"));  

        System.out.println("Correct string inputed");

        return true;
    }

    /**
     * wait for game to start on server
     * 
     * @return
     */
    public CompletableFuture<Void> waitForGameStart() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String response = receiveMessage();
                if (response.equals("GameStart")) {

                    System.out.println("Game is ready to start\nEnter 2 to start game");

                }

                else {
                    System.out.println("left wait list");
                }

            } catch (Exception e) {
                // Handle exception
            }
            return null;
        });
    }
}