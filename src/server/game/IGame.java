package server.game;

import server.Player;

import java.util.List;

public interface IGame {

    void createMaze();

    GameResponse examineRoom(Player currentPlayer, List<Player> players, GameResponse gameResponse);

    GameResponse examineObject(Player player, String itemName);

    GameResponse move(Player player, String direction, List<Player> players);

    GameResponse take(Player player, String itemName);

    GameResponse drop(Player player, String itemName);

    GameResponse openInventory(Player player);

    void talk();

    void whisp();

    void help();

}
