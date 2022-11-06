package server.game;

import server.Player;

import java.util.List;

public interface IGame {

    void createMaze();

    String examineRoom(Player currentPlayer, List<Player> players);

    void examineObject(Player player, String itemName);

    void move(Player player, String direction, List<Player> players);

    void take(Player player, Item item);

    void drop(Player player, Item item);

    void openInventory(Player player);

    void useObject(Player player, Item item, Door door);

    void talk();

    void whisp();

    void help();

}
