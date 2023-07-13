
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MetodoJogo extends Thread {
    private Socket player1Socket; 
    private Socket player2Socket;     
    private BufferedReader Jogador1Leu;   
    private PrintWriter Jogador1Escreveu;  
    private BufferedReader Jogador2Leu;   
    private PrintWriter Jogador2Escreveu;  
    private char[][] TabelaGame;  
    private boolean temJogoOcorrendo;  
    private int JodadorDoMomento;    
    
    public MetodoJogo(Socket player1Socket, Socket player2Socket) throws IOException {
        this.player1Socket = player1Socket;
        this.player2Socket = player2Socket;
        Jogador1Leu = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
        Jogador1Escreveu = new PrintWriter(player1Socket.getOutputStream(), true);
        Jogador2Leu = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));
        Jogador2Escreveu = new PrintWriter(player2Socket.getOutputStream(), true);
        TabelaGame = new char[3][3];
        temJogoOcorrendo = true;
        JodadorDoMomento = 1;
    }
    
    private boolean ValidaJogada(int lin, int col) {
        return lin >= 0 && lin < 3 && col >= 0 && col < 3 && TabelaGame[lin][col] == '\0';
    }

    private boolean LinhaVencedora() {
        for (int row = 0; row < 3; row++) {
            if (TabelaGame[row][0] != '\0' && TabelaGame[row][0] == TabelaGame[row][1] && TabelaGame[row][1] == TabelaGame[row][2]) {
                return true;
            }
        }
        return false;
    }

    private boolean ColunaVencedora() {
        for (int col = 0; col < 3; col++) {
            if (TabelaGame[0][col] != '\0' && TabelaGame[0][col] == TabelaGame[1][col] && TabelaGame[1][col] == TabelaGame[2][col]) {
                return true;
            }
        }
        return false;
    }

    private boolean DiagonalVencedora() {
        if (TabelaGame[0][0] != '\0' && TabelaGame[0][0] == TabelaGame[1][1] && TabelaGame[1][1] == TabelaGame[2][2]) {
            return true;
        }
        else if (TabelaGame[0][2] != '\0' && TabelaGame[0][2] == TabelaGame[1][1] && TabelaGame[1][1] == TabelaGame[2][0]) {
            return true;
        }
        return false;
    }

    private boolean checkVencedor() {
        return LinhaVencedora() || ColunaVencedora() || DiagonalVencedora();
    }

    private boolean checkEmpate() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (TabelaGame[row][col] == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    private String TabelaJogoDaVelha() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char value = TabelaGame[row][col] == '\0' ? ' ' : TabelaGame[row][col];
                sb.append(value).append(" | ");
            }
            sb.setLength(sb.length() - 3); // Remove o último " | "
            sb.append("\n---------\n");
        }
        return sb.toString();
    }

    private void SMSpraOsJogadores(String message) {
        Jogador1Escreveu.println(message);
        Jogador2Escreveu.println(message);
    }

    private void EnviarSmSPraJogador(PrintWriter writer, String message) {
        writer.println(message);
    }

    private PrintWriter PegaAEntradaDoJogadorActual() {
        return (JodadorDoMomento == 1) ? Jogador1Escreveu : Jogador2Escreveu;
    }
    
    
    
    @Override
    public void run() {
        try {
            SMSpraOsJogadores("_________________ JOGO DA VELHA << Jogador 1 começa >> ______________________");
            
            while (temJogoOcorrendo) {  
                String EntraJogadorActual;
                // Aguarda a jogada do jogador atual
                if (JodadorDoMomento == 1) {
                    EnviarSmSPraJogador(Jogador1Escreveu, "Sua vez de jogar. Entra com a posição (1 a 9):");
                    EntraJogadorActual = Jogador1Leu.readLine();
                } else {
                    EnviarSmSPraJogador(Jogador2Escreveu, "Sua vez de jogar. Entra com a posição (1 a 9):");
                    EntraJogadorActual = Jogador2Leu.readLine();
                }
                
                //Aqui a jogada do jogador atual é processada convertendo-a em posição na matriz `TabelaGame`.
                int position = Integer.parseInt(EntraJogadorActual) - 1;
                int linha = position / 3;
                int col = position % 3;

                //É verificado se a jogada é válida através do método `isValidMove()`.
                if (ValidaJogada(linha, col)) {
                    // Atualiza o tabuleiro com a jogada do jogador
                    TabelaGame[linha][col] = (JodadorDoMomento == 1) ? 'X' : 'O';

                    // Verifica se o jogador venceu ou se houve um empate
                    if (checkVencedor() || checkEmpate()) {
                        temJogoOcorrendo = false;
                        SMSpraOsJogadores(TabelaJogoDaVelha());
                        SMSpraOsJogadores((checkVencedor() ? "Jogador " + JodadorDoMomento + " venceu!" : "Empate!"));
                        SMSpraOsJogadores("Fim de jogo.");
                    } else {
                        // Troca o jogador atual
                        JodadorDoMomento = (JodadorDoMomento == 1) ? 2 : 1;
                        SMSpraOsJogadores(TabelaJogoDaVelha());
                    }     
                    
                } 
                else {
                    EnviarSmSPraJogador(PegaAEntradaDoJogadorActual(), "Jogada inválida. Tente novamente.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
