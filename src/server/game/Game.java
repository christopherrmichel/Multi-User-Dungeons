package server.game;

import server.Player;

import java.text.MessageFormat;
import java.util.*;

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

        this.maze[0][0] = new Room(null, null, null, new Door(false), Arrays.asList(new Item("chave", "chave da saida")));
        this.maze[0][1] = new Room(null, new Door(false), new Door(false), null, new ArrayList<>());
//        this.maze[1][0] = new server.game.Room(null, null, null, new server.game.Door(false), new ArrayList<>());
        this.maze[1][1] = new Room(new Door(false), null, null, null, new ArrayList<>());

        for (int i = 0; i < 2; i++) {
            System.out.println();
            for (int j = 0; j < 2; j++) {
                System.out.print("x");
            }
        }
    }

    @Override
    public GameResponse examineRoom(Player currentPlayer, List<Player> players, GameResponse gameResponse) {

        if (currentPlayer == null) {
            gameResponse.setUnicast("Player nao cadastrado");
            return gameResponse;
        }

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
        String messageUnicast = MessageFormat.format("Sua posicao atual: coordenada ({0},{1}). \nEsta sala possui: {2} portas\n" + "{3} chaves", currentPlayer.getPosX(), currentPlayer.getPosY(), doors, items.size());
        gameResponse.setUnicast(messageUnicast);
        return gameResponse;
    }

    private Room getCurrentRoom(Player player) {
        return this.maze[player.getPosX()][player.getPosY()];
    }


    @Override
    public GameResponse examineObject(Player currentPlayer, String itemName) {
        GameResponse gameResponse = new GameResponse(null, null);

        if (currentPlayer == null) {
            gameResponse.setUnicast("Player nao cadastrado");
            return gameResponse;
        }

        Item currentItem = null;

        if (nonNull(currentPlayer.getItems())) {
            currentItem = currentPlayer.getItems().stream()
                    .filter(item -> itemName.equalsIgnoreCase(item.getName()))
                    .findFirst()
                    .orElse(null);
        }

        if (nonNull(currentItem)) {
            String message = MessageFormat.format("Item examinado: \nNome: {0} \nDescricao: {1}", currentItem.getName(), currentItem.getDescription());
            gameResponse.setUnicast(message);
            return gameResponse;
        }

        gameResponse.setUnicast("Nao ha item para ser examinado");
        return gameResponse;
    }

    @Override
    public GameResponse move(Player player, String direction, List<Player> players) {

        GameResponse gameResponse = new GameResponse(null, null);

        if (nonNull(player)) {
            Room room = getCurrentRoom(player);
            int currentPosX = player.getPosX();
            int currentPosY = player.getPosY();

            switch(direction) {
                case "L":
                    if (nonNull(room.getDoorLeft())) {
                        if ((!room.getDoorLeft().isClosed()) || (room.getDoorLeft().isClosed() && userHasKey(player))) {
                            currentPosY--;
                            player.setPosY(currentPosY);
                            String message = MessageFormat.format("Player {0} moveu para a coordenada ({1},{2})", player.getName(), player.getPosX(), player.getPosY());
                            gameResponse.setMulticast(message);

                        } else {
                            gameResponse.setUnicast("Voce precisa de uma chave para abrir esta porta");
                            return gameResponse;
                        }
                    } else {
                        gameResponse.setUnicast("Nao ha uma porta nessa direcao!");
                        return gameResponse;
                    }
                    break;
                case "N":
                    if (nonNull(room.getDoorUp())) {
                        if ((!room.getDoorUp().isClosed()) || (room.getDoorUp().isClosed() && userHasKey(player))) {
                            currentPosX--;
                            player.setPosX(currentPosX);
                            String message = MessageFormat.format("Player {0} moveu para a coordenada ({1},{2})", player.getName(), player.getPosX(), player.getPosY());
                            gameResponse.setMulticast(message);
                        } else {
                            gameResponse.setUnicast("Voce precisa de uma chave para abrir esta porta");
                            return gameResponse;
                        }
                    } else {
                        gameResponse.setUnicast("Nao ha uma porta nessa direcao!");
                        return gameResponse;
                    }
                    break;
                case "R":
                    if (nonNull(room.getDoorRight())) {
                        if ((!room.getDoorRight().isClosed()) || (room.getDoorRight().isClosed() && userHasKey(player))) {
                            currentPosY++;
                            player.setPosY(currentPosY);
                            String message = MessageFormat.format("Player {0} moveu para a coordenada ({1},{2})", player.getName(), player.getPosX(), player.getPosY());
                            gameResponse.setMulticast(message);
                        } else {
                            gameResponse.setUnicast("Voce precisa de uma chave para abrir esta porta");
                            return gameResponse;
                        }
                    } else {
                        gameResponse.setUnicast("Nao ha uma porta nessa direcao!");
                        return gameResponse;
                    }
                    break;
                case "S":
                    if (nonNull(room.getDoorDown())) {
                        if ((!room.getDoorDown().isClosed()) || (room.getDoorDown().isClosed() && userHasKey(player))) {
                            currentPosX++;
                            player.setPosX(currentPosX);
                            String message = MessageFormat.format("Player {0} moveu para a coordenada ({1},{2})", player.getName(), player.getPosX(), player.getPosY());
                            gameResponse.setMulticast(message);
                        } else {
                            gameResponse.setUnicast("Voce precisa de uma chave para abrir esta porta");
                            return gameResponse;
                        }
                    } else {
                        gameResponse.setUnicast("Nao ha uma porta nessa direcao!");
                        return gameResponse;
                    }
                    break;
                default:
                    break;
            }

            return examineRoom(player, players, gameResponse);
        }
        gameResponse.setUnicast("Player nao cadastrado");
        return gameResponse;
    }

    private boolean userHasKey(Player player) {
        return player.getItems().stream().anyMatch(item -> item.getName().equalsIgnoreCase("Chave"));
    }

    @Override
    public GameResponse take(Player player, String itemName) {
        GameResponse gameResponse = new GameResponse(null, null);

        if (nonNull(player)) {
            Room room = getCurrentRoom(player);

            Item item = room.getItems().stream().filter(currentItem -> currentItem.getName().equalsIgnoreCase("chave")).findAny().orElse(null);

            if (nonNull(item)) {
                player.addItem(item);

                String uniMessage = MessageFormat.format("Voce coletou {0}", item.getName());
                String multiMessage = MessageFormat.format("Player {0} coletou {1}", player.getName(), item.getName());
                gameResponse.setUnicast(uniMessage);
                gameResponse.setMulticast(multiMessage);
                return gameResponse;
            }
        }
        gameResponse.setUnicast("Nao foi possivel coletar o item desejado");
        return gameResponse;
    }

    @Override
    public GameResponse drop(Player player, String itemName) {
        GameResponse gameResponse = new GameResponse(null, null);

        if (nonNull(player)) {
            Room room = getCurrentRoom(player);
            Item item = room.getItems().stream().filter(currentItem -> currentItem.getName().equalsIgnoreCase("chave")).findAny().orElse(null);

            if (nonNull(item)) {
                player.removeItem(item);

                String uniMessage = MessageFormat.format("Voce largou {0}", item.getName());
                String multiMessage = MessageFormat.format("Player {0} largou {1}", player.getName(), item.getName());
                gameResponse.setUnicast(uniMessage);
                gameResponse.setMulticast(multiMessage);
                return gameResponse;
            }
        }
        gameResponse.setUnicast("Nao foi possivel largar o item");
        return gameResponse;
    }

    @Override
    public GameResponse openInventory(Player player) {
        GameResponse gameResponse = new GameResponse(null, null);

         if (nonNull(player)) {
             List<String> inventory;
             inventory = player.getItems().stream().map(Item::getName).collect(toList());

             String message = MessageFormat.format("Voce possui {0} no seu inventario", inventory.toString());
             gameResponse.setUnicast(message);
             return gameResponse;
         }
         gameResponse.setUnicast("Nao foi possivel abrir o inventario");
         return gameResponse;
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
