package Client.ui;

import java.util.Scanner;

public class Menu {

    private Scanner input;

    public Menu(Scanner input){
        this.input = input;
    }
    public void nonValidatedUserMenu(){
        System.out.println("**********************\n" +
                           "*     LOGIN MENU     *\n" +
                           "**********************\n" +
                           "*     1. NEW USER    *\n" +
                           "*     2. LOGIN       *\n" +
                           "*     3. QUIT        *\n" +
                           "**********************\n"  );
    }

    public void validatedUserMenu(){
        System.out.println("**********************\n" +
                        "*      GAME MENU     *\n" +
                        "**********************\n" +
                        "* 1. ENTER WAIT LIST *\n" +
                        "* 2. CHECK WAIT TIME *\n" +
                        "* 3. LEAVE WAIT LIST *\n" +
                        "* 4. LOGOUT          *\n" +
                        "* 5. QUIT            *\n" +
                        "**********************\n"  );
    }

    public void validatedGameMenu(){
        System.out.println("**********************\n" +
                "*      GAME OVER     *\n" +
                "**********************\n" +
                "* 1. ENTER WAIT LIST *\n" +
                "* 2. CHECK WAIT TIME *\n" +
                "* 3. SEE LEADERBOARD *\n" +
                "* 4. LOGOUT          *\n" +
                "* 5. QUIT            *\n" +
                "**********************\n"  );
    }

    public String getMenuInput(boolean isValidated){
        // TODO: handle bad client input
        System.out.println("Get menu option method: ");
        int menuOptions = isValidated ? 5 : 3;

        // get user input
        System.out.print("> ");
        String menuInput = this.input.nextLine();
        System.out.println("");

        // continue to get menu option from user until valid option is entered
        while(Integer.parseInt(menuInput) > menuOptions){
            // for now we will assume input will be a number value
            System.out.println("\n> Please enter options between 1 to " + menuOptions);
            System.out.print("> ");
            menuInput = this.input.nextLine();
            System.out.println("");
        }

        return menuInput;
    }

}
