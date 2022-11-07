package server.game;

import java.util.List;

public class Room {

    private Door doorUp;
    private Door doorDown;
    private Door doorLeft;
    private Door doorRight;
    private List<Item> items;

    public Room(Door doorUp, Door doorDown, Door doorLeft, Door doorRight, List<Item> items) {
        this.doorUp = doorUp;
        this.doorDown = doorDown;
        this.doorLeft = doorLeft;
        this.doorRight = doorRight;
        this.items = items;
    }

    public Door getDoorUp() {
        return doorUp;
    }

    public void setDoorUp(Door doorUp) {
        this.doorUp = doorUp;
    }

    public Door getDoorLeft() {
        return doorLeft;
    }

    public void setDoorLeft(Door doorLeft) {
        this.doorLeft = doorLeft;
    }

    public Door getDoorRight() {
        return doorRight;
    }

    public void setDoorRight(Door doorRight) {
        this.doorRight = doorRight;
    }

    public Door getDoorDown() {
        return doorDown;
    }

    public void setDoorDown(Door doorDown) {
        this.doorDown = doorDown;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
