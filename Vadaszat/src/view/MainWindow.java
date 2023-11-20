package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * A játék főablaka, ahol a játékteret megjelenítjük.
 * 
 * @author Horicsányi Máté
 */
public class MainWindow {
    private JFrame frame;       // A JFrame objektum az ablakhoz
    private JPanel buttonPanel; // A gombpanelelem az ablakhoz
    private JLabel turnLabel;   // A játékost jelző címke
    private JLabel stepsLabel;  // A hátralévő lépéseket jelző címke
    private JButton[][] buttons;// A játék gombjainak mátrixa
    private boolean isRedPlayerTurn = true; // Jelzi, hogy a piros játékos következik-e
    private int steps;          // A hátralévő lépések száma
    private int phase;           // adott kör fázisa

    /**
     * A MainWindow osztály konstruktora, inicializálja az ablakot és a vezérlőket.
     *
     * @throws IOException IO hiba kezelése
     */
    public MainWindow() throws IOException {
        // Ablak inicializálása
        frame = new JFrame("Vadaszat");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menüsáv inicializálása
        JMenuBar menuBar = new JMenuBar();
        
        // Új játék menü inicializálása
        JMenu newGameMenu = new JMenu("Új játék");
        JMenuItem newGame3x3 = new JMenuItem("3x3");
        JMenuItem newGame5x5 = new JMenuItem("5x5");
        JMenuItem newGame7x7 = new JMenuItem("7x7");
        newGame3x3.addActionListener((ActionEvent e) -> {
            createButtons(3);
        });
        newGame5x5.addActionListener((ActionEvent e) -> {
            createButtons(5);
        });
        newGame7x7.addActionListener((ActionEvent e) -> {
            createButtons(7);
        });

        newGameMenu.add(newGame3x3);
        newGameMenu.add(newGame5x5);
        newGameMenu.add(newGame7x7);
        menuBar.add(newGameMenu);
        
        // Lépésszám címke inicializálása
        stepsLabel = new JLabel("Lépések száma:");
        menuBar.add(stepsLabel);
        
        // Játékosváltás címke inicializálása
        turnLabel = new JLabel();
        menuBar.add(turnLabel);
        
        // Menüsáv beállítása
        frame.setJMenuBar(menuBar);
        
        // Gombpanel inicializálása
        buttonPanel = new JPanel();
        frame.add(buttonPanel);

        frame.setVisible(true);
    }

    /**
     * Új gombok létrehozása a megadott méret alapján.
     *
     * @param size Az új játéktér mérete size*size méretű lesz
     */
    private void createButtons(int size) {
        // Gombpanel törlése és új méret beállítása
        buttonPanel.removeAll();
        buttonPanel.setLayout(new GridLayout(size, size));
        
        // Piros játékos kezd
        isRedPlayerTurn = true;
        turnLabel.setText("  Piros játékos köre ");
        turnLabel.setForeground(Color.RED);
        
        // Lépések számának beállítása
        steps = 4 * size;
        stepsLabel.setText(" Hátralévő lépések száma: " + steps);

        // Fázis számának nullázása
        phase = 0;
        
        // Gombok mátrixának létrehozása
        buttons = new JButton[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JButton button = new JButton();         
                button.addActionListener((ActionEvent e) -> {
                    refreshButtons(e);
                });
                button.setEnabled(false);
                button.setBackground(Color.LIGHT_GRAY);
                if ((i == 0 && (j == 0 || j == size - 1)) || (i == size - 1 && (j == 0 || j == size - 1))) {
                    button.setBackground(Color.BLUE);
                } else if (i == size / 2 && j == size / 2) {
                    button.setBackground(Color.RED);
                } 
                buttons[i][j] = button;           
            }
        }
        // Megfelelő gombok engedélyezése
        buttons = enableCorrectButtons(buttons, size,phase);
        
        // Gombok hozzáadása a panelhez
        for(JButton[] row : buttons){
            for(JButton button : row){
                buttonPanel.add(button);
            }
        }
        
