package mikail.inputs;

import mikail.main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import static mikail.utilz.Constants.Player1Constants.*;
import static mikail.utilz.Constants.Player2Constants.*;

public class KeyboardInputs implements KeyListener {
    private GamePanel gamePanel;

    public KeyboardInputs(GamePanel gamePanel){
        this.gamePanel = gamePanel;

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
                case KeyEvent.VK_SPACE:
                    gamePanel.RUNNING = true;
                    break;
                case KeyEvent.VK_W:
                    gamePanel.moveDelta(PLAYER_1_UP);
                    break;
                case KeyEvent.VK_S:
                    gamePanel.moveDelta(PLAYER_1_DOWN);
                    break;
                case KeyEvent.VK_A:
                    gamePanel.moveDelta(PLAYER_1_LEFT);
                    break;
                case KeyEvent.VK_D:
                    gamePanel.moveDelta(PLAYER_1_RIGHT);
                    break;
                // Player 2 Keys
                case KeyEvent.VK_UP:
                    gamePanel.moveDelta2(PLAYER_2_UP);
                    break;
                case KeyEvent.VK_DOWN:
                    gamePanel.moveDelta2(PLAYER_2_DOWN);
                    break;
                case KeyEvent.VK_LEFT:
                    gamePanel.moveDelta2(PLAYER_2_LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                    gamePanel.moveDelta2(PLAYER_2_RIGHT);
                    break;
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
