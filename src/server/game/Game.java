package server.game;

import server.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class Game implements IGame{
    private Room[][] maze;

    public Game() {
        createMaze();
    }

    @Override
    public void createMaze() {
        this.maze = new Room[5][5];

        this.maze[0][0] = new Room(null, new Door(false), null, new Door(false), new ArrayList<>());
        this.maze[0][1] = new Room(null, new Door(false), new Door(false), new Door(false), new ArrayList<>());
        this.maze[0][2] = new Room(null, new Door(false), new Door(false), new Door(false), new ArrayList<>());
        this.maze[0][3] = new Room(null, null, new Door(false), new Door(false), new ArrayList<>());
        this.maze[0][4] = new Room(null, new Door(false), new Door(false), null, new ArrayList<>());

        this.maze[1][0] = new Room(new Door(false), new Door(false), null, new Door(false), new ArrayList<>());
        this.maze[1][1] = new Room(new Door(false), null, new Door(false), new Door(false), new ArrayList<>());
        this.maze[1][2] = new Room(new Door(false), new Door(false), new Door(false), null, asList(new Item("chave", "chave da saida")));
        this.maze[1][3] = null;
        this.maze[1][4] = new Room(new Door(false), new Door(false), null, null, new ArrayList<>());

        this.maze[2][0] = new Room(new Door(false), new Door(false), null, null, new ArrayList<>());
        this.maze[2][1] = null;
        this.maze[2][2] = new Room(new Door(false), new Door(false), null, new Door(false), new ArrayList<>());
        this.maze[2][3] = new Room(null, new Door(false), new Door(false), new Door(false), asList(new Item("chave", "chave da saida")));
        this.maze[2][4] = new Room(new Door(false), new Door(false), new Door(false), null, new ArrayList<>());

        this.maze[3][0] = new Room(new Door(false), new Door(false), null, new Door(false), new ArrayList<>());
        this.maze[3][1] = new Room(null, new Door(false), new Door(false), new Door(false), asList(new Item("chave", "chave da saida")));
        this.maze[3][2] = new Room(new Door(false), new Door(false), new Door(false), new Door(false), new ArrayList<>());
        this.maze[3][3] = new Room(new Door(false), new Door(false), new Door(false), new Door(false), new ArrayList<>());
        this.maze[3][4] = new Room(new Door(false), new Door(true), new Door(false), null, new ArrayList<>());

        this.maze[4][0] = new Room(new Door(false), null, null, new Door(false), new ArrayList<>());
        this.maze[4][1] = new Room(new Door(false), null, new Door(false), new Door(false), new ArrayList<>());
        this.maze[4][2] = new Room(new Door(false), null, new Door(false), new Door(false), new ArrayList<>());
        this.maze[4][3] = new Room(new Door(false), null, new Door(false), new Door(true), new ArrayList<>());
        this.maze[4][4] = new Room(new Door(false), null, new Door(false), null, new ArrayList<>());
    }

    @Override
    public GameResponse examineRoom(Player currentPlayer, List<Player> players, GameResponse gameResponse) {

        if (currentPlayer == null) {
            gameResponse.setUnicast("Player nao cadastrado");
            return gameResponse;
        }

        if (isFinalRoom(currentPlayer)) {
            gameResponse.setUnicast("Parabens, voce chegou ao final do labirinto.\n FIM DE JOGO!");
            gameResponse.setMulticast("Player {0} venceu o jogo.\n FIM DE JOGO!");
            gameResponse.setGameOver(true);
            return gameResponse;
        }

        Room room = getCurrentRoom(currentPlayer);

        int currentPosX = currentPlayer.getPosX();
        int currentPosY = currentPlayer.getPosY();

        Door left = null;
        Door right = null;
        Door up = null;
        Door down = null;
        if (!isNull(room.getDoorDown())) {
            down = room.getDoorDown();
        }
        if (!isNull(room.getDoorLeft())) {
            left = room.getDoorLeft();
        }
        if (!isNull(room.getDoorRight())) {
            right = room.getDoorRight();
        }
        if (!isNull(room.getDoorUp())) {
            up = room.getDoorUp();
        }

        List<String> items = new ArrayList<>();
        if (!room.getItems().isEmpty()) {
            items = room.getItems()
                    .stream()
                    .map(Item::getName)
                    .collect(toList());
        }
//TODO ARRUMAR MOSTRAR PLAYERS NA SALA
        List<String> playersInRoom = new ArrayList<>();
        if (players.size() > 1) {
            playersInRoom = players.stream()
                    .filter(player -> (player.getPosY() == currentPosY
                            && player.getPosX() == currentPosX) && !player.getName().equalsIgnoreCase(currentPlayer.getName()))
                    .map(Player::getName)
                    .collect(toList());
        }

        String messageUnicast = MessageFormat.format("Sua posicao atual: coordenada ({0},{1}). \nEsta sala possui:\n" +
                "{2}" +
                "{3}" +
                "{4}" +
                "{5}" +
                "{6} chaves\n" +
                "{7}",
                currentPlayer.getPosX(),
                currentPlayer.getPosY(),
                nonNull(down) ? "Uma porta " + checkDoor(down) + " para o Sul\n" : "",
                nonNull(left) ? "Uma porta " + checkDoor(left) + " para o Oeste\n" : "",
                nonNull(up) ? "Uma porta " + checkDoor(up) + " para o Norte\n" : "",
                nonNull(right) ? "Uma porta " + checkDoor(right) + " para o Leste\n" : "",
                items.size(),
                !playersInRoom.isEmpty() ? playersInRoom + " esta(ao) presente(s) na mesma sala que voce" : "");
        gameResponse.setUnicast(messageUnicast);
        return gameResponse;
    }

    private String checkDoor(Door door) {
        if (door.isClosed()) {
            return "fechada";
        } else {
            return "aberta";
        }
    }

    private Room getCurrentRoom(Player player) {
        return this.maze[player.getPosX()][player.getPosY()];
    }

    private boolean isFinalRoom(Player player) {
        if (player.getPosX() == 4 && player.getPosY() == 4) {
            return true;
        } else {
            return false;
        }
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
                            gameResponse.setUnicast("Voce precisa encontrar uma chave no labirinto para abrir esta porta");
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

    private boolean roomHasKey(Room room) {
        return room.getItems().stream().anyMatch(item -> item.getName().equalsIgnoreCase("Chave"));
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
//TODO VERIFICAR PROBLEMA
        if (nonNull(player)) {
            Room room = getCurrentRoom(player);
            Item item = room.getItems().stream().filter(currentItem -> currentItem.getName().equalsIgnoreCase("chave")).findAny().orElse(null);

            if (nonNull(item)) {
                player.removeItem(item);

                if (!roomHasKey(room)) {
                    room.getItems().add(item);
                }

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

    @Override
    public GameResponse openMap(Player player) {
        GameResponse gameResponse = new GameResponse(null, null);
        StringBuilder message = new StringBuilder();
        if (nonNull(player)) {
            for (int i = 0; i < maze.length; i++) {
                message.append("\n");
                for (int j = 0; j < maze.length; j++) {
                    if (player.getPosX() == i && player.getPosY() == j) {
                        message.append("o  ");
                    } else {
                        message.append("x  ");
                    }
                }
            }
            gameResponse.setUnicast(message.toString());
            return gameResponse;
        }
        gameResponse.setUnicast("Erro ao mostrar o mapa");
        return gameResponse;
    }
}
