package Client.UserInterface;

import java.util.Scanner;

import Client.Client.ClientState;

public class Menu {

    int numOptionsNotLoggedIn = 3;
    int numOptionsLoggedIn = 4;
    int numOptionsWaiting = 5;
    ClientState state;

    private Scanner input;

    public Menu(Scanner input, ClientState state) {
        this.input = input;
        this.state = state;
    }

    public void run(ClientState state) {
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
            //case PLAYING:
            //break;

            default:
                break;
        }
    }

    public static void nonValidatedUserMenu() {
        System.out.println("**********************\n" +
                "*     LOGIN MENU     *\n" +
                "**********************\n" +
                "*     1. NEW USER    *\n" +
                "*     2. LOGIN       *\n" +
                "*     3. QUIT        *\n" +
                "**********************\n");
    }

    public static void validatedUserNotWaiting() {
        System.out.println("**********************\n" +
                "*      GAME MENU     *\n" +
                "**********************\n" +
                "* 1. ENTER WAIT LIST *\n" +
                "* 2. CHECK WAIT TIME *\n" +
                "* 3. LOGOUT          *\n" +
                "* 4. QUIT            *\n" +
                "**********************\n");
    }

    public static void validatedUserWaiting() {
        System.out.println("**********************\n" +
                "*      GAME MENU     *\n" +
                "**********************\n" +
                "* 1. LEAVE WAIT LIST *\n" +
                "* 2. ENTER GAME      *\n" +
                "* 3. CHECK WAIT TIME *\n" +
                "* 4. LOGOUT          *\n" +
                "* 5. QUIT            *\n" +
                "**********************\n");
    }

    public static void inGame(){
        System.out.println("**********************\n" +
                "* Enter String Bellow*\n" +
                "**********************\n");
    }


    public int getMenuInput(ClientState state) {

        if(state == ClientState.PLAYING)
            return 1;

        System.out.println("Get menu option method: ");

        // get user input
        System.out.print("> ");

        int menuInput = Integer.parseInt(this.input.nextLine());

        System.out.println("");

        // continue to get menu option from user until valid option is entered
        while (menuInput < 1 || menuInput > getNumOfMenuOptions(state) && state != ClientState.PLAYING) {

            // draw the menu again
            run(state);

            // prompt user to enter a valid option
            System.out.println("\n> Please enter options between 1 to " + getNumOfMenuOptions(state));

            System.out.print("> ");
            menuInput = Integer.parseInt(this.input.nextLine());
            System.out.println("");
        }

        return (menuInput);
    }

    // helper fucntion to get the number of menu
    // options based on the client state
    // is a helper function used in getMenuInput()
    private int getNumOfMenuOptions(ClientState state) {
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
