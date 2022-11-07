package server.game;

import server.Player;

import java.util.List;

public interface IGame {

    void createMaze();

    GameResponse examineRoom(Player currentPlayer, List<Player> players);

    GameResponse examineObject(Player player, String itemName);

    GameResponse move(Player player, String direction, List<Player> players);

    String take(Player player, String itemName);

    void drop(Player player, Item item);

    void openInventory(Player player);

    void useObject(Player player, Item item, Door door);

    void talk();

    void whisp();

    void help();

}
