import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Main {
    private Socket cliente1;
    private Socket cliente2;
    private DataInputStream in1;
    private DataInputStream in2;
    private DataOutputStream out1;
    private DataOutputStream out2;
    private ArrayList<Integer> BarcosC1;
    private ArrayList<Integer> BarcosC2;
    public final ArrayList<Integer> TdsBarcos = new ArrayList<>();
    private int score1;
    private int score2;

    public void connect() throws IOException {
        try {
            ServerSocket server = new ServerSocket(0);
            System.out.println("Servidor iniciado. Aguardando conexão com Cliente 1");
            cliente1 = server.accept();
            System.out.println("Aguardando conexão com Cliente 2");
            cliente2 = server.accept();

            out1 = new DataOutputStream(cliente1.getOutputStream());
            out1.writeUTF("A");
            out1.writeBoolean(true);

            out2 = new DataOutputStream(cliente2.getOutputStream());
            out2.writeUTF("A");
            out2.writeBoolean(true);

            in1 = new DataInputStream(cliente1.getInputStream());
            BarcosC1 = recebeBarcos(in1);

            in2 = new DataInputStream(cliente2.getInputStream());
            BarcosC2 = recebeBarcos(in2);

        } catch (Exception e) {
            System.out.println("Erro encontrado: " + e.getMessage());
        }
    }

    public ArrayList<Integer> recebeBarcos(DataInputStream in) throws IOException {
        int c1Linhas = in.readInt();
        int c1Colunas = in.readInt();

        int[][] c1Matriz = new int[c1Linhas][c1Colunas];
        for (int i = 0; i < c1Linhas; i++) {
            for (int j = 0; j < c1Colunas; j++) {
                c1Matriz[i][j] = in.readInt();
            }
        }

        ArrayList<Integer> listaBarco = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int cte, vf, v0;

        for (int i = 0; i < c1Linhas; i++) {
            for (int j = 0; j < (c1Colunas - 3); j++) {
                sb.setLength(0);
                if (c1Matriz[i][j] == c1Matriz[i][j + 2]) {
                    cte = c1Matriz[i][j];
                    if (c1Matriz[i][j + 1] > c1Matriz[i][j + 3]) {
                        vf = c1Matriz[i][j + 1];
                        v0 = c1Matriz[i][j + 3];
                    } else {
                        vf = c1Matriz[i][j + 3];
                        v0 = c1Matriz[i][j + 1];
                    }
                    for (; v0 <= vf; v0++) {
                        sb.append(cte).append(v0);
                        int bloco = Integer.parseInt(sb.toString());
                        listaBarco.add(bloco);
                    }
                } else {
                    cte = c1Matriz[i][j + 1];
                    if (c1Matriz[i][j] > c1Matriz[i][j + 2]) {
                        vf = c1Matriz[i][j];
                        v0 = c1Matriz[i][j + 2];
                    } else {
                        vf = c1Matriz[i][j + 2];
                        v0 = c1Matriz[i][j];
                    }
                    for (; v0 <= vf; v0++) {
                        sb.append(v0).append(cte);
                        int bloco = Integer.parseInt(sb.toString());
                        listaBarco.add(bloco);
                    }
                }
            }
        }
        TdsBarcos.addAll(listaBarco);
        return listaBarco;
    }

    public boolean verificacaoBombas(ArrayList<Integer> barcos, int bomba) {
        return barcos.contains(bomba);
    }

    public void jogo() throws IOException {
        boolean vezDoUm = true;

        while (score1 < TdsBarcos.size() && score2 < TdsBarcos.size()) {
            if (vezDoUm) {
                out1.writeUTF("B");
                out1.writeBoolean(true);
                out2.writeUTF("B");
                out2.writeBoolean(false);

                int bomba = in1.readInt();
                boolean acertou = verificacaoBombas(BarcosC2, bomba);
                if (acertou) {
                    score1++;
                }

                out1.writeUTF("C");
                out1.writeInt(bomba);
                out1.writeInt(score1);
                out1.writeInt(score2);

                out2.writeUTF("D");
                out2.writeInt(bomba);
            } else {
                out2.writeUTF("B");
                out2.writeBoolean(true);
                out1.writeUTF("B");
                out1.writeBoolean(false);

                int bomba = in2.readInt();
                boolean acertou = verificacaoBombas(BarcosC1, bomba);
                if (acertou) {
                    score2++;
                }

                out2.writeUTF("C");
                out2.writeInt(bomba);
                out2.writeInt(score2);
                out2.writeInt(score1);

                out1.writeUTF("D");
                out1.writeInt(bomba);
            }

            vezDoUm = !vezDoUm;
        }

        if (score1 == TdsBarcos.size()) {
            out1.writeUTF("E");
            out1.writeBoolean(true);
            out2.writeUTF("E");
            out2.writeBoolean(false);
            System.out.println("Jogador 1 venceu!");
        } else if (score2 == TdsBarcos.size()) {
            out1.writeUTF("E");
            out1.writeBoolean(false);
            out2.writeUTF("E");
            out2.writeBoolean(true);
            System.out.println("Jogador 2 venceu!");
        }

        out1.close();
        out2.close();
        in1.close();
        in2.close();
    }

    public static void main(String[] args) throws IOException {
        Main server = new Main();
        server.connect();
        server.jogo();
    }
}