        // Ablak frissítése
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Gombok frissítése a felhasználói interakció után.
     *
     * @param e Az esemény, amely kiváltotta a gombnyomást
     */
    private void refreshButtons(ActionEvent e){
        JButton clickedButton = (JButton) e.getSource();        
        int clickedRow = -1, clickedCol = -1;
        
        // A kattintott gomb pozíciójának meghatározása
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (buttons[i][j].getBackground() != Color.BLUE){
                    if(phase == 1){
                        buttons[i][j].setBackground(Color.LIGHT_GRAY);
                    }
                }
                if (buttons[i][j] == clickedButton) {
                    clickedRow = i;
                    clickedCol = j;
                }
            }
        }
        
        // Attól függöen milyen lépés történt (milyen állás van), akciók végrehajtása
        if(phase == 1){//piros lépett
            buttons[clickedRow][clickedCol].setBackground(Color.RED);
            turnLabel.setText("  Kék játékos karaktert választ ");
            turnLabel.setForeground(Color.BLUE);
            buttons = enableCorrectButtons(buttons, buttons.length,phase);
        }else if(phase == 2){//kék választott
            if (isAdjacentToColor(buttons,clickedRow,clickedCol,Color.LIGHT_GRAY)) {
                buttons[clickedRow][clickedCol].setBackground(Color.BLACK);
                turnLabel.setText("  Kék játékos köre");
                turnLabel.setForeground(Color.BLUE);
                buttons = enableCorrectButtons(buttons, buttons.length,phase);
            }
        }else if(phase == 0){//kék lépett
            buttons[clickedRow][clickedCol].setBackground(Color.BLUE);
            turnLabel.setText("  Piros játékos köre ");
            turnLabel.setForeground(Color.RED);
            buttons = enableCorrectButtons(buttons, buttons.length,phase);
            steps--;
            stepsLabel.setText(" Hátralévő lépések száma: " + steps);
        }
        
        // Játék vége ellenőrzése, piros nyert-e
        if (steps == 0) {
            JOptionPane.showMessageDialog(frame, "Piros játékos nyert!", "Játék vége", JOptionPane.INFORMATION_MESSAGE);
            createButtons(buttons.length);
        }
    }
    
    /**
     * A megfelelő gombok engedélyezése az adott körben.
     *
     * @param b A gombok mátrixa
     * @param s Az aktuális méret
     * @param f Az aktuális fázis száma
     * @return A frissített gombok mátrixa
     */
    private JButton[][] enableCorrectButtons(JButton[][] b, int s,int f) {
        if(phase == 0){//kék lépett
            boolean redPlayerCanMove = false;
            for (int i = 0; i < s; i++){
               for (int j = 0; j< s; j++){
                   if (buttons[i][j].getBackground()!=Color.RED && buttons[i][j].getBackground()!=Color.BLUE ){
                       buttons[i][j].setBackground(Color.LIGHT_GRAY);
                   }
                   buttons[i][j].setEnabled(false);
                   if (buttons[i][j].getBackground() == Color.LIGHT_GRAY) {
                       if (isAdjacentToColor(buttons,i,j,Color.RED)) {
                          buttons[i][j].setBackground(null);
                          buttons[i][j].setEnabled(true);
                          redPlayerCanMove = true;
                       }
                   }
               }
           }
           phase++;
           
           // Játék vége ellenőrzése, kék nyert-e
            if (!redPlayerCanMove) {
                JOptionPane.showMessageDialog(frame, "Kék játékos nyert!", "Játék vége", JOptionPane.INFORMATION_MESSAGE);

            }
        }else if(phase == 1){//piros lépett
           for (int i = 0; i < s; i++){
                for (int j = 0; j< s; j++){
                    if(buttons[i][j].getBackground() == Color.BLUE){ 
                        buttons[i][j].setBackground(Color.CYAN);
                        buttons[i][j].setEnabled(true);
                    }else{
                        buttons[i][j].setEnabled(false);
                    }
                }
            }
           phase++;
        }else if(phase == 2){//kék választott
            for (int i = 0; i < s; i++){
                for (int j = 0; j< s; j++){
                    buttons[i][j].setEnabled(false);
                    if(buttons[i][j].getBackground() == Color.CYAN){              
                        buttons[i][j].setBackground(Color.BLUE);
                    }
                    if (buttons[i][j].getBackground() == Color.LIGHT_GRAY) {
                        if (isAdjacentToColor(buttons,i,j,Color.BLACK)) {
                           buttons[i][j].setBackground(null);
                           buttons[i][j].setEnabled(true);
                        }
                    }
                }
            }
           
           phase = 0;
        }
        
        return buttons;
    }
    
    /**
     * Ellenőrzi, hogy a buttons mátrixban a megadott pozíció szomszédos-e a megadott színnel .
     *
     * @param buttons A gombok mátrixa
     * @param i       pozíció sor indexe
     * @param j       pozíció oszlop indexe
     * @param color   A szín, amivel szomszédosságot ellenőrizünk
     * @return Igaz, ha szomszédosak, különben hamis
     */
    private boolean isAdjacentToColor(JButton[][] buttons,int i,int j,Color color) {
        if (buttons[Math.max(i - 1, 0)][j].getBackground() == color
            || buttons[Math.min(i + 1, buttons.length - 1)][j].getBackground() == color
            || buttons[i][Math.max(j - 1, 0)].getBackground() == color
            || buttons[i][Math.min(j + 1, buttons.length - 1)].getBackground() == color){
            return true;
        }
        return false;
    }

    
    /**
     * A program belépési pontja, példányosítja és inicializálja a MainWindow objektumot.
     *
     * @param args A parancssori argumentumok
     */
    public static void main(String[] args) {
        try {
            MainWindow mainWindow = new MainWindow();
            mainWindow.createButtons(5);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}