package Client.RPC;

import packages.MyUser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class UserRPC {

    private Scanner input;
    private PrintWriter serverWriter;
    private BufferedReader serverReader;

    private ObjectOutputStream serializedOutput;
    private String username;

    public UserRPC(Socket clientSocket, Scanner input, PrintWriter serverWriter, BufferedReader serverReader) throws IOException {
        this.input = input;
        this.serverReader = serverReader;
        this.serverWriter = serverWriter;
        this.serializedOutput = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    public void newUser() throws IOException {

        System.out.println("New user RPC");
        this.username = getUsername();
        if(validateUsername() == 1){
            // send New User RPC
            String password = getPassword();
            this.serverWriter.println("New User");

            MyUser newUser = new MyUser(username, password);
            serializedOutput.writeObject(newUser);
            serializedOutput.flush();

            String res = this.serverReader.readLine();
            System.out.println(res);
            if(Integer.parseInt(res) != 1){
                System.out.println("> SERVER ERROR: Please try entering New User again. " + res);
            } else {
                System.out.println("> New User Profile: " + username + " successfully created!");
            }
        }
    }
    public boolean login() throws IOException {
        String[] userCredentials = getUserCredentials();
        serverWriter.println("Login");
        MyUser newUser = new MyUser(userCredentials[0], userCredentials[1]);
        System.out.println("creds: " + newUser.username + " " + newUser.password);
        serializedOutput.writeObject(newUser);
        serializedOutput.flush();
        boolean res = validateCredentials(userCredentials[1]);
        if(res){
            System.out.println("> Successfully logged in as " + this.username);
            return true;
        }
        System.out.println("Incorrect username or password - Please retry Login.");
        return false;
    }

    public void logout(){
        serverWriter.println("Logout");
        System.out.println("> Successfully logged out!");
    }

    private String[] getUserCredentials() throws IOException {
        System.out.println("****************************\n" +
                           "*  ENTER USER CREDENTIALS  *\n");
        System.out.print(  "*  USERNAME: ");
        this.username = input.nextLine();
        System.out.print("*  PASSWORD: ");
        String password = input.nextLine();
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
            System.out.println("server res: " + res);
            while(Integer.parseInt(res) == 0 || username.isEmpty()){
                System.out.println("> '" + this.username + "' taken. re-enter username");
                this.username = getUsername();

                System.out.println("> validating username...");
                serverWriter.println("Valid Username");
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
