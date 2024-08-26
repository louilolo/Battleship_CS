import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Cliente {
    String HOST = "localhost";
    int PORT = 44444;
    private Socket cliente;
    private final DataInputStream in;
    private final DataOutputStream out;
    BatalhaNaval jogo = new BatalhaNaval();
    int[][] posicaoBarcos = new int[5][4];

    public Cliente() throws IOException{
        //construtor estabelece a conexao com o host a partir do IP e da Porta
        Socket cliente = new Socket(HOST, PORT);
        //instanciadas variaveis que permitem a leitura e escrita de dados primitivos
        in = new DataInputStream(cliente.getInputStream());
        out = new DataOutputStream(cliente.getOutputStream());
    }

    public void Comunicacao() throws IOException {
        int[] bomba = new int[2];
        String leitura = in.readUTF();
        do{
            //filtra o tipo de sinal recebido pelo servidor
            switch (leitura) {
                //sinal de conexao
                case "A" -> ChecaConexao();
                //sinal que filtra de quem é a vez
                case "B" -> bomba = ProcessaVez();
                //sinal que indica se o jogador acertou o barco do adversario
                case "C" -> ProcessaAcerto(bomba);
                //sinal que recebe a bomba posicionada pelo adversario
                case "D" -> ProcessaAcertoAdv();
                //sinal de vitoria
                case "E" -> Vitoria();
            }
        }while (!in.readUTF().equals("bye"));
        FechaConexao();
    }

    private void ChecaConexao() throws IOException {
        //apos sinal de conexao, ve se foi estabelecida a conexao dos dois clientes
        if(in.readBoolean()){
            System.out.println("Conexao estabelecida");
            //se a conexao foi estabelecida, inicia o jogo com o posicionamento dos barcos
            posicaoBarcos = jogo.InicioJogo();
            //envia barcos posicionados para o server
            EnviaBarcos();
        }
        else{
            System.out.println("Falha na conexao");
        }
    }
    private void EnviaBomba(int[] bombinha) throws IOException {
        out.writeInt(bombinha[0]);
        out.writeInt(bombinha[1]);
    }

    private int[] ProcessaVez() throws IOException {
        int[] bomba = new int[2];
        //verifica se é a vez do jogador ou do adversario
        if(in.readBoolean()){
            bomba = jogo.MinhaVez();
            EnviaBomba(bomba);
        } else{
            jogo.VezDoAdv();
        }
        return bomba;
    }
    private void ProcessaAcerto(int[] bomba) throws IOException {
        //verifica se o o jogador acertou e recebe a sua pontuação no momento
        jogo.ProcessaBomba(bomba, in.readBoolean());
        jogo.ControlaScore(in.readInt());
    }
    public void EnviaBarcos() throws IOException {
        //envia coordenadas dos barcos posicionados
        out.writeInt(posicaoBarcos.length);  // Número de linhas
        out.writeInt(posicaoBarcos[0].length);  // Número de colunas

        for (int[] linha : posicaoBarcos) {
            for (int valor : linha) {
                out.writeInt(valor);  // Enviando cada valor da matriz
            }
        }
    }
    private void ProcessaAcertoAdv() throws IOException {
        int[] bombaAdv = new int[2];
        //processa bomba do adversario
        bombaAdv[0] = in.readInt();
        bombaAdv[1] = in.readInt();
        jogo.ProcessaBombardeio(bombaAdv);
    }
    private void Vitoria() throws IOException {
        //indica se o jogador ganhou
        jogo.FimDeJogo(in.readBoolean());
    }
    private void FechaConexao() throws IOException {
        jogo.FechaTudo();
        in.close();
        out.close();
        cliente.close();
    }
}
