
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Jogador {
    private static final String SERVER_IP = "localhost";  
    private static final int SERVER_PORT = 12345;  
    
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); 
            // Aguarda mensagem de início de jogo do servidor
            String message = reader.readLine();
            System.out.println(message);

            while (true) {
                // Aguarda mensagem do servidor
                message = reader.readLine();
                System.out.println(message);

                // Verifica se é a vez do jogador
                if (message.startsWith("Sua vez")) {
                    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                    String move = consoleReader.readLine();
                    // Envia a jogada para o servidor
                    writer.println(move);
                }
                // Verifica se o jogo terminou
                if (message.startsWith("Fim de jogo"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}