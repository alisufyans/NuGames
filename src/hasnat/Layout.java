package hasnat;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Layout {

    int x, y, width, height;

    // Modern color palette
    private static final Color RED_MAIN = new Color(220, 50, 50);
    private static final Color RED_LIGHT = new Color(245, 100, 100);
    private static final Color GREEN_MAIN = new Color(40, 180, 60);
    private static final Color GREEN_LIGHT = new Color(80, 210, 100);
    private static final Color YELLOW_MAIN = new Color(230, 200, 30);
    private static final Color YELLOW_LIGHT = new Color(250, 230, 80);
    private static final Color BLUE_MAIN = new Color(50, 80, 220);
    private static final Color BLUE_LIGHT = new Color(90, 120, 245);
    private static final Color CELL_BG = new Color(245, 245, 240);
    private static final Color GRID_COLOR = new Color(180, 180, 175);
    private static final Color BOARD_BORDER = new Color(60, 60, 60);

    public Layout(int xi, int yi) {
        x = xi;
        y = yi;
        width = 30;
        height = 30;
    }

    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Board background with subtle shadow
        g.setColor(new Color(0, 0, 0, 40));
        g.fillRoundRect(x + 4, y + 4, 15 * width, 15 * height, 8, 8);
        g.setColor(CELL_BG);
        g.fillRoundRect(x, y, 15 * width, 15 * height, 6, 6);

        // Draw home bases with gradients
        drawHomeBase(g, x + width, y + height, RED_MAIN, RED_LIGHT);        // Top-left (Red)
        drawHomeBase(g, x + 10 * width, y + height, GREEN_MAIN, GREEN_LIGHT); // Top-right (Green)
        drawHomeBase(g, x + width, y + 10 * height, BLUE_MAIN, BLUE_LIGHT);   // Bottom-left (Blue)
        drawHomeBase(g, x + 10 * width, y + 10 * height, YELLOW_MAIN, YELLOW_LIGHT); // Bottom-right (Yellow)

        // Draw colored cells along the paths
        // Red path cells
        for (int i = 0; i < 6; i++) {
            fillCell(g, x + (i * width), y, RED_MAIN, RED_LIGHT);
            fillCell(g, x, y + (i * height), RED_MAIN, RED_LIGHT);
            fillCell(g, x + (i * width), y + (5 * height), RED_MAIN, RED_LIGHT);
            fillCell(g, x + (5 * width), y + (i * height), RED_MAIN, RED_LIGHT);
        }
        // Green path cells
        for (int i = 0; i < 6; i++) {
            fillCell(g, x + ((i + 9) * width), y, GREEN_MAIN, GREEN_LIGHT);
            fillCell(g, x + (9 * width), y + (i * height), GREEN_MAIN, GREEN_LIGHT);
            fillCell(g, x + ((i + 9) * width), y + (5 * height), GREEN_MAIN, GREEN_LIGHT);
            fillCell(g, x + (14 * width), y + (i * height), GREEN_MAIN, GREEN_LIGHT);
        }
        // Yellow path cells
        for (int i = 0; i < 6; i++) {
            fillCell(g, x + ((i + 9) * width), y + (9 * height), YELLOW_MAIN, YELLOW_LIGHT);
            fillCell(g, x + (9 * width), y + ((i + 9) * height), YELLOW_MAIN, YELLOW_LIGHT);
            fillCell(g, x + ((i + 9) * width), y + (14 * height), YELLOW_MAIN, YELLOW_LIGHT);
            fillCell(g, x + (14 * width), y + ((i + 9) * height), YELLOW_MAIN, YELLOW_LIGHT);
        }
        // Blue path cells
        for (int i = 0; i < 6; i++) {
            fillCell(g, x + (i * width), y + (9 * height), BLUE_MAIN, BLUE_LIGHT);
            fillCell(g, x, y + ((i + 9) * height), BLUE_MAIN, BLUE_LIGHT);
            fillCell(g, x + (i * width), y + (14 * height), BLUE_MAIN, BLUE_LIGHT);
            fillCell(g, x + (5 * width), y + ((i + 9) * height), BLUE_MAIN, BLUE_LIGHT);
        }

        // Home run paths (center colored strips)
        for (int i = 1; i < 6; i++) {
            fillCell(g, x + (i * width), y + (7 * height), RED_MAIN, RED_LIGHT);
            fillCell(g, x + ((8 + i) * width), y + (7 * height), YELLOW_MAIN, YELLOW_LIGHT);
            fillCell(g, x + (7 * width), y + (i * height), GREEN_MAIN, GREEN_LIGHT);
            fillCell(g, x + (7 * width), y + ((8 + i) * height), BLUE_MAIN, BLUE_LIGHT);
        }

        // Safe zone entry cells
        fillCell(g, x + width, y + (6 * height), RED_MAIN, RED_LIGHT);
        fillCell(g, x + (13 * width), y + (8 * height), YELLOW_MAIN, YELLOW_LIGHT);
        fillCell(g, x + (8 * width), y + height, GREEN_MAIN, GREEN_LIGHT);
        fillCell(g, x + (6 * width), y + (13 * height), BLUE_MAIN, BLUE_LIGHT);

        // Draw pawn starting positions in home bases
        int temp1 = x + 45, temp2 = y + 45;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                drawPawnSlot(g, temp1 + (2 * i * width), temp2 + (2 * j * height), RED_MAIN);
                drawPawnSlot(g, temp1 + (2 * i * width) + 9 * width, temp2 + (2 * j * height) + 9 * height, YELLOW_MAIN);
                drawPawnSlot(g, temp1 + (2 * i * width) + 9 * width, temp2 + (2 * j * height), GREEN_MAIN);
                drawPawnSlot(g, temp1 + (2 * i * width), temp2 + (2 * j * height) + 9 * height, BLUE_MAIN);
            }
        }

        // Center triangles (finish area)
        drawCenterTriangles(g);

        // Grid lines
        g.setStroke(new BasicStroke(1));
        g.setColor(GRID_COLOR);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                g.drawRect(x + ((i + 6) * width), y + (j * height), width, height);
                g.drawRect(x + ((j) * width), y + ((i + 6) * height), width, height);
                g.drawRect(x + ((i + 6) * width), y + ((j + 9) * height), width, height);
                g.drawRect(x + ((j + 9) * width), y + ((i + 6) * height), width, height);
            }
        }

        // Home base borders
        g.setStroke(new BasicStroke(2));
        g.setColor(BOARD_BORDER);
        g.drawRoundRect(x + width, y + height, 4 * width, 4 * height, 6, 6);
        g.drawRoundRect(x + 10 * width, y + height, 4 * width, 4 * height, 6, 6);
        g.drawRoundRect(x + width, y + 10 * height, 4 * width, 4 * height, 6, 6);
        g.drawRoundRect(x + 10 * width, y + 10 * height, 4 * width, 4 * height, 6, 6);

        // Board outer border
        g.setStroke(new BasicStroke(3));
        g.setColor(BOARD_BORDER);
        g.drawRoundRect(x, y, 15 * width, 15 * height, 6, 6);

        // Pawn slot borders in home bases
        g.setStroke(new BasicStroke(2));
        g.setColor(BOARD_BORDER);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                g.drawRoundRect(temp1 + (2 * i * width), temp2 + (2 * j * height), width, height, 4, 4);
                g.drawRoundRect(temp1 + (2 * i * width) + 9 * width, temp2 + (2 * j * height) + 9 * height, width, height, 4, 4);
                g.drawRoundRect(temp1 + (2 * i * width) + 9 * width, temp2 + (2 * j * height), width, height, 4, 4);
                g.drawRoundRect(temp1 + (2 * i * width), temp2 + (2 * j * height) + 9 * height, width, height, 4, 4);
            }
        }

        // Safe zone markers (stars)
        drawStar(g, x + 5 + (6 * width), y + 5 + (2 * height), width - 10, height - 10);
        drawStar(g, x + 5 + (12 * width), y + 5 + (6 * height), width - 10, height - 10);
        drawStar(g, x + 5 + (8 * width), y + 5 + (12 * height), width - 10, height - 10);
        drawStar(g, x + 5 + (2 * width), y + 5 + (8 * height), width - 10, height - 10);
    }

    private void fillCell(Graphics2D g, int cx, int cy, Color main, Color light) {
        GradientPaint gp = new GradientPaint(cx, cy, light, cx + width, cy + height, main);
        g.setPaint(gp);
        g.fillRect(cx, cy, width, height);
    }

    private void drawHomeBase(Graphics2D g, int bx, int by, Color main, Color light) {
        GradientPaint gp = new GradientPaint(bx, by, light, bx + 4 * width, by + 4 * height, main);
        g.setPaint(gp);
        g.fillRoundRect(bx, by, 4 * width, 4 * height, 6, 6);
    }

    private void drawPawnSlot(Graphics2D g, int sx, int sy, Color c) {
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 180));
        g.fillRoundRect(sx, sy, width, height, 4, 4);
    }

    private void drawCenterTriangles(Graphics2D g) {
        // Red triangle (left)
        int[] xpoints0 = {x + (6 * width), x + (6 * width), x + 15 + (7 * width)};
        int[] ypoints0 = {y + (6 * height), y + (9 * height), y + 15 + (7 * width)};
        GradientPaint gp0 = new GradientPaint(x + (6 * width), y + (7 * height), RED_LIGHT, x + 15 + (7 * width), y + 15 + (7 * width), RED_MAIN);
        g.setPaint(gp0);
        g.fillPolygon(xpoints0, ypoints0, 3);

        // Yellow triangle (right)
        int[] xpoints1 = {x + (9 * width), x + (9 * width), x + 15 + (7 * width)};
        int[] ypoints1 = {y + (6 * height), y + (9 * height), y + 15 + (7 * width)};
        GradientPaint gp1 = new GradientPaint(x + (9 * width), y + (7 * height), YELLOW_LIGHT, x + 15 + (7 * width), y + 15 + (7 * width), YELLOW_MAIN);
        g.setPaint(gp1);
        g.fillPolygon(xpoints1, ypoints1, 3);

        // Green triangle (top)
        int[] xpoints2 = {x + (6 * width), x + (9 * width), x + 15 + (7 * width)};
        int[] ypoints2 = {y + (6 * height), y + (6 * height), y + 15 + (7 * width)};
        GradientPaint gp2 = new GradientPaint(x + (7 * width), y + (6 * height), GREEN_LIGHT, x + 15 + (7 * width), y + 15 + (7 * width), GREEN_MAIN);
        g.setPaint(gp2);
        g.fillPolygon(xpoints2, ypoints2, 3);

        // Blue triangle (bottom)
        int[] xpoints3 = {x + (6 * width), x + (9 * width), x + 15 + (7 * width)};
        int[] ypoints3 = {y + (9 * height), y + (9 * height), y + 15 + (7 * width)};
        GradientPaint gp3 = new GradientPaint(x + (7 * width), y + (9 * height), BLUE_LIGHT, x + 15 + (7 * width), y + 15 + (7 * width), BLUE_MAIN);
        g.setPaint(gp3);
        g.fillPolygon(xpoints3, ypoints3, 3);

        // Triangle borders
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(BOARD_BORDER);
        g.drawPolygon(xpoints0, ypoints0, 3);
        g.drawPolygon(xpoints1, ypoints1, 3);
        g.drawPolygon(xpoints2, ypoints2, 3);
        g.drawPolygon(xpoints3, ypoints3, 3);
    }

    private void drawStar(Graphics2D g, int sx, int sy, int w, int h) {
        g.setColor(new Color(60, 60, 60));
        g.setStroke(new BasicStroke(2));
        int cx = sx + w / 2, cy = sy + h / 2;
        int r = Math.min(w, h) / 2 - 1;
        // Simple star using lines
        g.drawLine(cx, cy - r, cx, cy + r);
        g.drawLine(cx - r, cy, cx + r, cy);
        g.drawLine(cx - r / 2, cy - r / 2, cx + r / 2, cy + r / 2);
        g.drawLine(cx + r / 2, cy - r / 2, cx - r / 2, cy + r / 2);
    }
}
