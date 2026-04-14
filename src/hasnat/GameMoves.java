package hasnat;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Timer;
import javax.swing.JPanel;

public class GameMoves extends JPanel implements KeyListener, ActionListener, MouseListener {

    private static final long serialVersionUID = 1L;
    Layout la;
    Build_Player p;
    Timer time;
    int delay = 10;
    int current_player;
    int dice;
    int flag = 0, roll, kill = 0;

    private String[] playerNames = {"RED", "GREEN", "YELLOW", "BLUE"};
    private static final Color[] PLAYER_COLORS = {
            new Color(220, 50, 50),
            new Color(40, 180, 60),
            new Color(230, 200, 30),
            new Color(50, 80, 220)
    };

    // Game states
    private boolean showWinner = false;
    private int winnerPlayer = -1;
    private int winDisplayTimer = 0;
    private static final int WIN_DISPLAY_DURATION = 300; // ~5 seconds at 60fps
    private float pulsePhase = 0f;

    // Panel background
    private static final Color PANEL_BG = new Color(38, 40, 52);

    public GameMoves() {
        setFocusTraversalKeysEnabled(false);
        requestFocus();
        setBackground(PANEL_BG);
        current_player = 0;
        la = new Layout(80, 50);
        p = new Build_Player(la.height, la.width);
        dice = 0;
        flag = 0;
        roll = 0;
        kill = 0;

        // Timer for animations
        time = new Timer(16, this);
        time.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Draw the board and players
        la.draw(g2d);
        p.draw(g2d);

        // Draw right-side info panel
        drawInfoPanel(g2d);

        // Draw dice
        drawDice(g2d);

        // Draw current turn and dice result text
        drawTurnInfo(g2d);

        // Draw instructions
        drawInstructions(g2d);

        // Player name labels on the board
        drawPlayerLabels(g2d);

        // Win overlay
        if (showWinner) {
            drawWinScreen(g2d);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pulsePhase += 0.05f;

        if (showWinner) {
            winDisplayTimer++;
            if (winDisplayTimer >= WIN_DISPLAY_DURATION) {
                showWinner = false;
                winDisplayTimer = 0;
                resetGame();
            }
        }

        repaint();
    }

    // ==================== INFO PANEL ====================

    private void drawInfoPanel(Graphics2D g2d) {
        int px = 550, py = 50, pw = 410, ph = 480;

        // Panel background
        g2d.setColor(new Color(30, 32, 42));
        g2d.fill(new RoundRectangle2D.Float(px, py, pw, ph, 16, 16));

        // Panel border
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(60, 65, 80));
        g2d.draw(new RoundRectangle2D.Float(px, py, pw, ph, 16, 16));

        // Title
        g2d.setFont(new Font("Consolas", Font.BOLD, 24));
        g2d.setColor(Color.WHITE);
        g2d.drawString("LUDO STAR", px + 130, py + 35);

        // Divider
        g2d.setColor(new Color(60, 65, 80));
        g2d.drawLine(px + 20, py + 50, px + pw - 20, py + 50);
    }

    private void drawDice(Graphics2D g2d) {
        int dx = 700, dy = 130, diceSize = 70;

        // Dice shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fill(new RoundRectangle2D.Float(dx + 3, dy + 3, diceSize, diceSize, 12, 12));

        // Dice body
        Color currentColor = PLAYER_COLORS[current_player];
        GradientPaint diceGrad = new GradientPaint(dx, dy, new Color(250, 250, 245), dx + diceSize, dy + diceSize, new Color(220, 220, 215));
        g2d.setPaint(diceGrad);
        g2d.fill(new RoundRectangle2D.Float(dx, dy, diceSize, diceSize, 12, 12));

        // Dice border with player color
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(currentColor);
        g2d.draw(new RoundRectangle2D.Float(dx, dy, diceSize, diceSize, 12, 12));

        // Draw dots
        if (dice > 0) {
            g2d.setColor(new Color(40, 40, 40));
            int dotR = 7;
            int cx = dx + diceSize / 2;
            int cy = dy + diceSize / 2;
            int off = 18;

            // Center dot (1, 3, 5)
            if (dice == 1 || dice == 3 || dice == 5) {
                g2d.fillOval(cx - dotR, cy - dotR, dotR * 2, dotR * 2);
            }
            // Top-right and bottom-left (2, 3, 4, 5, 6)
            if (dice >= 2) {
                g2d.fillOval(cx + off - dotR, cy - off - dotR, dotR * 2, dotR * 2);
                g2d.fillOval(cx - off - dotR, cy + off - dotR, dotR * 2, dotR * 2);
            }
            // Top-left and bottom-right (4, 5, 6)
            if (dice >= 4) {
                g2d.fillOval(cx - off - dotR, cy - off - dotR, dotR * 2, dotR * 2);
                g2d.fillOval(cx + off - dotR, cy + off - dotR, dotR * 2, dotR * 2);
            }
            // Middle-left and middle-right (6)
            if (dice == 6) {
                g2d.fillOval(cx - off - dotR, cy - dotR, dotR * 2, dotR * 2);
                g2d.fillOval(cx + off - dotR, cy - dotR, dotR * 2, dotR * 2);
            }
        } else {
            // No roll yet — show "?"
            g2d.setColor(new Color(150, 150, 150));
            g2d.setFont(new Font("Consolas", Font.BOLD, 32));
            g2d.drawString("?", dx + 27, dy + 47);
        }
    }

