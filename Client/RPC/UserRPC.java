package Client.RPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class UserRPC {

    private Scanner input;
    private PrintWriter serverWriter;
    private BufferedReader serverReader;

    public UserRPC(Scanner input, PrintWriter serverWriter, BufferedReader serverReader){
        this.input = input;
        this.serverReader = serverReader;
        this.serverWriter = serverWriter;
    }

    public void newUser() throws IOException {

        System.out.println("New user RPC");
        String username = getUsername();
        validateUsername(username);

        // send New User RPC
        String password = getPassword();
        this.serverWriter.println("New User");
        this.serverWriter.println(username);
        this.serverWriter.println(password);
        System.out.println("> New User Profile: " + username + " successfully created!");


    }
    private void login() throws IOException {
        String[] userCredentials = getUserCredentials();
        serverWriter.println("Login");

    }

    private String[] getUserCredentials() throws IOException {
        System.out.println("****************************\n" +
                           "*  ENTER USER CREDENTIALS  *\n");
        System.out.print(  "*  USERNAME: ");
        String username = input.nextLine();
        while(validateUsername(username) == 0){
            System.out.println("*   BAD USER CREDENTIALS   *");
            System.out.println(  "\n*  RE-ENTER USERNAME: ");
            username = input.nextLine();
        }
        System.out.println("*  PASSWORD: \n");
        String password = input.nextLine();
        while(!validatePassword()){
            System.out.println("*   BAD USER CREDENTIALS   *");
            System.out.println(  "\n*  RE-ENTER PASSWORD: ");
            password = input.nextLine();
        }
        return new String[]{username, password};
    }

    public String getUsername(){
        System.out.print("> Username: ");
        String username = this.input.nextLine();
        return username;
    }

    public String getPassword(){
        System.out.print("> Password: ");
        String password = this.input.nextLine();
        return password;
    }

    public int validateUsername(String username) {
        // TODO: check if username has any spaces, has non-numeric or alphabet chars and is unique
        try {
            this.serverWriter.println(username);
            System.out.println("> validating username...");
            serverWriter.println("Valid Username");
            serverWriter.println(username);

            String res = serverReader.readLine();
            while(res == "0"){
                System.out.println("> '" + username + "' taken. re-enter username");
                username = getUsername();

                System.out.println("> validating username...");
                serverWriter.println("Valid User");
                serverWriter.println(username);
                res = serverReader.readLine();
            }
            return 1;
        } catch (IOException e) {
            System.out.println("Error occurred while creating username. Please try again.");
        }
        return 0;
    }

    private boolean validatePassword(){
        // TODO: check if password has spaces, has non-numeric or alphabet chars and is unique
        return true;
    }

    public String serverResponse() throws IOException {
        String res = this.serverReader.readLine();
        return res;
    }

    public void handleRequest(String userInput){
        switch(userInput){
            case "1":
                break;
            case "2":
                break;
            case "3":
                break;
            default:
                System.out.println("> Please select valid menu option");
        }
    }

}
