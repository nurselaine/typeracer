
import java.io.IOException;
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String clientFile = "Client/Client.java";
        String serverFile = "Server/Server.java";
        String compiledClientFile = "Client";
        String compiledServerFile = "Server";
        String serverComand;
        String clientCommand;
        String currentDir;

        String hostOS;

        System.out.println("Compiling project...");

        ProcessBuilder clientCompiler = new ProcessBuilder("javac", clientFile);
        ProcessBuilder serverCompiler = new ProcessBuilder("javac", serverFile);

        Process compileProcessClient = clientCompiler.start();
        compileProcessClient.waitFor();

        if(compileProcessClient.exitValue() != 0){
            System.out.println("Client compilation failed");
            //System.exit(1);
        }

        Process compileProcessServer = serverCompiler.start();
        compileProcessServer.waitFor();

        if(compileProcessServer.exitValue() != 0){
            System.out.println("Server compilation failed");
            System.exit(1);
        }

        serverComand = "java " + compiledServerFile;
        clientCommand = "java " + compiledClientFile;

        ProcessBuilder serverRunner = new ProcessBuilder(serverComand);

        ProcessBuilder clientRunner = new ProcessBuilder(clientCommand);

        hostOS = getHostOS();

        currentDir = System.getProperty("user.dir");

        switch (hostOS) {
            case "windows":
                serverRunner.command("cmd", "/c", "start", "cmd", "/k", "cd \"" + currentDir + "\" && " + serverComand);
                clientRunner.command("cmd", "/c", "start", "cmd", "/k", "cd \"" + currentDir + "\" && " + clientCommand);

                break;

            case "mac":
                serverRunner.command("osascript", "-e", "tell app \"Terminal\" to do script \"cd " + currentDir + "; " +
                        serverComand + "\"");
                clientRunner.command("osascript", "-e", "tell app \"Terminal\" to do script \"cd " + currentDir + "; " +
                        clientCommand + "\"");
                break;

            default:
                serverRunner.command("gnome-terminal", "--", "bash", "-c", "cd " + currentDir + "; " + serverComand);
                clientRunner.command("gnome-terminal", "--", "bash", "-c", "cd " + currentDir + "; " + clientCommand);
                break;
        }

        try {
            serverRunner.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try{
            clientRunner.start();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String getHostOS(){

        //get operating systemkjjj
        String os = System.getProperty("os.name").toLowerCase();

        if(os.indexOf("win") >= 0){
            return "windows";
        } else if(os.indexOf("mac") >= 0) {
            return "mac";
        }
        else if(os.indexOf("linux") >= 0) {
            return "linux";
        }
        else {
            return "unknown";
        }
    }

}