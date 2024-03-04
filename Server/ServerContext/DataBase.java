package Server.ServerContext;

import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class DataBase {

    Scanner fileReader;
    UserCache userCache;
    private Path path; 

    public DataBase(Path path, UserCache userCache) {
        this.path = path; 
        this.userCache = userCache;

        try {
            this.fileReader = new Scanner(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

    } 

    /// Elaine code. do not touch
    public void run() {
        while (fileReader.hasNext()) {

            String[] user_credentials = fileReader.nextLine().split(" ");

            if (user_credentials.length != 3) {
                System.out.println("ERROR: user database file is not in the correct format");
            }
            

            int colon = user_credentials[0].indexOf(':');
            String host = user_credentials[0].substring(0, colon);

            this.userCache.addNewUser(new User(host, user_credentials[1], user_credentials[2]));

            try {
                this.userCache.addNewUser(new User(host, user_credentials[1], user_credentials[2]));
            } catch (Exception e) {
                System.out.println("Error adding user to user cache");
            }

        }

            fileReader.close();
    }

}
