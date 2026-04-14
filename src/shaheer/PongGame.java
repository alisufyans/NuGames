package shaheer;

import gamewindow.MainMenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class PongGame extends JPanel implements ActionListener, KeyListener {

    // --- Constants ---
    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 600;
    private static final int PADDLE_WIDTH = 12;
    private static final int PADDLE_HEIGHT = 100;
    private static final int PADDLE_MARGIN = 50;
    private static final int BALL_SIZE = 18;
    private static final int PADDLE_SPEED = 6;
    private static final double INITIAL_BALL_SPEED = 4.0;
    private static final double BALL_SPEED_INCREMENT = 0.3;
    private static final double MAX_BALL_SPEED = 12.0;
    private static final int MAX_MISSES = 5;
    private static final int TRAIL_LENGTH = 8;

    // --- Game States ---
    private enum GameState { WAITING, PLAYING, GAME_OVER }
    private GameState state = GameState.WAITING;

    // --- Ball ---
    private double ballX, ballY;
    private double ballVelX, ballVelY;
    private double ballSpeed = INITIAL_BALL_SPEED;

    // --- Ball Trail ---
    private ArrayList<double[]> ballTrail = new ArrayList<>();

    // --- Paddles ---
    private int paddle1Y = BOARD_HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int paddle2Y = BOARD_HEIGHT / 2 - PADDLE_HEIGHT / 2;

    // --- Key State Tracking (smooth movement) ---
    private boolean p1Up = false, p1Down = false;
    private boolean p2Up = false, p2Down = false;

    // --- Scores ---
    private int score1 = 0, score2 = 0;

    // --- Visual Effects ---
    private int hitFlashTimer = 0;
    private int hitFlashSide = 0; // 1 = left, 2 = right
    private float pulsePhase = 0f;

    // --- Timer ---
    private Timer timer;

    // --- Colors ---
    private static final Color BG_COLOR = new Color(10, 10, 30);
    private static final Color BG_GRADIENT_TOP = new Color(15, 15, 45);
    private static final Color BG_GRADIENT_BOTTOM = new Color(5, 5, 20);
    private static final Color P1_COLOR = new Color(0, 230, 255);       // Cyan
    private static final Color P1_GLOW = new Color(0, 230, 255, 40);
    private static final Color P2_COLOR = new Color(255, 50, 180);      // Magenta/Pink
    private static final Color P2_GLOW = new Color(255, 50, 180, 40);
    private static final Color CENTER_LINE_COLOR = new Color(255, 255, 255, 25);
    private static final Color HUD_COLOR = new Color(255, 255, 255, 180);
    private static final Color SCORE_COLOR = new Color(255, 255, 255, 220);

    public PongGame() {
        setFocusable(true);
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(BG_COLOR);
        addKeyListener(this);

        resetBall();

        // Use ~60 FPS game loop
        timer = new Timer(16, this);
    }

    // --- Entry point called from MainMenuBar / MainGamePanel ---
    public void main() {
        JFrame frame = new JFrame("Pong Game");
        frame.setJMenuBar(new MainMenuBar(frame));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        // Reuse 'this' instance instead of creating a second one
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        this.requestFocusInWindow();
        timer.start();
    }

    // ==================== GAME LOGIC ====================

    @Override
    public void actionPerformed(ActionEvent e) {
        pulsePhase += 0.04f;

        if (state == GameState.PLAYING) {
            updatePaddles();
            updateBall();
            checkCollisions();

            if (hitFlashTimer > 0) {
                hitFlashTimer--;
            }
        }

        repaint();
    }

    private void updatePaddles() {
        if (p1Up && paddle1Y > 0) {
            paddle1Y -= PADDLE_SPEED;
        }
        if (p1Down && paddle1Y < BOARD_HEIGHT - PADDLE_HEIGHT) {
            paddle1Y += PADDLE_SPEED;
        }
        if (p2Up && paddle2Y > 0) {
            paddle2Y -= PADDLE_SPEED;
        }
        if (p2Down && paddle2Y < BOARD_HEIGHT - PADDLE_HEIGHT) {
            paddle2Y += PADDLE_SPEED;
        }
    }

    private void updateBall() {
        // Store trail position
        ballTrail.add(new double[]{ballX, ballY});
        if (ballTrail.size() > TRAIL_LENGTH) {
            ballTrail.remove(0);
        }

        ballX += ballVelX;
        ballY += ballVelY;
    }

    private void checkCollisions() {
        // --- Top/Bottom wall bounce ---
        if (ballY <= 0) {
            ballY = 0;
            ballVelY = Math.abs(ballVelY);
        }
        if (ballY >= BOARD_HEIGHT - BALL_SIZE) {
            ballY = BOARD_HEIGHT - BALL_SIZE;
            ballVelY = -Math.abs(ballVelY);
        }

        // --- Left paddle collision ---
        int p1Right = PADDLE_MARGIN + PADDLE_WIDTH;
        if (ballVelX < 0 && ballX <= p1Right && ballX >= PADDLE_MARGIN - BALL_SIZE
                && ballY + BALL_SIZE >= paddle1Y && ballY <= paddle1Y + PADDLE_HEIGHT) {
            ballX = p1Right; // Push ball out
            handlePaddleHit(paddle1Y, 1);
        }

        // --- Right paddle collision ---
        int p2Left = BOARD_WIDTH - PADDLE_MARGIN - PADDLE_WIDTH;
        if (ballVelX > 0 && ballX + BALL_SIZE >= p2Left && ballX <= BOARD_WIDTH - PADDLE_MARGIN
                && ballY + BALL_SIZE >= paddle2Y && ballY <= paddle2Y + PADDLE_HEIGHT) {
            ballX = p2Left - BALL_SIZE; // Push ball out
            handlePaddleHit(paddle2Y, 2);
        }

        // --- Ball goes out of bounds (left) ---
        if (ballX + BALL_SIZE < 0) {
            score2++;
            checkGameOver();
            if (state != GameState.GAME_OVER) {
                resetBall();
            }
        }

        // --- Ball goes out of bounds (right) ---
        if (ballX > BOARD_WIDTH) {
            score1++;
            checkGameOver();
            if (state != GameState.GAME_OVER) {
                resetBall();
            }
        }
    }

    private void handlePaddleHit(int paddleY, int side) {
        // Calculate hit position relative to paddle center (-1.0 to 1.0)
        double paddleCenter = paddleY + PADDLE_HEIGHT / 2.0;
        double ballCenter = ballY + BALL_SIZE / 2.0;
        double relativeHit = (ballCenter - paddleCenter) / (PADDLE_HEIGHT / 2.0);
        relativeHit = Math.max(-1.0, Math.min(1.0, relativeHit));

        // Increase ball speed
        ballSpeed = Math.min(ballSpeed + BALL_SPEED_INCREMENT, MAX_BALL_SPEED);

        // Calculate new velocity based on hit position
        double angle = relativeHit * Math.toRadians(60); // Max 60 degree deflection
        if (side == 1) {
            ballVelX = ballSpeed * Math.cos(angle);
            ballVelY = ballSpeed * Math.sin(angle);
        } else {
            ballVelX = -ballSpeed * Math.cos(angle);
            ballVelY = ballSpeed * Math.sin(angle);
        }

        // Visual feedback
        hitFlashTimer = 6;
        hitFlashSide = side;
    }

    private void checkGameOver() {
        if (score1 >= MAX_MISSES || score2 >= MAX_MISSES) {
            state = GameState.GAME_OVER;
        }
    }

    private void resetBall() {
        ballX = BOARD_WIDTH / 2.0 - BALL_SIZE / 2.0;
        ballY = BOARD_HEIGHT / 2.0 - BALL_SIZE / 2.0;
        ballSpeed = INITIAL_BALL_SPEED;
        ballTrail.clear();

        // Random initial direction
        double angle = (Math.random() - 0.5) * Math.toRadians(60);
        int direction = Math.random() > 0.5 ? 1 : -1;
        ballVelX = direction * ballSpeed * Math.cos(angle);
        ballVelY = ballSpeed * Math.sin(angle);
    }

    private void resetGame() {
        score1 = 0;
        score2 = 0;
        paddle1Y = BOARD_HEIGHT / 2 - PADDLE_HEIGHT / 2;
        paddle2Y = BOARD_HEIGHT / 2 - PADDLE_HEIGHT / 2;
        hitFlashTimer = 0;
        resetBall();
        state = GameState.PLAYING;
    }

    // ==================== RENDERING ====================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Draw background gradient
        drawBackground(g2d);

        // Draw center court line
        drawCenterLine(g2d);

        switch (state) {
            case WAITING:
                drawPaddles(g2d);
                drawBall(g2d);
                drawHUD(g2d);
                drawStartScreen(g2d);
                break;
            case PLAYING:
                drawPaddles(g2d);
                drawBallTrail(g2d);
                drawBall(g2d);
                drawHUD(g2d);
                break;
            case GAME_OVER:
                drawPaddles(g2d);
                drawBall(g2d);
                drawHUD(g2d);
                drawGameOverScreen(g2d);
                break;
        }
    }

    private void drawBackground(Graphics2D g2d) {
        GradientPaint bgGrad = new GradientPaint(0, 0, BG_GRADIENT_TOP, 0, BOARD_HEIGHT, BG_GRADIENT_BOTTOM);
        g2d.setPaint(bgGrad);
        g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Subtle vignette corners
        RadialGradientPaint vignette = new RadialGradientPaint(
                BOARD_WIDTH / 2f, BOARD_HEIGHT / 2f, BOARD_WIDTH * 0.7f,
                new float[]{0f, 1f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 100)}
        );
        g2d.setPaint(vignette);
        g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
    }

    private void drawCenterLine(Graphics2D g2d) {
        g2d.setColor(CENTER_LINE_COLOR);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{12, 12}, 0));
        g2d.drawLine(BOARD_WIDTH / 2, 15, BOARD_WIDTH / 2, BOARD_HEIGHT - 15);

        // Center circle
        int circleSize = 60;
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(BOARD_WIDTH / 2 - circleSize / 2, BOARD_HEIGHT / 2 - circleSize / 2, circleSize, circleSize);
    }

    private void drawPaddles(Graphics2D g2d) {
        // --- Player 1 paddle (Cyan) ---
        int p1x = PADDLE_MARGIN;
        boolean p1Flash = hitFlashTimer > 0 && hitFlashSide == 1;

        // Glow
        Color glowColor = p1Flash ? new Color(0, 255, 255, 60) : P1_GLOW;
        for (int i = 3; i >= 1; i--) {
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), glowColor.getAlpha() / i));
            g2d.fill(new RoundRectangle2D.Float(p1x - i * 3, paddle1Y - i * 3, PADDLE_WIDTH + i * 6, PADDLE_HEIGHT + i * 6, 8, 8));
        }

        // Paddle body
        GradientPaint p1Grad = new GradientPaint(p1x, paddle1Y, P1_COLOR, p1x + PADDLE_WIDTH, paddle1Y + PADDLE_HEIGHT, P1_COLOR.brighter());
        g2d.setPaint(p1Grad);
        g2d.fill(new RoundRectangle2D.Float(p1x, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT, 6, 6));

        // --- Player 2 paddle (Magenta) ---
        int p2x = BOARD_WIDTH - PADDLE_MARGIN - PADDLE_WIDTH;
        boolean p2Flash = hitFlashTimer > 0 && hitFlashSide == 2;

        // Glow
        glowColor = p2Flash ? new Color(255, 100, 220, 60) : P2_GLOW;
        for (int i = 3; i >= 1; i--) {
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), glowColor.getAlpha() / i));
            g2d.fill(new RoundRectangle2D.Float(p2x - i * 3, paddle2Y - i * 3, PADDLE_WIDTH + i * 6, PADDLE_HEIGHT + i * 6, 8, 8));
        }

        // Paddle body
        GradientPaint p2Grad = new GradientPaint(p2x, paddle2Y, P2_COLOR, p2x + PADDLE_WIDTH, paddle2Y + PADDLE_HEIGHT, P2_COLOR.brighter());
        g2d.setPaint(p2Grad);
        g2d.fill(new RoundRectangle2D.Float(p2x, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT, 6, 6));
    }

    private void drawBallTrail(Graphics2D g2d) {
        for (int i = 0; i < ballTrail.size(); i++) {
            double[] pos = ballTrail.get(i);
            float alpha = (float) (i + 1) / ballTrail.size() * 0.35f;
            float size = BALL_SIZE * ((float) (i + 1) / ballTrail.size());

            g2d.setColor(new Color(1f, 1f, 1f, alpha));
            g2d.fill(new Ellipse2D.Double(
                    pos[0] + (BALL_SIZE - size) / 2,
                    pos[1] + (BALL_SIZE - size) / 2,
                    size, size));
        }
    }

    private void drawBall(Graphics2D g2d) {
        // Outer glow
        for (int i = 4; i >= 1; i--) {
            g2d.setColor(new Color(255, 255, 255, 12 / i));
            g2d.fill(new Ellipse2D.Double(ballX - i * 4, ballY - i * 4, BALL_SIZE + i * 8, BALL_SIZE + i * 8));
        }

        // Ball body
        float pulse = (float) (1.0 + Math.sin(pulsePhase * 2) * 0.05);
        float pSize = BALL_SIZE * pulse;
        float pOffset = (BALL_SIZE - pSize) / 2f;

        RadialGradientPaint ballGrad = new RadialGradientPaint(
                (float) (ballX + BALL_SIZE / 2), (float) (ballY + BALL_SIZE / 2), pSize / 2f,
                new float[]{0f, 0.7f, 1f},
                new Color[]{Color.WHITE, new Color(220, 220, 255), new Color(180, 180, 220)}
        );
        g2d.setPaint(ballGrad);
        g2d.fill(new Ellipse2D.Double(ballX + pOffset, ballY + pOffset, pSize, pSize));
    }

    private void drawHUD(Graphics2D g2d) {
        // Score background bar
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRect(0, 0, BOARD_WIDTH, 55);
        g2d.setColor(new Color(255, 255, 255, 15));
        g2d.drawLine(0, 55, BOARD_WIDTH, 55);

        Font scoreFont = new Font("Consolas", Font.BOLD, 32);
        Font labelFont = new Font("Consolas", Font.PLAIN, 14);
        g2d.setFont(scoreFont);

        // Player 1 score
        g2d.setColor(P1_COLOR);
        String s1 = String.valueOf(score1);
        g2d.drawString(s1, BOARD_WIDTH / 2 - 80, 38);

        // Player 2 score
        g2d.setColor(P2_COLOR);
        String s2 = String.valueOf(score2);
        g2d.drawString(s2, BOARD_WIDTH / 2 + 60, 38);

        // Divider
        g2d.setColor(HUD_COLOR);
        g2d.setFont(new Font("Consolas", Font.PLAIN, 28));
        g2d.drawString(":", BOARD_WIDTH / 2 - 6, 36);

        // Player labels
        g2d.setFont(labelFont);
        g2d.setColor(P1_COLOR);
        g2d.drawString("P1  [W/S]", 20, 38);
        g2d.setColor(P2_COLOR);
        String p2Label = "[\u2191/\u2193]  P2";
        g2d.drawString(p2Label, BOARD_WIDTH - 100, 38);

        // First to N label
        g2d.setFont(new Font("Consolas", Font.PLAIN, 12));
        g2d.setColor(HUD_COLOR);
        g2d.drawString("FIRST TO " + MAX_MISSES, BOARD_WIDTH / 2 - 35, 50);
    }

    private void drawStartScreen(Graphics2D g2d) {
        // Dimming overlay
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Title
        g2d.setFont(new Font("Consolas", Font.BOLD, 56));
        String title = "PONG";
        FontMetrics fm = g2d.getFontMetrics();
        int titleW = fm.stringWidth(title);

        // Title glow
        g2d.setColor(new Color(0, 230, 255, 40));
        g2d.drawString(title, BOARD_WIDTH / 2 - titleW / 2 - 2, 232);
        g2d.drawString(title, BOARD_WIDTH / 2 - titleW / 2 + 2, 228);
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, BOARD_WIDTH / 2 - titleW / 2, 230);

        // Subtitle
        g2d.setFont(new Font("Consolas", Font.PLAIN, 16));
        g2d.setColor(HUD_COLOR);
        String sub = "A Classic Reimagined";
        fm = g2d.getFontMetrics();
        g2d.drawString(sub, BOARD_WIDTH / 2 - fm.stringWidth(sub) / 2, 260);

        // Controls info
        g2d.setFont(new Font("Consolas", Font.PLAIN, 14));
        int infoY = 320;

        g2d.setColor(P1_COLOR);
        String c1 = "Player 1:  W / S";
        g2d.drawString(c1, BOARD_WIDTH / 2 - fm.stringWidth(c1) / 2 - 20, infoY);

        g2d.setColor(P2_COLOR);
        String c2 = "Player 2:  \u2191 / \u2193";
        g2d.drawString(c2, BOARD_WIDTH / 2 - fm.stringWidth(c2) / 2 - 20, infoY + 25);

        // Pulsing "Press SPACE" prompt
        float alpha = (float) (0.5 + Math.sin(pulsePhase * 3) * 0.5);
        g2d.setColor(new Color(1f, 1f, 1f, Math.max(0f, Math.min(1f, alpha))));
        g2d.setFont(new Font("Consolas", Font.BOLD, 20));
        String prompt = "PRESS  SPACE  TO  START";
        fm = g2d.getFontMetrics();
        g2d.drawString(prompt, BOARD_WIDTH / 2 - fm.stringWidth(prompt) / 2, 420);
    }

    private void drawGameOverScreen(Graphics2D g2d) {
        // Dimming overlay
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Winner determination
        boolean p1Wins = score1 >= MAX_MISSES;
        Color winnerColor = p1Wins ? P1_COLOR : P2_COLOR;
        String winnerName = p1Wins ? "PLAYER 1" : "PLAYER 2";

        // "GAME OVER" text
        g2d.setFont(new Font("Consolas", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String gameOverText = "GAME OVER";
        int goW = fm.stringWidth(gameOverText);

        // Glow behind text
        g2d.setColor(new Color(winnerColor.getRed(), winnerColor.getGreen(), winnerColor.getBlue(), 30));
        g2d.drawString(gameOverText, BOARD_WIDTH / 2 - goW / 2 - 2, 202);
        g2d.drawString(gameOverText, BOARD_WIDTH / 2 - goW / 2 + 2, 198);
        g2d.setColor(Color.WHITE);
        g2d.drawString(gameOverText, BOARD_WIDTH / 2 - goW / 2, 200);

        // Winner announcement
        g2d.setFont(new Font("Consolas", Font.BOLD, 28));
        fm = g2d.getFontMetrics();
        String winText = winnerName + "  WINS!";
        g2d.setColor(winnerColor);
        g2d.drawString(winText, BOARD_WIDTH / 2 - fm.stringWidth(winText) / 2, 260);

        // Final score
        g2d.setFont(new Font("Consolas", Font.PLAIN, 22));
        fm = g2d.getFontMetrics();
        String scoreText = score1 + "  -  " + score2;
        g2d.setColor(SCORE_COLOR);
        g2d.drawString(scoreText, BOARD_WIDTH / 2 - fm.stringWidth(scoreText) / 2, 310);

        // Restart prompt
        float alpha = (float) (0.5 + Math.sin(pulsePhase * 3) * 0.5);
        g2d.setColor(new Color(1f, 1f, 1f, Math.max(0f, Math.min(1f, alpha))));
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));
        fm = g2d.getFontMetrics();
        String restartText = "PRESS  SPACE  TO  PLAY  AGAIN";
        g2d.drawString(restartText, BOARD_WIDTH / 2 - fm.stringWidth(restartText) / 2, 400);
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

        // Paddle movement flags
        if (key == KeyEvent.VK_W) p1Up = true;
        if (key == KeyEvent.VK_S) p1Down = true;
        if (key == KeyEvent.VK_UP) p2Up = true;
        if (key == KeyEvent.VK_DOWN) p2Down = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) p1Up = false;
        if (key == KeyEvent.VK_S) p1Down = false;
        if (key == KeyEvent.VK_UP) p2Up = false;
        if (key == KeyEvent.VK_DOWN) p2Down = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}