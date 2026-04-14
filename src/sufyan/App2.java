package sufyan;

import gamewindow.MainMenuBar;

import javax.swing.*;

public class App2 {
    public App2() {
        //window variables
        int tileSize = 32;
        int rows = 16;
        int columns = 16;
        int boardWidth = tileSize * columns; // 32 * 16 = 512px
        int boardHeight = tileSize * rows; // 32 * 16 = 512px

        JFrame frame = new JFrame("Space Invaders");
        // frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setJMenuBar(new MainMenuBar(frame));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpaceInvaders spaceInvaders = new SpaceInvaders();
        frame.add(spaceInvaders);
        frame.pack();
        spaceInvaders.requestFocus();
        frame.setVisible(true);
    }
}