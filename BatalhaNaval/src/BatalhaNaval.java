import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BatalhaNaval extends JFrame{
    JButton[][] CamposBatalha = new JButton[10][10];
    JButton[][] meuCampo = new JButton[10][10];
    int[][] posicaoBarcos = new int[5][4];
    JPanel quadrado = new JPanel();
    JLabel pontos = new JLabel("Pontuação: 0");
    JLabel textocentral = new JLabel();
    boolean prontoPraEnviar = false;
    public BatalhaNaval(){
        //construtor que inicia a interface grafica do jogo
        setVisible(true);
        setTitle("Batalha Naval");
        setDefaultCloseOperation(3);
        setLayout(null);
        setBounds(0,0,1650,850);
    }

    public static void main(String[] args) throws IOException {
        //instanciando objeto do tipo cliente para iniciar a conexao
        Cliente c = new Cliente();
        c.Comunicacao();
    }
    private void printaBarcos(){
        for(int i=0; i<5; i++){
            for(int j=0; j<4; j++){
                System.out.println(posicaoBarcos[i][j]);
            }
        }
    }

    public int[][] InicioJogo(){
        QuadradinhoCentral("Bote seus Barcos");
        //chamado de funcao que permite que o usuario posicione seus barcos
        AcionaHover(0);
        printaBarcos();
        while(!prontoPraEnviar){
            System.out.println("aguardando");
        }
        //retorna o local em que foram posicionados barcos para enviar ao servidor
        return posicaoBarcos;
    }

    public void CriaCampos(){
        //criacao do campo do jogador
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
        //criacao do campo que corresponde ao do oponente
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
        //criacao de um quadro central para passar informacoes ao jogador
        quadrado.setBounds(715, 370,95,95);
        quadrado.setLayout(new GridBagLayout());
        quadrado.setBackground(Color.PINK);
        quadrado.setVisible(true);
        add(quadrado);
        QuadradinhoCentral("Inicio do jogo :)!");
        pontos.setBounds(1400, 5, 100, 25); // x, y, largura, altura
        add(pontos);
        textocentral.setHorizontalAlignment(SwingConstants.CENTER);
        textocentral.setVerticalAlignment(SwingConstants.CENTER);
        quadrado.add(textocentral);
    }

    public void ControlaScore(int score){
        pontos.setText("Pontuação: " + String.valueOf(score));
    }

    public void paint(Graphics g) {
        //implementa linhas no centro da tela para dividir os mapas
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        Line2D lin = new Line2D.Float(770, 0, 770, 400);
        Line2D lin2 = new Line2D.Float(770, 495, 770, 845);
        g2.draw(lin);
        g2.draw(lin2);
    }

    private void QuadradinhoCentral(String dito){
        //modifica o texto central mostrado ao usuario
        textocentral.setText(dito);
        ajustarTamanhoFonte(textocentral, 95);
    }
    public static void ajustarTamanhoFonte(JLabel label, int larguraMaxima) {
        Font fonte = label.getFont();
        String texto = label.getText();

        // Obtém o componente de medição de fonte
        FontMetrics metrics = label.getFontMetrics(fonte);
        int larguraTexto = metrics.stringWidth(texto);

        // Diminui a fonte até o texto caber na largura especificada
        while (larguraTexto > larguraMaxima) {
            fonte = fonte.deriveFont((float) fonte.getSize() - 1);
            metrics = label.getFontMetrics(fonte);
            larguraTexto = metrics.stringWidth(texto);
        }

        // Define a fonte ajustada para o JLabel
        label.setFont(fonte);
    }

    private void AcionaHover(int contaCliques) {
        //instrucoes quanto a rotacao no posicionamento dos barcos
        JLabel instrucao = new JLabel("Aperte 'R' para mudar a orientação do barco");
        instrucao.setBounds(15, 750, 700, 25); // x, y, largura, altura
        add(instrucao);
        //tamanhos dos barcos padrao de batalha naval
        int[] tamanhobarcos = {5, 4, 3, 3, 2};
        //condicional que finaliza a recursiviade da funcao, indicando o fim do posicionamento
        if (contaCliques >= tamanhobarcos.length) {
            remove(instrucao);
            printaBarcos();
            prontoPraEnviar = true;
            return;}
        int size = tamanhobarcos[contaCliques];
        //variavel que controla a orientação do barco que o jogador quer posicionar
        final boolean[] horizontal = {true};
        //imagens utilizadas
        ImageIcon emptyIcon = new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        Image img = null;
        try {
            img = ImageIO.read(Objects.requireNonNull(getClass().getResource("barco.jpg")));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Icon imagemBarco = new ImageIcon(img);
        //implementa a rotacao do barco ao pressionar a tecla R
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    horizontal[0] = !horizontal[0];
                }
            }
        });
        requestFocusInWindow();
        //criacao que variaveis que permitem a interacao com os botoes
        MouseAdapter[][] mouseAdapters = new MouseAdapter[10][10];
        ActionListener[][] actionListeners = new ActionListener[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int finalI = i;
                int finalJ = j;
                //funcao que permite o efeito de hover sobre os botoes
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
                //funcao que permite o posicionamento dos barcos no campo escolhido
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
                        // remove os MouseListeners
                        for (int h = 0; h < 10; h++) {
                            for (int g = 0; g < 10; g++) {
                                meuCampo[h][g].removeMouseListener(mouseAdapters[h][g]);
                                meuCampo[h][g].removeActionListener(actionListeners[h][g]);
                            }
                        }
                        posicaoBarcos[contaCliques] = barcos;
                        //chamada recursiva da funcao indicando a quantidade de barcos posicionados
                        AcionaHover(contaCliques + 1);
                    }
                };
                
                //remove os ActionListeners
                if (((horizontal[0] && (finalI + size-1) <= 10)||(!horizontal[0] && (finalJ + size-1) <= 10))&&meuCampo[i][j].isEnabled()) {
                    meuCampo[i][j].addActionListener(actionListeners[i][j]);
                }
            }
        }
    }


    public int[] MinhaVez() {
        System.out.println("MINHA VEZ");
        QuadradinhoCentral("Sua vez");

        int[] botaoClicado = new int[2];
        CountDownLatch latch = new CountDownLatch(1);

        ActionListener[][] actionListeners = new ActionListener[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int finalI = i;
                int finalJ = j;
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
                        for (int h = 0; h < 10; h++) {
                            for (int g = 0; g < 10; g++) {
                                if (finalI != h || finalJ != g) {
                                    CamposBatalha[h][g].setEnabled(false);
                                }
                                CamposBatalha[h][g].removeActionListener(actionListeners[h][g]);
                            }
                        }
                        latch.countDown(); // Libera a espera
                    }
                };
                if (CamposBatalha[i][j].isEnabled()) {
                    CamposBatalha[i][j].addActionListener(actionListeners[i][j]);
                }
            }
        }

        try {
            latch.await(); // Espera até que o botão seja clicado
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return botaoClicado;
    }

    public void ProcessaBomba(int[] bomba, boolean acerto){
        //desativa o local da bomba e ativa os outros
        CamposBatalha[bomba[0]][bomba[1]].setEnabled(false);
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                if(i!=bomba[0]||j!=bomba[1]) {
                    CamposBatalha[i][j].setEnabled(true);
                }
            }
        }
        //verifica se o jogador acertou a bomba
        if(acerto){
            QuadradinhoCentral("Acertou!!!");
        }
        else{
            QuadradinhoCentral("Errou:(");
            //adiciona imagem de x se errou
            Image img = null;
            try {
                img = ImageIO.read(Objects.requireNonNull(getClass().getResource("xis.jpg")));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            CamposBatalha[bomba[0]][bomba[1]].setDisabledIcon(new ImageIcon(img));
            CamposBatalha[bomba[0]][bomba[1]].setIcon(new ImageIcon(img));
        }
        esperar(3000);
    }

    public void VezDoAdv(){
        QuadradinhoCentral("Vez do Adversário");
    }

    public void ProcessaBombardeio(int[] bombaAdv){
        String foto;
        //processa o bombardeio que o jogador recebeu do adversario e coloca uma imagem correspondente ao acerto
        if(meuCampo[bombaAdv[0]][bombaAdv[1]].isEnabled()){
            meuCampo[bombaAdv[0]][bombaAdv[1]].setEnabled(false);
            foto = "xis.jpg";
        }else{
            foto = "explosao.jpg";
        }
        Image img = null;
        try {
            img = ImageIO.read(Objects.requireNonNull(getClass().getResource(foto)));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        meuCampo[bombaAdv[0]][bombaAdv[1]].setDisabledIcon(new ImageIcon(img));
    }

    public void FimDeJogo(boolean venceu){
        //indica se o jogador ganhou ou perdeu no fim do jogo
        if(venceu){
            QuadradinhoCentral("Você venceu!!!");
        }else{
            QuadradinhoCentral("Você perdeu :(");
        }
        esperar(5000);
    }
    public void esperar(int mili) {
        // Cria um CountDownLatch que vai "travar" até ser liberado
        CountDownLatch latch = new CountDownLatch(1);

        // Cria um Timer que liberará o latch após o tempo especificado
        Timer timer = new Timer(mili, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                latch.countDown(); // Libera o CountDownLatch
                ((Timer) e.getSource()).stop(); // Para o timer após disparar
            }
        });

        timer.setRepeats(false); // Define para rodar apenas uma vez
        timer.start(); // Inicia o Timer

        try {
            // Faz o programa "esperar" até o latch ser liberado pelo Timer
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void FechaTudo(){
        // Limpando todos os componentes do JFrame
        getContentPane().removeAll();
        repaint();
        //indica que o o jogador feche o jogo
        JLabel tchau = new JLabel("Fim de jogo, feche a página");
        tchau.setBounds(100, 400, 1000, 50);
        add(tchau);
    }
}