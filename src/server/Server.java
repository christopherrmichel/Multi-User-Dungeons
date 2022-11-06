package server;

import server.game.Game;

public class Server {
    public static void main(String args[])  throws Exception {
        try {
            Game game = new Game();
            ServerManager sm = new ServerManager(game);
            sm.executeServer();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