    private void drawTurnInfo(Graphics2D g2d) {
        int px = 570, py = 230;

        // Current player indicator
        g2d.setFont(new Font("Consolas", Font.BOLD, 16));
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawString("CURRENT TURN", px, py);

        // Player name with color indicator
        Color pc = PLAYER_COLORS[current_player];
        g2d.setColor(pc);
        g2d.fillRoundRect(px, py + 10, 8, 30, 4, 4);

        g2d.setFont(new Font("Consolas", Font.BOLD, 22));
        g2d.setColor(Color.WHITE);
        g2d.drawString(playerNames[current_player], px + 18, py + 34);

        // Dice result
        if (dice > 0) {
            g2d.setFont(new Font("Consolas", Font.PLAIN, 16));
            g2d.setColor(new Color(180, 180, 180));
            g2d.drawString("Rolled: " + dice, px, py + 65);

            if (flag == 1) {
                // Pulsing "Click a pawn" prompt
                float alpha = (float) (0.5 + Math.sin(pulsePhase * 3) * 0.5);
                g2d.setColor(new Color(0, 230, 255, (int) (Math.max(0, Math.min(1, alpha)) * 255)));
                g2d.setFont(new Font("Consolas", Font.BOLD, 14));
                g2d.drawString("\u25B6 Click a pawn to move!", px, py + 90);
            }
        } else {
            g2d.setFont(new Font("Consolas", Font.PLAIN, 14));
            float alpha = (float) (0.5 + Math.sin(pulsePhase * 3) * 0.5);
            g2d.setColor(new Color(1f, 1f, 1f, Math.max(0f, Math.min(1f, alpha))));
            g2d.drawString("Press ENTER to roll", px, py + 65);
        }

        // Score section
        py = 370;
        g2d.setColor(new Color(60, 65, 80));
        g2d.drawLine(570, py, 940, py);

        g2d.setFont(new Font("Consolas", Font.BOLD, 16));
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawString("SCOREBOARD", 570, py + 25);

        g2d.setFont(new Font("Consolas", Font.PLAIN, 14));
        for (int i = 0; i < 4; i++) {
            int sy = py + 50 + i * 28;
            g2d.setColor(PLAYER_COLORS[i]);
            g2d.fillRoundRect(570, sy - 10, 8, 18, 4, 4);
            g2d.setColor(i == current_player ? Color.WHITE : new Color(160, 160, 160));
            g2d.drawString(playerNames[i] + ":  " + p.pl[i].coin + " / 4 home", 585, sy + 4);
        }
    }

    private void drawInstructions(Graphics2D g2d) {
        int px = 570, py = 510;

        g2d.setFont(new Font("Consolas", Font.PLAIN, 11));
        g2d.setColor(new Color(120, 120, 130));
        g2d.drawString("ENTER = Roll Dice  |  Click = Move Pawn", px, py);
        g2d.drawString("Roll 6 to enter the board", px, py + 16);
    }

