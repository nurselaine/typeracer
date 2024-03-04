package Client.UserInterface;

import java.util.Scanner;

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

            case PLAYING:
                inGameMenu();
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

    public static void inGameMenu(){
        System.out.println("**********************\n" +
                        "*      Game jksdfjasd;js           *\n" +
                        "**********************\n" +
                        "* 1. ENTER WAIT LIST *\n" +
                        "* 2. CHECK WAIT TIME *\n" +
                        "* 3. LOGOUT          *\n" +
                        "* 4. QUIT            *\n" +
                        "**********************\n"  );
    }

    public int getMenuInput(ClientState state){
        System.out.println("Get menu option method: ");

        // get user input
        System.out.print("> ");
        String menuInputValidation = this.input.nextLine();
        
        // validating text is only digits (for menu options)
        if(menuInputValidation.length() != 1 && !menuInputValidation.matches("[0-9]+")){
            return -1;
        }
        int menuInput = Integer.parseInt(this.input.nextLine());

        System.out.println("");

        // continue to get menu option from user until valid option is entered
        while(menuInput < 1 || menuInput > getNumOfMenuOptions(state)){

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
    private int getNumOfMenuOptions(ClientState state){
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

// TODO: Disconnect the client properly - ensure to set clienthandler to null & see logout user for example
// TODO: Fix client side improper input bug
// 