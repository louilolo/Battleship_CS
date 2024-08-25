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
    private DataInputStream in;
    private DataOutputStream out;
    BatalhaNaval jogo = new BatalhaNaval();
    int[][] posicaoBarcos = new int[5][4];

    public Cliente() throws IOException{
        Socket cliente = new Socket(HOST, PORT);
        DataInputStream in = new DataInputStream(cliente.getInputStream());
        DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
    }

    public void Comunicacao() throws IOException {
        int[] bomba = new int[2];
        String leitura = in.readUTF();
        do{
            switch (leitura) {
                case "A" -> ChecaConexao();
                case "B" -> bomba = ProcessaVez();
                case "C" -> ProcessaAcerto(bomba);
                case "D" -> ProcessaAcertoAdv();
                case "E" -> Vitoria();
            }
        }while (!in.readUTF().equals("bye"));
        FechaConexao();
    }

    private void ChecaConexao() throws IOException {
        if(in.readBoolean()){
            System.out.println("Conexao estabelecida");
            posicaoBarcos = jogo.InicioJogo();
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
        if(in.readBoolean()){
            bomba = jogo.MinhaVez();
            EnviaBomba(bomba);
        } else{
            jogo.VezDoAdv();
        }
        return bomba;
    }
    private void ProcessaAcerto(int[] bomba) throws IOException {
        jogo.ProcessaBomba(bomba, in.readBoolean());
        jogo.ControlaScore(in.readInt());
    }
    public void EnviaBarcos() throws IOException {
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
        bombaAdv[0] = in.readInt();
        bombaAdv[1] = in.readInt();
        jogo.ProcessaBombardeio(bombaAdv);
    }
    private void Vitoria() throws IOException {
        jogo.FimDeJogo(in.readBoolean());
    }
    private void FechaConexao() throws IOException {
        jogo.FechaTudo();
        in.close();
        out.close();
        cliente.close();
    }
}
