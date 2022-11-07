package server;

import server.game.Game;
import server.game.GameResponse;
import utils.Commands;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerManager {
    private final DatagramSocket serverSocket;
    private final int MAX_BUF = 65000;
    private final static int serverPort = 7777;
    private List<Player> clients = new CopyOnWriteArrayList<>();
    public Game game;

    public ServerManager(Game game) throws IOException {
        this.serverSocket = new DatagramSocket(this.serverPort);
        this.game = game;
    }

    public void executeServer() throws IOException, InterruptedException {
        byte[] receiveData = new byte[MAX_BUF];

        while (true) {
            // declara o pacote a ser recebido
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // recebe o pacote do cliente
            this.serverSocket.receive(receivePacket);

            // pega mensagem enviada pelo cliente
            String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
            String[] splitData = sentence.split(" ");
            Commands command = this.getCommand(splitData);
            String message = this.getMessage(splitData, (command!=null && !command.equals(Commands.DEFAULT)));
            String param = this.getParam(splitData);
            if (command == null) {
                this.sendMessage("Servidor [privado]: Comando não encontrado!", receivePacket.getAddress(), receivePacket.getPort());
                continue;
            }
            Player currentPlayer;
            GameResponse gameResponse;
            switch (command) {
//TODO MENSAGENS DE INTRODUCAO DO JOGO
                case CREATE_USER:
                    this.createClient(param, receivePacket.getAddress(), receivePacket.getPort());
                    break;
                case LIST_PLAYERS:
                    this.listPlayers(receivePacket.getAddress(), receivePacket.getPort());
                    break;
                case EXAMINE_ROOM:
                    currentPlayer = getPlayerByIPAndPort(receivePacket.getAddress(), receivePacket.getPort());
                    gameResponse = this.game.examineRoom(currentPlayer, this.clients, new GameResponse(null, null));
                    if(gameResponse.getUnicast() != null) {
                        this.sendMessage(gameResponse.getUnicast(), receivePacket.getAddress(), receivePacket.getPort());
                    }
                    if(gameResponse.getMulticast() != null) {
                        this.sendMulticastFromPlayer(gameResponse.getMulticast(), receivePacket.getAddress(), receivePacket.getPort());
                    }
                    break;
                case MOVE:
                    currentPlayer = getPlayerByIPAndPort(receivePacket.getAddress(), receivePacket.getPort());
                    gameResponse = this.game.move(currentPlayer, param, this.clients);
                    if(gameResponse.getUnicast() != null)
                        this.sendMessage(gameResponse.getUnicast(), receivePacket.getAddress(), receivePacket.getPort());
                    if(gameResponse.getMulticast() != null)
                        this.sendMulticastFromPlayer(gameResponse.getMulticast(), receivePacket.getAddress(), receivePacket.getPort());
                    break;
                case EXAMINE_ITEM:
                    currentPlayer = getPlayerByIPAndPort(receivePacket.getAddress(), receivePacket.getPort());
                    gameResponse = this.game.examineObject(currentPlayer, param);
                    if(gameResponse.getUnicast() != null)
                        this.sendMessage(gameResponse.getUnicast(), receivePacket.getAddress(), receivePacket.getPort());
                    if(gameResponse.getMulticast() != null)
                        this.sendMulticastFromPlayer(gameResponse.getMulticast(), receivePacket.getAddress(), receivePacket.getPort());
                    break;
                case TAKE:
                    currentPlayer = getPlayerByIPAndPort(receivePacket.getAddress(), receivePacket.getPort());
                    gameResponse = this.game.take(currentPlayer, param);
                    if (gameResponse.getUnicast() != null)
                        this.sendMessage(gameResponse.getUnicast(), receivePacket.getAddress(), receivePacket.getPort());
                    if (gameResponse.getMulticast() != null)
                        this.sendMulticastFromPlayer(gameResponse.getMulticast(), receivePacket.getAddress(), receivePacket.getPort());
                    break;
                case DROP:
                    currentPlayer = getPlayerByIPAndPort(receivePacket.getAddress(), receivePacket.getPort());
                    gameResponse = this.game.drop(currentPlayer, param);
                    if (gameResponse.getUnicast() != null)
                        this.sendMessage(gameResponse.getUnicast(), receivePacket.getAddress(), receivePacket.getPort());
                    if (gameResponse.getMulticast() != null)
                        this.sendMulticastFromPlayer(gameResponse.getMulticast(), receivePacket.getAddress(), receivePacket.getPort());
                    break;
                case OPEN_INVENTORY:
                    currentPlayer = getPlayerByIPAndPort(receivePacket.getAddress(), receivePacket.getPort());
                    gameResponse = this.game.openInventory(currentPlayer);
                    if (gameResponse.getUnicast() != null)
                        this.sendMessage(gameResponse.getUnicast(), receivePacket.getAddress(), receivePacket.getPort());
                    if (gameResponse.getMulticast() != null)
                        this.sendMulticastFromPlayer(gameResponse.getMulticast(), receivePacket.getAddress(), receivePacket.getPort());
                    break;
                case MAP:
                    currentPlayer = getPlayerByIPAndPort(receivePacket.getAddress(), receivePacket.getPort());
                    gameResponse = this.game.openMap(currentPlayer);
                    if (gameResponse.getUnicast() != null)
                        this.sendMessage(gameResponse.getUnicast(), receivePacket.getAddress(), receivePacket.getPort());
                    if (gameResponse.getMulticast() != null)
                        this.sendMulticastFromPlayer(gameResponse.getMulticast(), receivePacket.getAddress(), receivePacket.getPort());
                    break;
                case HELP:
                    this.listCommands(receivePacket.getAddress(), receivePacket.getPort());
                    break;
                case DEFAULT:
                    //To do
                    break;
            }
        }
    }

    private void listPlayers(InetAddress address, int port) throws IOException {
        String message = "Players no jogo: ";
        for (Player player : this.clients) {
            if(!(player.getIPAddress().equals(address) && player.getPort() == port)) {
                message += player.getName() + " ";
            }
        }
        this.sendMessage(message, address, port);
    }

    private void createClient(String name, InetAddress IPAddress, int port) throws IOException, InterruptedException {
        if (!this.verifyClient(name, IPAddress, port)) return;
        Player client = new Player(name.toLowerCase(), IPAddress, port);
        this.clients.add(client);
        this.sendMulticastFromPlayer(client.getName() + " acabou de entrar no jogo", IPAddress, port);
        this.sendMessage("Usuario criado com sucesso! Para visualizar os comandos disponíveis digite ::HELP", IPAddress, port);

        // evniar mensagem de boas vindas para todos
        Thread.sleep(100);
        String message = "Servidor [para todos]: O usuário " + name + " acabou de entrar no chat!";
    }

    private void listCommands(InetAddress IPAddress, int port) throws IOException {
        StringBuilder sb = new StringBuilder("LISTA DE COMANDOS: \n\n");
        sb.append("::MOVE [Direction] = Move para a proxima sala na direcao passada como parametro (L,R,N,S);\n");
        sb.append("::EXAMINE_ROOM = Lista portas e items da sala;\n");
        sb.append("::EXAMINE_ITEM [item] = Mostra os detalhes de um item;\n");
        sb.append("::LIST_PLAYERS = Lista usuarios no jogo;\n");
        sb.append("::TAKE [item] = Pega o item na sala e adiciona no inventario;\n");
        sb.append("::DROP [item] = Remove o item do inventario e adiciona o item na sala;\n");
        sb.append("::OPEN_INVENTORY = Mostra todos os items coletados pelo player;\n");
        sb.append("::CREATE_USER [name] = cria usuario;\n");
        sb.append("::HELP = lista os comandos disponiveis no jogo;\n");
        sb.append("::MAP = Mostrar mapa;\n");
        this.sendMessage(sb.toString(), IPAddress, port);
    }

    private void sendMessage(String message, InetAddress IPAddress, int port) throws IOException {
        var buffer = message.getBytes();
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, IPAddress, port);
        this.serverSocket.send(datagram);
    }

    private void sendMulticastFromPlayer(String message, InetAddress IPAddress, int port) throws IOException {
        for (Player player : this.clients) {
            if(!(player.getIPAddress().equals(IPAddress) && player.getPort() == port)) {
                sendMessage(message, player.getIPAddress(), player.getPort());
            }
        }
    }

    private boolean verifyClient(String name, InetAddress IPAddress, int port) throws IOException {
        if (this.isEmpty(name, IPAddress, port, "Nome não informado! Por favor informe um nome para cadastrar um cliente.")) return false;
        if (this.clientIsRegistered(name, IPAddress, port)) return false;
        return true;
    }

    private boolean isEmpty(String name, InetAddress IPAddress, int port, String messageError) throws IOException {
        if (name == null || name.isEmpty()) {
            this.sendMessage("Servidor [privado]: " + messageError, IPAddress, port);
            return true;
        }
        return false;
    }

    private boolean clientIsRegistered(String name, InetAddress IPAddress, int port) throws IOException {
        Player client = this.getClientByName(name, IPAddress, port, false);
        if (client != null) {
            this.sendMessage("Servidor [privado]: Já existe um cliente cadastrado com este nome, por favor utilize um outro nome.", IPAddress, port);
            return true;
        }
        return false;
    }

    private Player getClientByPort(InetAddress IPAddress, int port) throws IOException {
        Optional<Player> clientOpt = this.clients.stream().filter(cli -> cli.getPort() == port).findFirst();
        if (clientOpt.isEmpty()) {
            this.sendMessage("Servidor [privado]: Cliente não encontrado!", IPAddress, port);
            return null;
        }
        Player client = clientOpt.get();
        return client;
    }

    private Player getClientByName(String name, InetAddress IPAddress, int port, boolean showWarning) throws IOException {
        Optional<Player> clientOpt = this.clients.stream().filter(cli -> cli.getName().equals(name.toLowerCase())).findFirst();
        if (clientOpt.isEmpty()) {
            if (showWarning) this.sendMessage("Servidor [privado]: Cliente não encontrado!", IPAddress, port);
            return null;
        }
        Player client = clientOpt.get();
        return client;
    }

    private String getMessage(String[] splitData, boolean isCommand) {
        StringBuilder sb = new StringBuilder();
        for (int i = isCommand ? 2 : 0; i < splitData.length; i++) {
            sb.append(splitData[i]);
            if (i + 1 != splitData.length) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private Commands getCommand(String[] splitData) {
        Commands command = Commands.DEFAULT;
        if (splitData[0].startsWith("::")) {
            command = Commands.valueOfAbbreviation(splitData[0].trim().toUpperCase());
        }
        return command;
    }

    private String getParam(String[] splitData) {
        String param = "";
        if (splitData.length > 1) {
            param = splitData[1];
        }
        return param;
    }

    private Player getPlayerByIPAndPort(InetAddress IPAddress, int port) {
        for (Player player : this.clients) {
            if(player.getIPAddress().equals(IPAddress) && player.getPort() == port) {
                return player;
            }
        }
        return null;
    }
}
