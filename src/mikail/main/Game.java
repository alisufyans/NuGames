package mikail.main;

import gamewindow.MainMenuBar;

import javax.swing.*;

public class Game implements Runnable{
    private final int FPS_SET = 120;
    private Thread gameThread;
    private GamePanel gamePanel;
    public Game(){
        JFrame frame = new JFrame();
        gamePanel = new GamePanel();
        gamePanel.setFocusable(true);
        frame.setTitle("Cross To Kacha");
        frame.setJMenuBar(new MainMenuBar(frame));
        frame.setResizable(false);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        startGameLoop();
    }


    public void startGameLoop(){
        gameThread = new Thread(this);
        gameThread.start();

    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0/ FPS_SET;
        long lastFrame = System.nanoTime();
        long now = System.nanoTime();


        long lastCheck = System.currentTimeMillis();

        while(true){
            now = System.nanoTime();
            if(now - lastFrame >= timePerFrame){
                gamePanel.repaint();
                lastFrame = now;

            }

            if(System.currentTimeMillis() - lastCheck >= 1000){
                lastCheck = System.currentTimeMillis();
            }
        }
        }


    }
