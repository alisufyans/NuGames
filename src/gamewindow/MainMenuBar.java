package gamewindow;

import hasnat.GameScreen;

import mikail.main.Game;
import nihal.App;
import shaheer.PongGame;
import sufyan.App2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MainMenuBar extends JMenuBar{
    JFrame jframe;
    public MainMenuBar(JFrame jframe){
        this.jframe = jframe;

        JMenu menu = new JMenu("Home");
        JMenu menu2 = new JMenu("Games");
        JMenuItem i1 = new JMenuItem("Cross to Kacha");
        JMenuItem i2 = new JMenuItem("Flappy Bird");
        JMenuItem i3 = new JMenuItem("Ludo Star");
        JMenuItem i4 = new JMenuItem("Star Ship");
        JMenuItem i5 = new JMenuItem("Pong Game");
        Cursor c = new Cursor(Cursor.HAND_CURSOR);
        i1.setCursor(c);
        i2.setCursor(c);
        i3.setCursor(c);
        i4.setCursor(c);
        i5.setCursor(c);

        i1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Game();
                jframe.dispose();
            }
        });

        i2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new App();
                jframe.dispose();
            }
        });

        i3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GameScreen();
                jframe.dispose();
            }
        });

        i4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new App2();
                jframe.dispose();
            }
        });

        i5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PongGame().main();
                jframe.dispose();
            }
        });


        menu.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MainGamePanel();
                jframe.dispose();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


        menu2.add(i1);
        menu2.add(i2);
        menu2.add(i3);
        menu2.add(i4);
        menu2.add(i5);

        menu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menu2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(menu);
        add(menu2);


    }
}
