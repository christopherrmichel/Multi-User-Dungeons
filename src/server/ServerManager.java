package server;

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
    private final List<Client> clients = new CopyOnWriteArrayList<>();
    private final int MAX_BUF = 65000;
    private final static int serverPort = 9880;
    private static int multicastPort = 4446;

    public ServerManager() throws IOException {
        this.serverSocket = new DatagramSocket(this.serverPort);
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

            switch (command) {
                case CREATE_USER:
                    this.createClient(param, receivePacket.getAddress(), receivePacket.getPort());
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

    private void createClient(String name, InetAddress IPAddress, int port) throws IOException, InterruptedException {
        if (!this.verifyClient(name, IPAddress, port)) return;
        Client client = new Client(name.toLowerCase(), IPAddress, port);
        this.clients.add(client);
        this.sendMessage("Servidor [privado]: Cliente registrado com sucesso! Para visualizar os comandos disponíveis digite ::help", IPAddress, port);

        // evniar mensagem de boas vindas para todos
        Thread.sleep(100);
        String message = "Servidor [para todos]: O usuário " + name + " acabou de entrar no chat!";
    }

    private void listCommands(InetAddress IPAddress, int port) throws IOException {
        StringBuilder sb = new StringBuilder("LISTA DE COMANDOS: \n\n");
        sb.append("::CREATE_USER [name] – criar  usuário;\n");
        sb.append("::HELP – listar os comandos;\n");
        this.sendMessage(sb.toString(), IPAddress, port);
    }

    private void sendMessage(String message, InetAddress IPAddress, int port) throws IOException {
        var buffer = message.getBytes();
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, IPAddress, port);
        this.serverSocket.send(datagram);
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
        Client client = this.getClientByName(name, IPAddress, port, false);
        if (client != null) {
            this.sendMessage("Servidor [privado]: Já existe um cliente cadastrado com este nome, por favor utilize um outro nome.", IPAddress, port);
            return true;
        }
        return false;
    }

    private Client getClientByPort(InetAddress IPAddress, int port) throws IOException {
        Optional<Client> clientOpt = this.clients.stream().filter(cli -> cli.getPort() == port).findFirst();
        if (clientOpt.isEmpty()) {
            this.sendMessage("Servidor [privado]: Cliente não encontrado!", IPAddress, port);
            return null;
        }
        Client client = clientOpt.get();
        return client;
    }

    private Client getClientByName(String name, InetAddress IPAddress, int port, boolean showWarning) throws IOException {
        Optional<Client> clientOpt = this.clients.stream().filter(cli -> cli.getName().equals(name.toLowerCase())).findFirst();
        if (clientOpt.isEmpty()) {
            if (showWarning) this.sendMessage("Servidor [privado]: Cliente não encontrado!", IPAddress, port);
            return null;
        }
        Client client = clientOpt.get();
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
}
