package packages;

import java.io.Serializable;

public class MyUser implements Serializable {
    public String username;
    public String password;
    public MyUser(String username, String password){
        this.username = username;
        this.password = password;
    }
}
