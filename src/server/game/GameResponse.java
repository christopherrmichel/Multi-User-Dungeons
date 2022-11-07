package server.game;

public class GameResponse {

    private String unicast;
    private String multicast;
    private boolean gameOver;

    public GameResponse(String unicast, String multicast) {
        this.unicast = unicast;
        this.multicast = multicast;
        this.gameOver = false;
    }

    public String getUnicast() {
        return unicast;
    }

    public void setUnicast(String unicast) {
        this.unicast = unicast;
    }

    public String getMulticast() {
        return multicast;
    }

    public void setMulticast(String multicast) {
        this.multicast = multicast;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
