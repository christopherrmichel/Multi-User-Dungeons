package client;

public class Client {

    public static void main(String[] args) {
        try {
            System.out.println("Você estava explorando uma área perto do lago e percebeu que havia uma pequena caverna na qual decidiu explorar.");
            Thread.sleep(1000);
            System.out.println("Logo depois de descer algumas pedras para entrar, percebeu que é impossível voltar pelo mesmo caminho...");
            Thread.sleep(1000);
            System.out.println("Sendo assim, o único jeito de sobreviver é atravessando");
            Thread.sleep(1000);
            System.out.println("Existe apenas uma saida e somente o primeiro jogador a passar por ela sobrevive");
            Thread.sleep(1000);
            System.out.println("Para iniciar digite ::CREATE_USER <apelido> inserindo um apelido de sua escolha");
            Thread.sleep(1000);
            System.out.println("Para verificar os possiveis comandos do jogo digite ::HELP");
            Thread.sleep(1000);
            System.out.println("Boa sorte!");

            ClientManager cm = new ClientManager();
            cm.executeClient();


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
