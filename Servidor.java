
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Servidor {
    private static final int PORT = 12345;  //11220   //5000

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado. Aguardando jogadores...");
            Socket Sock1 = serverSocket.accept();
            System.out.println("#### Jogador 1 se conectou ###");
            Socket Sock2 = serverSocket.accept();
            System.out.println("### Jogador 2 se conectou ###");
            MetodoJogo usa = new MetodoJogo(Sock1, Sock2);
            usa.start();
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro "+e);
        }
    }
}