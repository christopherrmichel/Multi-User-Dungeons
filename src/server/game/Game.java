package server.game;

import server.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class Game implements IGame{
    private Room[][] maze;
    private List<Player> players;
    private boolean[][] mazeConfig = {{true,true},
                                      {false,true}};

    @Override
    public void createMaze() {
//        for (int i = 0; i < mazeConfig.length; i++) {
//            for (int j = 0; j < mazeConfig.length; j++) {
//                if (mazeConfig[i][j]) {
//                    this.maze[i][j] = new server.game.Room();
//                }
//            }
//        }

        this.maze[0][0] = new Room(null, null, null, new Door(false), Arrays.asList(new Item("Chave", "Chave da Saída")));
        this.maze[0][1] = new Room(null, new Door(false), new Door(false), null, null);
//        this.maze[1][0] = new server.game.Room(null, null, null, new server.game.Door(false), null);
        this.maze[1][1] = new Room(new Door(false), null, null, null, null);

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                System.out.println("x");
            }
        }
    }

    @Override
    public void examineRoom(String username) {
        Player currentPlayer = getCurrentPlayer(username);
        if (currentPlayer == null) return;

        Room room = getCurrentRoom(currentPlayer);

        int currentPosX = currentPlayer.getPosX();
        int currentPosY = currentPlayer.getPosY();

        int doors = 0;
        if (isNull(room.getDoorDown())) {
            doors++;
        }
        if (isNull(room.getDoorLeft())) {
            doors++;
        }
        if (isNull(room.getDoorRight())) {
            doors++;
        }
        if (isNull(room.getDoorUp())) {
            doors++;

        }

        List<String> items = new ArrayList<>();
        if (!room.getItems().isEmpty()) {
            items = room.getItems()
                    .stream()
                    .map(Item::getName)
                    .collect(toList());
        }

        List<String> playersInRoom = new ArrayList<>();
        if (players.size() > 1) {
            playersInRoom = players.stream()
                    .filter(player -> player.getPosY() == currentPosY
                            && player.getPosX() == currentPosX)
                    .map(Player::getName)
                    .collect(toList());
        }

        // TODO Montar mensagem
        System.out.printf("Esta sala possui: %d portas\n" +
                "%d chaves", doors);

    }

    private Room getCurrentRoom(Player player) {
        return this.maze[player.getPosX()][player.getPosY()];
    }

    private Player getCurrentPlayer(String username) {
        Player currentPlayer = players.stream().filter(player -> username.equalsIgnoreCase(player.getName()))
                .findFirst().orElse(null);

        if (isNull(currentPlayer)) {
            return null;
        }
        return currentPlayer;
    }

    @Override
    public void examineObject(String username, String itemName) {
        Player currentPlayer = getCurrentPlayer(username);
        if (currentPlayer == null) return;

        String itemDescription = "";

        if (nonNull(currentPlayer.getItems())) {
            itemDescription = currentPlayer.getItems().stream()
                    .filter(item -> itemName.equals(item.getName()))
                    .findFirst()
                    .orElse(new Item())
                    .getDescription();
        }

        if (itemDescription.isEmpty()) {
            Room room = getCurrentRoom(currentPlayer);

            if (nonNull(room.getItems()))
            itemDescription = room.getItems().stream()
                    .filter(item -> itemName.equals(item.getName()))
                    .findFirst()
                    .orElse(new Item())
                    .getDescription();
        }

        // TODO Montar mensagem
        System.out.println("Retornar a descrição do item");
    }

    @Override
    public void move(String username, String direction) {
        Player player = getCurrentPlayer(username);

        if (nonNull(player)) {
            Room room = getCurrentRoom(player);
            int currentPosX = player.getPosX();
            int currentPosY = player.getPosY();

            switch(direction) {
                case "L":
                    if (nonNull(room.getDoorLeft()) && !room.getDoorLeft().isClosed()) {
                        if (userHasKey(player)) {
                            player.setPosY(currentPosY--);
                        } else {
                            System.out.println("É necessário uma chave para abrir a porta");
                        }
                    } else {
                        System.out.println("Caminho inválido!");
                    }
                    break;
                case "N":
                    if (nonNull(room.getDoorUp()) && !room.getDoorUp().isClosed()) {
                        if (userHasKey(player)) {
                            player.setPosX(currentPosX--);
                        } else {
                            System.out.println("É necessário uma chave para abrir a porta");
                        }
                    } else {
                        System.out.println("Caminho inválido!");
                    }
                    break;
                case "R":
                    if (nonNull(room.getDoorRight()) && !room.getDoorRight().isClosed()) {
                        if (userHasKey(player)) {
                            player.setPosY(currentPosY++);
                        } else {
                            System.out.println("É necessário uma chave para abrir a porta");
                        }
                    } else {
                        System.out.println("Caminho inválido!");
                    }
                    break;
                case "S":
                    if (nonNull(room.getDoorDown()) && !room.getDoorDown().isClosed()) {
                        if (userHasKey(player)) {
                            player.setPosX(currentPosX++);
                        } else {
                            System.out.println("É necessário uma chave para abrir a porta");
                        }
                    } else {
                        System.out.println("Caminho inválido!");
                    }
                    break;
                default:
                    break;
            }

            examineRoom(username);
        }
    }

    private boolean userHasKey(Player player) {
        return player.getItems().stream().anyMatch(item -> item.getName().equalsIgnoreCase("Chave"));
    }

    @Override
    public void take(String username, Item item) {
        Player player = getCurrentPlayer(username);

        if (nonNull(player)) {
            Room room = getCurrentRoom(player);

            if (room.getItems().contains(item)) {
                player.addItem(item);
                room.removeItem(item);
            }
        }
    }

    @Override
    public void drop(String username, Item item) {
        Player player = getCurrentPlayer(username);

        if (nonNull(player)) {
            Room room = getCurrentRoom(player);

            player.removeItem(item);
            room.addItem(item);
        }
    }

    @Override
    public void openInventory(String username) {
        Player player = getCurrentPlayer(username);
         List<String> inventory = new ArrayList<>();

         if (nonNull(player)) {
             inventory = player.getItems().stream().map(Item::getName).collect(toList());
         }
    }

    @Override
    public void useObject(String username, Item item, Door door) {
        Player player = getCurrentPlayer(username);

        if (nonNull(player) && nonNull(item) && door.isClosed()) {
            door.setClosed(false);
            player.removeItem(item);
        }
    }

    @Override
    public void talk() {

    }

    @Override
    public void whisp() {

    }

    @Override
    public void help() {

    }
}
