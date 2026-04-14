package mikail.inputs;

import mikail.main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static mikail.utilz.Constants.Player1Constants.*;
import static mikail.utilz.Constants.Player2Constants.*;

public class KeyboardInputs2 implements KeyListener {
    private GamePanel gamePanel;
    public KeyboardInputs2(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
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
        switch (e.getKeyCode()){
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                gamePanel.moveDelta(PLAYER_1_IDLE);
                break;
        }
    }
}
