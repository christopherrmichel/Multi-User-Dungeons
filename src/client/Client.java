package client;

public class Client {

    public static void main(String[] args) {
        try {
            System.out.println("Voce esta explorando uma area perto do lago e percebeu que ha uma pequena caverna na qual decide explorar.");
            Thread.sleep(3000);
            System.out.println("Logo depois de descer algumas pedras para entrar, percebe que e impossível voltar pelo mesmo caminho...");
            Thread.sleep(3000);
            System.out.println("Sendo assim, o unico jeito de sobreviver e atravessando o labirinto");
            Thread.sleep(3000);
            System.out.println("Para iniciar digite ::CREATE_USER <apelido> inserindo um apelido de sua escolha");
            Thread.sleep(3000);
            System.out.println("Para verificar os possiveis comandos do jogo digite ::HELP");
            Thread.sleep(3000);
            System.out.println("Existe apenas uma saida do labirinto. Boa sorte!");

            ClientManager cm = new ClientManager();
            cm.executeClient();


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
