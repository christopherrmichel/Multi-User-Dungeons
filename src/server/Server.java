package server;

public class Server {
    public static void main(String args[])  throws Exception {
        try {
            ServerManager sm = new ServerManager();
            //comecar o jogo
            sm.executeServer();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
