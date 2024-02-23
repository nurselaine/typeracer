package Client.RPC;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class userRPC {

    private Scanner input;
    private PrintWriter serverWriter;
    private BufferedReader serverReader;

    public userRPC(Scanner input, PrintWriter serverWriter, BufferedReader serverReader){
        this.input = input;
        serverReader = serverReader;
        serverWriter = serverWriter;
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

    private void login(){
        String[] userCredentials = getUserCredentials();
        serverWriter.println("Login");

    }

    private String[] getUserCredentials(){
        System.out.println("****************************\n" +
                           "*  ENTER USER CREDENTIALS  *\n");
        System.out.print(  "*  USERNAME: ");
        String username = input.nextLine();
        while(!validateUsername(username)){
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

    private boolean validateUsername(String username){
        // TODO: check if username has any spaces, has non-numeric or alphabet chars and is unique
        return true;
    }

    private boolean validatePassword(){
        // TODO: check if password has spaces, has non-numeric or alphabet chars and is unique
        return true;
    }

}
