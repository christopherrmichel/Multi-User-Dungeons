package server.game;

import server.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class Game implements IGame{
    private Room[][] maze;
    private boolean[][] mazeConfig = {{true,true},
                                      {false,true}};

    public Game() {
        createMaze();
    }

    @Override
    public void createMaze() {
        this.maze = new Room[2][2];

        this.maze[0][0] = new Room(null, null, null, new Door(false), Arrays.asList(new Item("Chave", "Chave da Saída")));
        this.maze[0][1] = new Room(null, new Door(false), new Door(false), null, new ArrayList<>());
//        this.maze[1][0] = new server.game.Room(null, null, null, new server.game.Door(false), new ArrayList<>());
        this.maze[1][1] = new Room(new Door(false), null, null, null, new ArrayList<>());

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                System.out.println("x");
            }
        }
    }

    @Override
    public String examineRoom(Player currentPlayer, List<Player> players) {
        if (currentPlayer == null) return "Player não cadastrado";

        Room room = getCurrentRoom(currentPlayer);

        int currentPosX = currentPlayer.getPosX();
        int currentPosY = currentPlayer.getPosY();

        int doors = 0;
        if (!isNull(room.getDoorDown())) {
            doors++;
        }
        if (!isNull(room.getDoorLeft())) {
            doors++;
        }
        if (!isNull(room.getDoorRight())) {
            doors++;
        }
        if (!isNull(room.getDoorUp())) {
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
        String message = MessageFormat.format("Sua posicao atual: coordenada ({0},{1}). Esta sala possui: {2} portas\n" + "{3} items", currentPlayer.getPosX(), currentPlayer.getPosY(), doors, items.size());
        return message;
    }

    private Room getCurrentRoom(Player player) {
        return this.maze[player.getPosX()][player.getPosY()];
    }


    @Override
    public void examineObject(Player currentPlayer, String itemName) {
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
    public String move(Player player, String direction, List<Player> players) {

        if (nonNull(player)) {
            Room room = getCurrentRoom(player);
            int currentPosX = player.getPosX();
            int currentPosY = player.getPosY();

            switch(direction) {
                case "L":
                    if (nonNull(room.getDoorLeft())) {
                        if ((!room.getDoorLeft().isClosed()) || (room.getDoorLeft().isClosed() && userHasKey(player))) {
                            player.setPosY(currentPosY--);
                        } else {
                            return("Voce precisa de uma chave para abrir esta porta");
                        }
                    } else {
                        return("Nao ha uma porta nessa direcao!");
                    }
                    break;
                case "N":
                    if (nonNull(room.getDoorUp())) {
                        if ((!room.getDoorUp().isClosed()) || (room.getDoorUp().isClosed() && userHasKey(player))) {
                            player.setPosX(currentPosX--);
                        } else {
                            return("Voce precisa de uma chave para abrir esta porta");
                        }
                    } else {
                        return("Nao ha uma porta nessa direcao!");
                    }
                    break;
                case "R":
                    if (nonNull(room.getDoorRight())) {
                        if ((!room.getDoorRight().isClosed()) || (room.getDoorRight().isClosed() && userHasKey(player))) {
                            currentPosY++;
                            player.setPosY(currentPosY);
                        } else {
                            return("Voce precisa de uma chave para abrir esta porta");
                        }
                    } else {
                        return("Nao ha uma porta nessa direcao!");
                    }
                    break;
                case "S":
                    if (nonNull(room.getDoorDown())) {
                        if ((!room.getDoorDown().isClosed()) || (room.getDoorDown().isClosed() && userHasKey(player))) {
                            player.setPosX(currentPosX++);
                        } else {
                            return("Voce precisa de uma chave para abrir esta porta");
                        }
                    } else {
                        return("Nao ha uma porta nessa direcao!");
                    }
                    break;
                default:
                    break;
            }

            return examineRoom(player, players);
        }
        return("Player nao cadastrado");
    }

    private boolean userHasKey(Player player) {
        return player.getItems().stream().anyMatch(item -> item.getName().equalsIgnoreCase("Chave"));
    }

    @Override
    public void take(Player player, Item item) {

        if (nonNull(player)) {
            Room room = getCurrentRoom(player);

            if (room.getItems().contains(item)) {
                player.addItem(item);
                room.removeItem(item);
            }
        }
    }

    @Override
    public void drop(Player player, Item item) {

        if (nonNull(player)) {
            Room room = getCurrentRoom(player);

            player.removeItem(item);
            room.addItem(item);
        }
    }

    @Override
    public void openInventory(Player player) {
         List<String> inventory = new ArrayList<>();

         if (nonNull(player)) {
             inventory = player.getItems().stream().map(Item::getName).collect(toList());
         }
    }

    @Override
    public void useObject(Player player, Item item, Door door) {

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