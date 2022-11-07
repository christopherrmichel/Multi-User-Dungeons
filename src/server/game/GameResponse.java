package server.game;

public class GameResponse {

    private String unicast;
    private String multicast;

    public GameResponse(String unicast, String multicast) {
        this.unicast = unicast;
        this.multicast = multicast;
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
}
