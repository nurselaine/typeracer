package Client.ui;

public class Menu {
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
}
