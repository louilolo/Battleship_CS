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
    private int score1 = 0;
    private int score2 = 0;

    public void connect() throws IOException {
        try {
            ServerSocket server = new ServerSocket(44444);
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

            in2 = new DataInputStream(cliente2.getInputStream());

            while (!in1.readBoolean()){
                System.out.println("esperando barcos jogador 1");
            }
            BarcosC1 = recebeBarcos(in1);
            while (!in2.readBoolean()){
                System.out.println("esperando barcos jogador 2");
            }
            BarcosC2 = recebeBarcos(in2);

            out1.writeBoolean(true);
            out2.writeBoolean(true);

            System.out.println("Barcos do Cliente 1: " + BarcosC1.size());
            System.out.println("Barcos do Cliente 2: " + BarcosC2.size());

        } catch (Exception e) {
            System.out.println("Erro encontrado: " + e.getMessage());
        }
    }

    public ArrayList<Integer> recebeBarcos(DataInputStream in) throws IOException {
        int linhas = 5;
        int colunas = 4;

        int[][] matriz = new int[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                matriz[i][j] = in.readInt();
                System.out.println(matriz[i][j]);
            }
        }

        ArrayList<Integer> listaBarco = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int cte, vf, v0;

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < (colunas - 3); j++) {
                sb.setLength(0);
                if (matriz[i][j] == matriz[i][j + 2]) {
                    cte = matriz[i][j];
                    if (matriz[i][j + 1] > matriz[i][j + 3]) {
                        vf = matriz[i][j + 1];
                        v0 = matriz[i][j + 3];
                    } else {
                        vf = matriz[i][j + 3];
                        v0 = matriz[i][j + 1];
                    }
                    for (; v0 <= vf; v0++) {
                        sb.append(cte).append(v0);
                        int bloco = Integer.parseInt(sb.toString());
                        listaBarco.add(bloco);
                    }
                } else {
                    cte = matriz[i][j + 1];
                    if (matriz[i][j] > matriz[i][j + 2]) {
                        vf = matriz[i][j];
                        v0 = matriz[i][j + 2];
                    } else {
                        vf = matriz[i][j + 2];
                        v0 = matriz[i][j];
                    }
                    for (; v0 <= vf; v0++) {
                        sb.append(v0).append(cte);
                        int bloco = Integer.parseInt(sb.toString());
                        listaBarco.add(bloco);
                    }
                }
            }
        }
        return listaBarco;
    }

    public boolean verificacaoBombas(ArrayList<Integer> barcos, int bomba) {
        return barcos.contains(bomba);
    }

    public void jogo() throws IOException {
        boolean vezDoUm = true;

        while (score1 < BarcosC2.size() && score2 < BarcosC1.size()) {
            if (vezDoUm) {
                out1.writeUTF("B");
                out1.writeBoolean(true);
                out2.writeUTF("B");
                out2.writeBoolean(false);

                int bomba = in1.readInt();
                boolean acertou = verificacaoBombas(BarcosC2, bomba);
                if (acertou) {
                    score1++;
                    System.out.println("Jogador 1 acertou! Score1: " + score1);
                } else {
                    System.out.println("Jogador 1 errou.");
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
                    System.out.println("Jogador 2 acertou! Score2: " + score2);
                } else {
                    System.out.println("Jogador 2 errou.");
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

        if (score1 == BarcosC2.size()) {
            out1.writeUTF("E");
            out1.writeBoolean(true);
            out2.writeUTF("E");
            out2.writeBoolean(false);
            System.out.println("Jogador 1 venceu!");
        } else if (score2 == BarcosC1.size()) {
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