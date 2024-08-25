import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BatalhaNaval extends JFrame{
    JButton[][] CamposBatalha = new JButton[10][10];
    JButton[][] meuCampo = new JButton[10][10];
    int[][] posicaoBarcos = new int[5][4];
    JPanel quadrado = new JPanel();
    JLabel pontos = new JLabel("Pontuação: 0");
    public BatalhaNaval(){
        setVisible(true);
        setTitle("Batalha Naval");
        setDefaultCloseOperation(3);
        setLayout(null);
        setBounds(0,0,1650,850);
    }

    public static void main(String[] args) throws IOException {
        BatalhaNaval j = new BatalhaNaval();
        j.CriaCampos();
        Cliente c = new Cliente();
        c.Comunicacao();
        /*int[] bomba = j.MinhaVez();
        j.ControlaScore(1);
        j.AcionaHover(0);
        //Cliente c = new Cliente();
        //c.EnviaBomba(bomba);
        //c.FechaConexao();
        j.ProcessaBomba(bomba, true);
        j.QuadradinhoCentral("oi");*/
    }
    private void printaBarcos(){
        for(int i=0; i<5; i++){
            for(int j=0; j<4; j++){
                System.out.println(posicaoBarcos[i][j]);
            }
        }
    }

    public int[][] InicioJogo(){
        QuadradinhoCentral("Posicione os seus Barcos");
        AcionaHover(0);
        return posicaoBarcos;
    }

    private void CriaCampos(){
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                meuCampo[i][j] = new JButton();
                add(meuCampo[i][j]);
                meuCampo[i][j].setBounds((70 * i)+15, (70 * j)+40, 65, 65);
            }
        }
        JLabel texto1 = new JLabel("Meu mapa");
        texto1.setBounds(15, 5, 100, 25); // x, y, largura, altura
        add(texto1);
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                CamposBatalha[i][j] = new JButton();
                add(CamposBatalha[i][j]);
                CamposBatalha[i][j].setBounds((70 * i)+815, (70 * j)+40, 65, 65);
            }
        }
        JLabel texto2 = new JLabel("Mapa inimigo");
        texto2.setBounds(815, 5, 100, 25); // x, y, largura, altura
        add(texto2);
        quadrado.setBounds(715, 370,95,95);
        quadrado.setBackground(Color.blue);
        quadrado.setVisible(true);
        add(quadrado);
        QuadradinhoCentral("Inicio do jogo!");
        pontos.setBounds(1400, 5, 100, 25); // x, y, largura, altura
        add(pontos);
    }

    public void ControlaScore(int score){
        pontos.setText("Pontuação: " + String.valueOf(score));
    }

    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g;
        Line2D lin = new Line2D.Float(770, 0, 770, 400);
        Line2D lin2 = new Line2D.Float(770, 495, 770, 845);
        g2.draw(lin);
        g2.draw(lin2);
    }

    private void QuadradinhoCentral(String dito){
        JLabel textocentral = new JLabel();
        textocentral.setText(dito);
        quadrado.add(textocentral);
    }

    private void AcionaHover(int contaCliques) {
        JLabel instrucao = new JLabel("Aperte 'R' para mudar a orientação do barco");
        instrucao.setBounds(15, 750, 700, 25); // x, y, largura, altura
        add(instrucao);
        int[] tamanhobarcos = {5, 4, 3, 3, 2};
        if (contaCliques >= tamanhobarcos.length) {
            remove(instrucao);
            printaBarcos();
            return;}
        int size = tamanhobarcos[contaCliques];
        ImageIcon emptyIcon = new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        final boolean[] horizontal = {true};
        Image img = null;
        try {
            img = ImageIO.read(Objects.requireNonNull(getClass().getResource("barco.jpeg")));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Icon imagemBarco = new ImageIcon(img);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    horizontal[0] = !horizontal[0];
                }
            }
        });

        requestFocusInWindow();

        MouseAdapter[][] mouseAdapters = new MouseAdapter[10][10];
        ActionListener[][] actionListeners = new ActionListener[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int finalI = i;
                int finalJ = j;

                mouseAdapters[i][j] = new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        for (int pos = 0; pos < size; pos++) {
                            if (horizontal[0] && finalI + size <= 10) {
                                meuCampo[finalI + pos][finalJ].setIcon(imagemBarco);
                            } else if (!horizontal[0] && finalJ + size <= 10) {
                                meuCampo[finalI][finalJ + pos].setIcon(imagemBarco);
                            }
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        for (int h = 0; h < 10; h++) {
                            for (int g = 0; g < 10; g++) {
                                if(meuCampo[h][g].isEnabled()) {
                                    meuCampo[h][g].setIcon(emptyIcon);
                                }
                            }
                        }
                    }
                };
                if(meuCampo[i][j].isEnabled()) {
                    meuCampo[i][j].addMouseListener(mouseAdapters[i][j]);
                }
                actionListeners[i][j] = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int[] barcos = {finalI, finalJ, finalI, finalJ};
                        for (int pos = 0; pos < size; pos++) {
                            if (horizontal[0] && finalI + size <= 10) {
                                barcos[2] = finalI+size-1;
                                meuCampo[finalI + pos][finalJ].setEnabled(false);
                                meuCampo[finalI + pos][finalJ].setDisabledIcon(imagemBarco);
                            } else if (!horizontal[0] && finalJ + size <= 10) {
                                barcos[3] = finalJ+size-1;
                                meuCampo[finalI][finalJ + pos].setEnabled(false);
                                meuCampo[finalI][finalJ + pos].setDisabledIcon(imagemBarco);
                            }
                        }
                        // Remover os MouseListeners
                        for (int h = 0; h < 10; h++) {
                            for (int g = 0; g < 10; g++) {
                                meuCampo[h][g].removeMouseListener(mouseAdapters[h][g]);
                                meuCampo[h][g].removeActionListener(actionListeners[h][g]);
                            }
                        }
                        posicaoBarcos[contaCliques] = barcos;
                        AcionaHover(contaCliques + 1);
                    }
                };
                if (((horizontal[0] && finalI + size <= 10)||(!horizontal[0] && finalJ + size <= 10))&&meuCampo[i][j].isEnabled()) {
                    meuCampo[i][j].addActionListener(actionListeners[i][j]);
                }
            }
        }
    }

    public int[] MinhaVez() {
        QuadradinhoCentral("Sua vez");
        int[] botaoClicado = new int[2];
        ActionListener[][] actionListeners = new ActionListener[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int finalJ = j;
                int finalI = i;
                actionListeners[i][j] = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        botaoClicado[0] = finalI;
                        botaoClicado[1] = finalJ;
                        Image img = null;
                        try {
                            img = ImageIO.read(Objects.requireNonNull(getClass().getResource("bomba.png")));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        CamposBatalha[finalI][finalJ].setIcon(new ImageIcon(img));
                        CamposBatalha[finalI][finalJ].setDisabledIcon(new ImageIcon(img));
                        for(int h=0; h<10; h++){
                            for(int g=0; g<10; g++){
                                if(finalI!=h || finalJ!=g) {
                                    CamposBatalha[h][g].setEnabled(false);
                                }
                                CamposBatalha[h][g].removeActionListener(actionListeners[h][g]);
                            }
                        }
                    }
                };
                if(CamposBatalha[i][j].isEnabled()) {
                    CamposBatalha[i][j].addActionListener(actionListeners[i][j]);
                }
            }
        }
        return botaoClicado;
    }
    public void ProcessaBomba(int[] bomba, boolean acerto){
        CamposBatalha[bomba[0]][bomba[1]].setEnabled(false);
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                if(i!=bomba[0]&&j!=bomba[1]) {
                    CamposBatalha[i][j].setEnabled(true);
                }
            }
        }
        if(acerto){
            QuadradinhoCentral("Acertou!!!");
        }
        else{
            QuadradinhoCentral("Errou:(");
            Image img = null;
            try {
                img = ImageIO.read(Objects.requireNonNull(getClass().getResource("xis.png")));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            CamposBatalha[bomba[0]][bomba[1]].setDisabledIcon(new ImageIcon(img));
        }
    }

    public void VezDoAdv(){
        QuadradinhoCentral("Vez do Adversário");
    }

    public void ProcessaBombardeio(int[] bombaAdv){
        String foto;
        if(meuCampo[bombaAdv[0]][bombaAdv[1]].isEnabled()){
            meuCampo[bombaAdv[0]][bombaAdv[1]].setEnabled(false);
            foto = "xis.png";
        }else{
            foto = "bomba.png";
        }
        Image img = null;
        try {
            img = ImageIO.read(Objects.requireNonNull(getClass().getResource(foto)));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        CamposBatalha[bombaAdv[0]][bombaAdv[1]].setDisabledIcon(new ImageIcon(img));
    }

    public void FimDeJogo(boolean venceu){
        if(venceu){
            QuadradinhoCentral("Você venceu!!!");
        }else{
            QuadradinhoCentral("Você perdeu :(");
        }
        try {
            Thread.sleep(2000); // Espera 2 segundos antes de limpar
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void FechaTudo(){
        // Limpando todos os componentes do JFrame
        getContentPane().removeAll();
        repaint();
        JLabel tchau = new JLabel("Fim de jogo, feche a página");
        tchau.setBounds(100, 400, 1000, 50);
        add(tchau);
    }
}