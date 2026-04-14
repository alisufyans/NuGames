package sufyan;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {

    // --- Board ---
    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;

    // --- Game State ---
    private enum GameState { WAITING, PLAYING, GAME_OVER }
    private GameState state = GameState.WAITING;

    // --- Images ---
    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    // --- Block (shared entity) ---
    class Block {
        int x, y, width, height;
        Image img;
        boolean alive = true;
        boolean used = false;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    // --- Ship ---
    int shipWidth = tileSize * 2;
    int shipHeight = tileSize;
    int shipX = tileSize * columns / 2 - tileSize;
    int shipY = tileSize * rows - tileSize * 2;
    int shipVelocityX = 4;
    Block ship;

    // --- Key State ---
    boolean leftPressed = false, rightPressed = false;

    // --- Aliens ---
    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;
    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;
    int alienVelocityX = 1;

    // --- Bullets ---
    ArrayList<Block> bulletArray;
    int bulletWidth = 4;
    int bulletHeight = tileSize / 2;
    int bulletVelocityY = -10;

    // --- Game Stats ---
    Timer gameLoop;
    int score = 0;
    int lives = 3;
    int wave = 1;
    private float pulsePhase = 0f;

    // --- Stars (parallax background) ---
    private ArrayList<double[]> stars = new ArrayList<>(); // x, y, speed, brightness

    // --- Explosion Particles ---
    private ArrayList<double[]> particles = new ArrayList<>(); // x, y, velX, velY, life, r, g, b

    // --- Colors ---
    private static final Color BG_TOP = new Color(5, 5, 25);
    private static final Color BG_BOTTOM = new Color(10, 2, 20);
    private static final Color BULLET_COLOR = new Color(0, 230, 255);
    private static final Color BULLET_GLOW = new Color(0, 230, 255, 40);
    private static final Color HUD_COLOR = new Color(255, 255, 255, 200);
    private static final Color SHIP_GLOW = new Color(0, 200, 255, 30);

    SpaceInvaders() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        // Load images
        shipImg = new ImageIcon(getClass().getResource("/ship.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("/alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("/alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("/alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("/alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        // Initialize starfield
        Random rand = new Random();
        for (int i = 0; i < 120; i++) {
            stars.add(new double[]{
                    rand.nextInt(boardWidth),
                    rand.nextInt(boardHeight),
                    0.3 + rand.nextDouble() * 1.5,     // speed
                    0.3 + rand.nextDouble() * 0.7       // brightness
            });
        }

        createAliens();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    // ==================== GAME LOGIC ====================

    @Override
    public void actionPerformed(ActionEvent e) {
        pulsePhase += 0.05f;

        // Always update stars for atmosphere
        updateStars();

        if (state == GameState.PLAYING) {
            moveShip();
            moveAliens();
            moveBullets();
            updateParticles();
        } else {
            updateParticles(); // particles still render on game over
        }

        repaint();
    }

    private void updateStars() {
        for (double[] star : stars) {
            star[1] += star[2]; // move down
            if (star[1] > boardHeight) {
                star[1] = 0;
                star[0] = Math.random() * boardWidth;
            }
        }
    }

    private void moveShip() {
        if (leftPressed && ship.x > 0) {
            ship.x -= shipVelocityX;
        }
        if (rightPressed && ship.x + ship.width < boardWidth) {
            ship.x += shipVelocityX;
        }
    }

    private void moveAliens() {
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                alien.x += alienVelocityX;

                if (alien.x + alien.width >= boardWidth || alien.x <= 0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX * 2;
                    for (int j = 0; j < alienArray.size(); j++) {
                        alienArray.get(j).y += alienHeight;
                    }
                }

                if (alien.y >= ship.y) {
                    lives--;
                    if (lives <= 0) {
                        state = GameState.GAME_OVER;
                    } else {
                        // Reset aliens for this life
                        alienArray.clear();
                        bulletArray.clear();
                        alienVelocityX = 1 + (wave - 1);
                        createAliens();
                    }
                    return;
                }
            }
        }
    }

    private void moveBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            for (int j = 0; j < alienArray.size(); j++) {
                Block alien = alienArray.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;
                    spawnExplosion(alien.x + alien.width / 2, alien.y + alien.height / 2);
                }
            }
        }

        // Clear off-screen or used bullets
        while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
            bulletArray.remove(0);
        }

        // Next level
        if (alienCount == 0) {
            wave++;
            score += alienColumns * alienRows * 100;
            alienColumns = Math.min(alienColumns + 1, columns / 2 - 2);
            alienRows = Math.min(alienRows + 1, rows - 6);
            alienVelocityX = (alienVelocityX > 0 ? 1 : -1) * (1 + (wave - 1));
            alienArray.clear();
            bulletArray.clear();
            createAliens();
        }
    }

    private void spawnExplosion(int cx, int cy) {
        Random rand = new Random();
        for (int i = 0; i < 12; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double speed = 1.5 + rand.nextDouble() * 3;
            // Random warm color (orange/yellow/cyan)
            int r, g, b;
            if (rand.nextBoolean()) {
                r = 255; g = 150 + rand.nextInt(105); b = 0;
            } else {
                r = 0; g = 200 + rand.nextInt(55); b = 255;
            }
            particles.add(new double[]{cx, cy, Math.cos(angle) * speed, Math.sin(angle) * speed, 20 + rand.nextInt(15), r, g, b});
        }
    }

    private void updateParticles() {
        Iterator<double[]> it = particles.iterator();
        while (it.hasNext()) {
            double[] p = it.next();
            p[0] += p[2]; // x += velX
            p[1] += p[3]; // y += velY
            p[4] -= 1;    // life--
            if (p[4] <= 0) it.remove();
        }
    }

    public void createAliens() {
        Random random = new Random();
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                        alienX + c * alienWidth,
                        alienY + r * alienHeight,
                        alienWidth, alienHeight,
                        alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
                a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private void resetGame() {
        score = 0;
        lives = 3;
        wave = 1;
        alienColumns = 3;
        alienRows = 2;
        alienVelocityX = 1;
        ship.x = shipX;
        alienArray.clear();
        bulletArray.clear();
        particles.clear();
        createAliens();
        state = GameState.PLAYING;
    }

    // ==================== RENDERING ====================

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        drawBackground(g2d);
        drawStars(g2d);

        switch (state) {
            case WAITING:
                drawStartScreen(g2d);
                break;
            case PLAYING:
                drawShip(g2d);
                drawAliens(g2d);
                drawBullets(g2d);
                drawParticles(g2d);
                drawHUD(g2d);
                break;
            case GAME_OVER:
                drawShip(g2d);
                drawAliens(g2d);
                drawParticles(g2d);
                drawGameOverScreen(g2d);
                break;
        }
    }

    private void drawBackground(Graphics2D g2d) {
        GradientPaint bg = new GradientPaint(0, 0, BG_TOP, 0, boardHeight, BG_BOTTOM);
        g2d.setPaint(bg);
        g2d.fillRect(0, 0, boardWidth, boardHeight);
    }

    private void drawStars(Graphics2D g2d) {
        for (double[] star : stars) {
            float brightness = (float) star[3];
            float twinkle = (float) (0.7 + Math.sin(pulsePhase * 2 + star[0]) * 0.3);
            float alpha = Math.min(1f, brightness * twinkle);
            g2d.setColor(new Color(1f, 1f, 1f, alpha));
            int size = star[2] > 1.0 ? 2 : 1;
            g2d.fillRect((int) star[0], (int) star[1], size, size);
        }
    }

    private void drawShip(Graphics2D g2d) {
        // Ship glow
        for (int i = 3; i >= 1; i--) {
            g2d.setColor(new Color(SHIP_GLOW.getRed(), SHIP_GLOW.getGreen(), SHIP_GLOW.getBlue(), SHIP_GLOW.getAlpha() / i));
            g2d.fillOval(ship.x - i * 4, ship.y - i * 2, ship.width + i * 8, ship.height + i * 4);
        }
        // Ship image
        g2d.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        // Engine glow beneath ship
        float enginePulse = (float) (0.6 + Math.sin(pulsePhase * 6) * 0.4);
        g2d.setColor(new Color(0, 180, 255, (int) (40 * enginePulse)));
        g2d.fillOval(ship.x + ship.width / 2 - 8, ship.y + ship.height - 4, 16, 10);
    }

    private void drawAliens(Graphics2D g2d) {
        for (Block alien : alienArray) {
            if (alien.alive) {
                // Subtle glow behind alien
                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillOval(alien.x - 2, alien.y - 2, alien.width + 4, alien.height + 4);
                // Alien image
                g2d.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }
    }

    private void drawBullets(Graphics2D g2d) {
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                // Bullet glow
                g2d.setColor(BULLET_GLOW);
                g2d.fill(new RoundRectangle2D.Float(bullet.x - 3, bullet.y - 2, bullet.width + 6, bullet.height + 4, 4, 4));
                // Bullet body
                GradientPaint bulletGrad = new GradientPaint(bullet.x, bullet.y, BULLET_COLOR, bullet.x + bullet.width, bullet.y + bullet.height, new Color(100, 220, 255));
                g2d.setPaint(bulletGrad);
                g2d.fill(new RoundRectangle2D.Float(bullet.x, bullet.y, bullet.width, bullet.height, 3, 3));
            }
        }
    }

    private void drawParticles(Graphics2D g2d) {
        for (double[] p : particles) {
            float alpha = Math.max(0f, Math.min(1f, (float) (p[4] / 30.0)));
            float size = 3 + alpha * 4;
            g2d.setColor(new Color((int) p[5], (int) p[6], (int) p[7], (int) (alpha * 255)));
            g2d.fill(new Ellipse2D.Double(p[0] - size / 2, p[1] - size / 2, size, size));
        }
    }

    private void drawHUD(Graphics2D g2d) {
        // HUD bar
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRect(0, 0, boardWidth, 40);
        g2d.setColor(new Color(255, 255, 255, 20));
        g2d.drawLine(0, 40, boardWidth, 40);

        Font hudFont = new Font("Consolas", Font.BOLD, 18);
        g2d.setFont(hudFont);

        // Score (left)
        g2d.setColor(BULLET_COLOR);
        g2d.drawString("SCORE: " + score, 15, 27);

        // Wave (center)
        g2d.setColor(HUD_COLOR);
        String waveText = "WAVE " + wave;
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(waveText, boardWidth / 2 - fm.stringWidth(waveText) / 2, 27);

        // Lives (right) — draw ship icons
        g2d.setColor(new Color(255, 100, 100));
        String livesText = "LIVES: ";
        int livesTextWidth = fm.stringWidth(livesText);
        g2d.drawString(livesText, boardWidth - livesTextWidth - 15 - lives * 22, 27);
        for (int i = 0; i < lives; i++) {
            g2d.drawImage(shipImg, boardWidth - 15 - (lives - i) * 22, 10, 20, 20, null);
        }
    }

    private void drawStartScreen(Graphics2D g2d) {
        // Dim overlay
        g2d.setColor(new Color(0, 0, 0, 140));
        g2d.fillRect(0, 0, boardWidth, boardHeight);

        // Title
        g2d.setFont(new Font("Consolas", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "STAR SHIP";
        int tw = fm.stringWidth(title);

        // Title glow
        g2d.setColor(new Color(0, 200, 255, 35));
        g2d.drawString(title, boardWidth / 2 - tw / 2 - 2, 172);
        g2d.drawString(title, boardWidth / 2 - tw / 2 + 2, 168);
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, boardWidth / 2 - tw / 2, 170);

        // Subtitle
        g2d.setFont(new Font("Consolas", Font.PLAIN, 16));
        fm = g2d.getFontMetrics();
        String sub = "Defend Earth from the Alien Invasion";
        g2d.setColor(HUD_COLOR);
        g2d.drawString(sub, boardWidth / 2 - fm.stringWidth(sub) / 2, 200);

        // Controls
        g2d.setFont(new Font("Consolas", Font.PLAIN, 14));
        fm = g2d.getFontMetrics();
        int cy = 270;
        g2d.setColor(BULLET_COLOR);
        String c1 = "\u2190 / \u2192  Move Ship";
        g2d.drawString(c1, boardWidth / 2 - fm.stringWidth(c1) / 2, cy);
        String c2 = "SPACE   Fire";
        g2d.drawString(c2, boardWidth / 2 - fm.stringWidth(c2) / 2, cy + 25);

        // Pulsing start prompt
        float alpha = (float) (0.5 + Math.sin(pulsePhase * 3) * 0.5);
        g2d.setColor(new Color(1f, 1f, 1f, Math.max(0f, Math.min(1f, alpha))));
        g2d.setFont(new Font("Consolas", Font.BOLD, 20));
        fm = g2d.getFontMetrics();
        String prompt = "PRESS  SPACE  TO  START";
        g2d.drawString(prompt, boardWidth / 2 - fm.stringWidth(prompt) / 2, 380);
    }

    private void drawGameOverScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRect(0, 0, boardWidth, boardHeight);

        // Game Over
        g2d.setFont(new Font("Consolas", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String gameOver = "GAME OVER";
        int gow = fm.stringWidth(gameOver);

        g2d.setColor(new Color(255, 50, 50, 35));
        g2d.drawString(gameOver, boardWidth / 2 - gow / 2 - 2, 162);
        g2d.drawString(gameOver, boardWidth / 2 - gow / 2 + 2, 158);
        g2d.setColor(new Color(255, 80, 80));
        g2d.drawString(gameOver, boardWidth / 2 - gow / 2, 160);

        // Final score
        g2d.setFont(new Font("Consolas", Font.BOLD, 24));
        fm = g2d.getFontMetrics();
        g2d.setColor(BULLET_COLOR);
        String scoreText = "FINAL SCORE: " + score;
        g2d.drawString(scoreText, boardWidth / 2 - fm.stringWidth(scoreText) / 2, 220);

        // Wave reached
        g2d.setFont(new Font("Consolas", Font.PLAIN, 18));
        fm = g2d.getFontMetrics();
        g2d.setColor(HUD_COLOR);
        String waveText = "Waves Survived: " + (wave - 1);
        g2d.drawString(waveText, boardWidth / 2 - fm.stringWidth(waveText) / 2, 260);

        // Restart
        float alpha = (float) (0.5 + Math.sin(pulsePhase * 3) * 0.5);
        g2d.setColor(new Color(1f, 1f, 1f, Math.max(0f, Math.min(1f, alpha))));
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));
        fm = g2d.getFontMetrics();
        String restart = "PRESS  SPACE  TO  PLAY  AGAIN";
        g2d.drawString(restart, boardWidth / 2 - fm.stringWidth(restart) / 2, 350);
    }

    // ==================== INPUT ====================

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (state == GameState.WAITING && key == KeyEvent.VK_SPACE) {
            state = GameState.PLAYING;
            return;
        }

        if (state == GameState.GAME_OVER && key == KeyEvent.VK_SPACE) {
            resetGame();
            return;
        }

        if (state == GameState.PLAYING) {
            if (key == KeyEvent.VK_LEFT) leftPressed = true;
            if (key == KeyEvent.VK_RIGHT) rightPressed = true;
            if (key == KeyEvent.VK_SPACE) {
                Block bullet = new Block(ship.x + shipWidth / 2 - bulletWidth / 2, ship.y, bulletWidth, bulletHeight, null);
                bulletArray.add(bullet);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
