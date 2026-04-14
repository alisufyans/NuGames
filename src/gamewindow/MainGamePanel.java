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

public class MainGamePanel {
    public MainGamePanel(){
        MainPanel();
    }

    public void MainPanel(){
        JFrame jframe = new JFrame("NuGames");
        JPanel jpanel = new JPanel();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setResizable(false);
        jframe.setBackground(new Color(0xFFFFFF));

        jframe.add(jpanel);
        jframe.setJMenuBar(new MainMenuBar(jframe));
        jpanel.setPreferredSize(new Dimension(900, 600));
        jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.PAGE_AXIS));
        jpanel.setBackground(new Color(0xFFFFFF));

        JPanel header = new JPanel();
        JPanel footer = new JPanel();
        JPanel center = new JPanel();


        jpanel.add(header);
        jpanel.add(center);
        jpanel.add(footer);


        // layouts & sizes
        header.setBackground(Color.white);
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.setPreferredSize(new Dimension(860, 700));
        header.setPreferredSize(new Dimension(860, -10));
        center.setBackground(Color.white);


        // header
        ImageIcon headerIcon = new ImageIcon(getClass().getResource("/nugames.png"));
        header.add(new JLabel(headerIcon));


        // center
        JPanel mikailGame = new JPanel();
        JPanel nihalGame = new JPanel();
        JPanel hasnatGame = new JPanel();
        JPanel sufyanGame = new JPanel();
        JPanel shaheerGame = new JPanel();



        center.add(mikailGame);
        center.add(nihalGame);
        center.add(hasnatGame);
        center.add(sufyanGame);
        center.add(shaheerGame);

        //
        Dimension dimension = new Dimension(172, 172);


        // set layouts

        mikailGame.setLayout(new BoxLayout(mikailGame, BoxLayout.PAGE_AXIS));
        nihalGame.setLayout(new BoxLayout(nihalGame, BoxLayout.PAGE_AXIS));
        sufyanGame.setLayout(new BoxLayout(sufyanGame, BoxLayout.PAGE_AXIS));
        hasnatGame.setLayout(new BoxLayout(hasnatGame, BoxLayout.PAGE_AXIS));
        shaheerGame.setLayout(new BoxLayout(shaheerGame, BoxLayout.PAGE_AXIS));

        //set colors
        Color color = new Color(0xFFFFFF);
        mikailGame.setBackground(color);
        nihalGame.setBackground(color);
        hasnatGame.setBackground(color);
        sufyanGame.setBackground(color);
        shaheerGame.setBackground(color);

        // mikail
        JLabel gamelabel1 = new JLabel(new ImageIcon(getClass().getResource("/mikail.png")));
        gamelabel1.setPreferredSize(dimension);
        JButton launchGame1 = new JButton();
        launchGame1.setBackground(new Color(0x17B1D0));
        launchGame1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        launchGame1.setText("Cross to Kacha");
        launchGame1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Game();
                jframe.dispose();
            }
        });

        mikailGame.add(gamelabel1);
        mikailGame.add(launchGame1);

        //nihal
        JLabel gamelabel2 = new JLabel(new ImageIcon(getClass().getResource("/nihal.png")));
        gamelabel2.setPreferredSize(dimension);
        JButton launchGame2 = new JButton();
        launchGame2.setBackground(new Color(0x17B1D0));
        launchGame2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        launchGame2.setText("Flappy Bird");
        launchGame2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new App();
                jframe.dispose();
            }
        });

        nihalGame.add(gamelabel2);
        nihalGame.add(launchGame2);

        // hasnat game
        JLabel gamelabel3 = new JLabel(new ImageIcon(getClass().getResource("/hasnat.png")));
        gamelabel3.setPreferredSize(dimension);
        JButton launchGame3 = new JButton();
        launchGame3.setBackground(new Color(0x17B1D0));
        launchGame3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        launchGame3.setText("Ludo Star");
        launchGame3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GameScreen();
                jframe.dispose();
            }
        });


        hasnatGame.add(gamelabel3);
        hasnatGame.add(launchGame3);

        //sufyan
        JLabel gamelabel4 = new JLabel(new ImageIcon(getClass().getResource("/sufyan.png")));
        gamelabel4.setPreferredSize(dimension);
        JButton launchGame4 = new JButton();
        launchGame4.setBackground(new Color(0x17B1D0));
        launchGame4.setCursor(new Cursor(Cursor.HAND_CURSOR));
        launchGame4.setText("Star Ship");
        launchGame4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new App2();
                jframe.dispose();
            }
        });


        sufyanGame.add(gamelabel4);
        sufyanGame.add(launchGame4);




        //shaheer
        JLabel gamelabel5 = new JLabel(new ImageIcon(getClass().getResource("/shaheer.png")));
        gamelabel5.setPreferredSize(dimension);
        JButton launchGame5 = new JButton();
        launchGame5.setBackground(new Color(0x17B1D0));
        launchGame5.setCursor(new Cursor(Cursor.HAND_CURSOR));
        launchGame5.setText("Pong Game");
        launchGame5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PongGame().main();
                jframe.dispose();
            }
        });


        shaheerGame.add(gamelabel5);
        shaheerGame.add(launchGame5);


        //footer
        footer.setBackground(new Color(0x000000));
        JLabel footerlabel = new JLabel("@NuGames 2024");
        footerlabel.setForeground(Color.white);
        footer.add(footerlabel);

        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
    }
}
