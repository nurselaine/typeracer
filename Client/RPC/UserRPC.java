package Client.RPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class UserRPC {

    private Scanner input;
    private PrintWriter serverWriter;
    private BufferedReader serverReader;
    private String username;

    public UserRPC(Scanner input, PrintWriter serverWriter, BufferedReader serverReader){
        this.input = input;
        this.serverReader = serverReader;
        this.serverWriter = serverWriter;
    }

    public void newUser() throws IOException {

        System.out.println("New user RPC");
        this.username = getUsername();
        validateUsername();

        // send New User RPC
        String password = getPassword();
        this.serverWriter.println("New User");
        this.serverWriter.println(username);
        this.serverWriter.println(password);

        String res = this.serverReader.readLine();
        System.out.println(res);
        if(Integer.parseInt(res) != 1){
            System.out.println("> SERVER ERROR: Please try entering New User again. " + res);
        } else {
            System.out.println("> New User Profile: " + username + " successfully created!");
        }
    }
    public boolean login() throws IOException {
        String[] userCredentials = getUserCredentials();
        serverWriter.println("Login");
        serverWriter.println(userCredentials[0]);
        serverWriter.println(userCredentials[1]);
        boolean res = validateCredentials(userCredentials[1]);
        if(res){
            System.out.println("> Successfully logged in as " + this.username);
            return true;
        }
        System.out.println("Incorrect username or password - Please retry Login.");
        return false;
    }

    private String[] getUserCredentials() throws IOException {
        System.out.println("****************************\n" +
                           "*  ENTER USER CREDENTIALS  *\n");
        System.out.print(  "*  USERNAME: ");
        this.username = input.nextLine();
        System.out.println("");
        System.out.print("*  PASSWORD: ");
        String password = input.nextLine();
        System.out.println("");
        return new String[]{username, password};
    }

    public String getUsername(){
        System.out.print("> Username: ");
        this.username = this.input.nextLine();
        return username;
    }

    public String getPassword(){
        System.out.print("> Password: ");
        String password = this.input.nextLine();
        return password;
    }

    public int validateUsername() {
        // TODO: check if username has any spaces, has non-numeric or alphabet chars and is unique
        try {
            System.out.println("> validating username...");
            serverWriter.println("Valid Username");
            serverWriter.println(this.username);

            String res = serverReader.readLine();
            System.out.println(res);
            while(Integer.parseInt(res) == 0 || username.isEmpty()){
                System.out.println("> '" + this.username + "' taken. re-enter username");
                this.username = getUsername();

                System.out.println("> validating username...");
                serverWriter.println("Valid User");
                serverWriter.println(this.username);
                res = serverReader.readLine();
            }
            return 1;
        } catch (IOException e) {
            System.out.println("Error occurred while creating username. Please try again.");
        }
        return 0;
    }

    private boolean validateCredentials(String password) throws IOException {
        // TODO: check if password has spaces, has non-numeric or alphabet chars and is unique
        System.out.println("> Validating Credentials...");
        String res = this.serverReader.readLine();
        if(Integer.parseInt(res) == 1){
            return true;
        }
        return false;
    }

}
