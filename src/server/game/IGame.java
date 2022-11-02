package server.game;

public interface IGame {

    void createMaze();

    void examineRoom(String username);

    void examineObject(String username, String itemName);

    void move(String username, String direction);

    void take(String username, Item item);

    void drop(String username, Item item);

    void openInventory(String username);

    void useObject(String username, Item item, Door door);

    void talk();

    void whisp();

    void help();

}
