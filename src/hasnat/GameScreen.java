package hasnat;

import javax.swing.*;
import gamewindow.MainMenuBar;

public class GameScreen {
	   public GameScreen() {
	        JFrame jframe = new JFrame();
		   	jframe.setJMenuBar(new MainMenuBar(jframe));
	        jframe.setBounds(10,10,1000,600);
	        jframe.setTitle("LUDO");
	        jframe.setResizable(false);
	        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        GameMoves gm = new GameMoves();
	        gm.setFocusable(true);
	        gm.addKeyListener(gm);
	        gm.addMouseListener(gm);
	        jframe.add(gm);
		    jframe.setLocationRelativeTo(null);
	        jframe.setVisible(true);
	   }
}