    private void drawPlayerLabels(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 14));

        g2d.setColor(PLAYER_COLORS[0]);
        g2d.drawString("RED", 120, 42);

        g2d.setColor(PLAYER_COLORS[1]);
        g2d.drawString("GREEN", 380, 42);

        g2d.setColor(PLAYER_COLORS[3]);
        g2d.drawString("BLUE", 120, 520);

        g2d.setColor(PLAYER_COLORS[2]);
        g2d.drawString("YELLOW", 370, 520);
    }

    private void drawWinScreen(Graphics2D g2d) {
        // Dimming overlay
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        Color winColor = PLAYER_COLORS[winnerPlayer];

        // Winner text
        g2d.setFont(new Font("Consolas", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String winText = playerNames[winnerPlayer] + " WINS!";
        int tw = fm.stringWidth(winText);

        // Text glow
        g2d.setColor(new Color(winColor.getRed(), winColor.getGreen(), winColor.getBlue(), 40));
        g2d.drawString(winText, getWidth() / 2 - tw / 2 - 2, 262);
        g2d.drawString(winText, getWidth() / 2 - tw / 2 + 2, 258);
        g2d.setColor(winColor);
        g2d.drawString(winText, getWidth() / 2 - tw / 2, 260);

        // Congratulations
        g2d.setFont(new Font("Consolas", Font.PLAIN, 22));
        fm = g2d.getFontMetrics();
        String congrats = "Congratulations!";
        g2d.setColor(Color.WHITE);
        g2d.drawString(congrats, getWidth() / 2 - fm.stringWidth(congrats) / 2, 310);

        // Auto-restart countdown
        int remaining = (WIN_DISPLAY_DURATION - winDisplayTimer) / 60 + 1;
        g2d.setFont(new Font("Consolas", Font.PLAIN, 14));
        fm = g2d.getFontMetrics();
        String restartText = "Restarting in " + remaining + "s...";
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawString(restartText, getWidth() / 2 - fm.stringWidth(restartText) / 2, 370);
    }

    // ==================== GAME LOGIC (moved out of paint) ====================

    private void advanceTurn() {
        if (flag == 0 && dice != 0 && dice != 6 && kill == 0) {
            current_player = (current_player + 1) % 4;
        }
        kill = 0;
    }

    private void checkWin() {
        if (p.pl[current_player].coin == 4) {
            showWinner = true;
            winnerPlayer = current_player;
            winDisplayTimer = 0;
        }
    }

    private void resetGame() {
        current_player = 0;
        la = new Layout(80, 50);
        p = new Build_Player(la.height, la.width);
        dice = 0;
        flag = 0;
        roll = 0;
        kill = 0;
        showWinner = false;
    }

    // ==================== INPUT ====================

    @Override
    public void keyPressed(KeyEvent e) {
        if (showWinner) return;

        if (e.getKeyCode() == KeyEvent.VK_ENTER && flag == 0) {
            roll = 0;
            dice = 1 + (int) (Math.random() * 6);
            repaint();
            for (int i = 0; i < 4; i++) {
                if (p.pl[current_player].pa[i].current != -1 && p.pl[current_player].pa[i].current != 56 && (p.pl[current_player].pa[i].current + dice) <= 56) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 0 && dice == 6) {
                for (int i = 0; i < 4; i++) {
                    if (p.pl[current_player].pa[i].current == -1) {
                        flag = 1;
                        break;
                    }
                }
            }
            // If no valid moves, advance the turn
            if (flag == 0) {
                advanceTurn();
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (showWinner) return;

        if (flag == 1) {
            int mx = e.getX();
            int my = e.getY();
            mx = mx - 80;
            my = my - 50;
            mx = mx / 30;
            my = my / 30;
            if (dice == 6) {
                handleDiceSix(mx, my);
            } else {
                handleOtherDice(mx, my);
            }
            checkWin();
            if (!showWinner) {
                advanceTurn();
            }
            repaint();
        }
    }

    private int handleDiceSix(int x, int y) {
        int value = -1;
        for (int i = 0; i < 4; i++) {
            if (p.pl[current_player].pa[i].x == x && p.pl[current_player].pa[i].y == y && (p.pl[current_player].pa[i].current + dice) <= 56) {
                value = i;
                flag = 0;
                break;
            }
        }
        if (value != -1) {
            updatePlayerPosition(value);
        } else {
            for (int i = 0; i < 4; i++) {
                if (p.pl[current_player].pa[i].current == -1) {
                    p.pl[current_player].pa[i].current = 0;
                    flag = 0;
                    break;
                }
            }
        }
        return value;
    }

    private int handleOtherDice(int x, int y) {
        int value = -1;
        for (int i = 0; i < 4; i++) {
            if (p.pl[current_player].pa[i].x == x && p.pl[current_player].pa[i].y == y && (p.pl[current_player].pa[i].current + dice) <= 56) {
                value = i;
                flag = 0;
                break;
            }
        }
        if (value != -1) {
            updatePlayerPosition(value);
        }
        return value;
    }

    private void updatePlayerPosition(int value) {
        p.pl[current_player].pa[value].current += dice;
        if (p.pl[current_player].pa[value].current == 56) {
            p.pl[current_player].coin++;
        }
        int k = 0;
        int hou = p.pl[current_player].pa[value].current;
        if ((hou % 13) != 0 && (hou % 13) != 8 && hou < 51) {
            for (int i = 0; i < 4; i++) {
                if (i != current_player) {
                    for (int j = 0; j < 4; j++) {
                        int tem1 = Path.ax[current_player][p.pl[current_player].pa[value].current], tem2 = Path.ay[current_player][p.pl[current_player].pa[value].current];
                        if (p.pl[i].pa[j].x == tem1 && p.pl[i].pa[j].y == tem2) {
                            p.pl[i].pa[j].current = -1;
                            kill = 1;
                            k = 1;
                            break;
                        }
                    }
                }
                if (k == 1)
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {}

    @Override
    public void keyTyped(KeyEvent arg0) {}

    @Override
    public void mouseEntered(MouseEvent arg0) {}

    @Override
    public void mouseExited(MouseEvent arg0) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent arg0) {}
}
