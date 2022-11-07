package server;

import server.game.Item;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private int posX;
    private int posY;
    private List<Item> items;
    private String name;
    private InetAddress IPAddress;
    private int port;
    private int multicastPort;

    public Player(String name, InetAddress IPAddress, int port) {
        this.posX = 0;
        this.posY = 0;
        this.items = new ArrayList<>();
        this.name = name;
        this.IPAddress = IPAddress;
        this.port = port;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void removeItem(Item item) {
        this.items.removeIf(currentItem -> currentItem.getName().equalsIgnoreCase(item.getName()));
    }

    public InetAddress getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(InetAddress IPAddress) {
        this.IPAddress = IPAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
