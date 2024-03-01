package Client.ui;

import java.util.Scanner;

import Client.Client;
import Client.Client.ClientState;

public class Menu {

    int numOptionsNotLoggedIn = 3;
    int numOptionsLoggedIn = 4;
    int numOptionsWaiting = 4;
    ClientState state; 

    private Scanner input;

    public Menu(Scanner input, ClientState state){
        this.input = input;
        this.state = state;
    }


    public static void run(ClientState state){
        switch (state) {
            case NOT_LOGGED_IN:
                nonValidatedUserMenu();
                break;
            case LOGGED_IN:
                validatedUserNotWaiting();
                break;
            case WAITING:
                validatedUserWaiting();
                break;
            default:
            break;
        }

    }

    public static void nonValidatedUserMenu(){
        System.out.println("**********************\n" +
                           "*     LOGIN MENU     *\n" +
                           "**********************\n" +
                           "*     1. NEW USER    *\n" +
                           "*     2. LOGIN       *\n" +
                           "*     3. QUIT        *\n" +
                           "**********************\n"  );
    }

    public static void validatedUserNotWaiting(){
        System.out.println("**********************\n" +
                        "*      GAME MENU     *\n" +
                        "**********************\n" +
                        "* 1. ENTER WAIT LIST *\n" +
                        "* 2. CHECK WAIT TIME *\n" +
                        "* 3. LOGOUT          *\n" +
                        "* 4. QUIT            *\n" +
                        "**********************\n"  );
    }

    public static void validatedUserWaiting(){
        System.out.println("**********************\n" +
                        "*      GAME MENU     *\n" +
                        "**********************\n" +
                        "* 1. LEVE WAIT LIST  *\n" +
                        "* 2. CHECK WAIT TIME *\n" +
                        "* 3. LOGOUT          *\n" +
                        "* 4. QUIT            *\n" +
                        "**********************\n"  );
    }


    public int getMenuInput(){
        System.out.println("Get menu option method: ");

        // get user input
        System.out.print("> ");
        String menuInput = this.input.nextLine();

        System.out.println("");

        // continue to get menu option from user until valid option is entered
        while(Integer.parseInt(menuInput) > getNumOfMenuOptions()){
            // for now we will assume input will be a number value
            System.out.println("\n> Please enter options between 1 to " + getNumOfMenuOptions());
            System.out.print("> ");
            menuInput = this.input.nextLine();
            System.out.println("");
        }

        return Integer.parseInt(menuInput);
    }

    // helper fucntion to get the number of menu
    // options based on the client state
    // is a helper function used in getMenuInput()
    private int getNumOfMenuOptions(){
        switch (state) {
            case NOT_LOGGED_IN:
                return numOptionsNotLoggedIn;
            case LOGGED_IN:
                return numOptionsLoggedIn;
            case WAITING:
                return numOptionsWaiting;
            default:
                return 0;
        }
    }
}
