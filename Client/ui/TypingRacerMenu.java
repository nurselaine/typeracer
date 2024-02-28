package Client.ui;
import java.util.Scanner;

public class TypingRacerMenu {

    private boolean isLoggedIn = false;
    private Scanner scanner;

    public TypingRacerMenu() {
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        // Initial login
        while (!isLoggedIn) {
            System.out.println("Please log in to continue.");
            System.out.print("Enter command (login to continue): ");
            String command = scanner.nextLine();
            if ("login".equalsIgnoreCase(command)) {
                isLoggedIn = true;
                System.out.println("Login successful.");
            } else {
                System.out.println("Invalid command. Please type " +
                        "'login' to log in.");
            }
        }

        // Main menu loop
        boolean isRunning = true;
        while (isRunning && isLoggedIn) {
            System.out.println("\nTypingRacer Game Menu:");
            System.out.println("1. Start the game");
            System.out.println("2. Check game score");
            System.out.println("3. Check last game rank");
            System.out.println("4. Current Game ID");
            System.out.println("5. Exit game");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    startGame();
                    break;
                case "2":
                    checkGameScore();
                    break;
                case "3":
                    checkLastGameRank();
                    break;
                case "4":
                    getCurrentGameId();
                    break;
                case "5":
                    System.out.println("Exiting game. Thank you for playing!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice, please enter " +
                            "a number between 1 and 5.");
            }
        }
    }

    private void startGame() {
        System.out.println("Starting the game...");
    }

    private void checkGameScore() {
        System.out.println("Your game score is: [score placeholder]");
    }

    private void checkLastGameRank() {
        System.out.println("Your last game rank was: [rank placeholder]");
    }

    private void getCurrentGameId() {
        System.out.println("Your current game ID is: [ID placeholder]");
    }

    public void closeScanner() {
        if(scanner != null) {
            scanner.close();
        }
    }
}