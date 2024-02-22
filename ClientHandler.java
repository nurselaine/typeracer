
private static class ClientHandler implements Runnable{
    // this method accepts new incoming client connections and
    // creates a new socket object or returns null if connection was unsuccessful

    private Socket clientSocket;
    private ServerSocketService ss;

    public ClientHandler(Socket clientSocket, ServerSocketService ss){
        this.ss = ss;
    }
    public Socket acceptConnection(){
        Socket newConnection;
        try {
            newConnection = ss.accept();
            return newConnection;
        } catch (IOException e) {
            System.out.println("Unable to accept new socket connection");
            e.printStackTrace();


        }
}