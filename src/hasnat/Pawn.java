package hasnat;

import java.awt.*;

public class Pawn {
    int x, y;
    int current;
    int height, width;

    // Modern colors matching Layout
    private static final Color[] PLAYER_COLORS = {
            new Color(220, 50, 50),   // Red
            new Color(40, 180, 60),   // Green
            new Color(230, 200, 30),  // Yellow
            new Color(50, 80, 220)    // Blue
    };
    private static final Color[] PLAYER_LIGHT = {
            new Color(255, 120, 120),
            new Color(100, 220, 120),
            new Color(255, 240, 100),
            new Color(120, 150, 255)
    };

    public Pawn(int h, int w) {
        current = -1;
        x = -1;
        y = -1;
        height = h;
        width = w;
    }

    public void draw(Graphics2D g, int i, int j, int play) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int drawX, drawY;

        if (current == -1) {
            int temp1 = 80 + (height / 2), temp2 = 50 + (width / 2);
            x = i;
            y = j;
            drawX = temp1 + 5 + (i * width);
            drawY = temp2 + 5 + (j * height);
        } else {
            x = Path.ax[play][current];
            y = Path.ay[play][current];
            drawX = 80 + 5 + (x * width);
            drawY = 50 + 5 + (y * height);
        }

        int pawnSize = width - 10;

        // Drop shadow
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(drawX + 2, drawY + 3, pawnSize, pawnSize);

        // Pawn body with radial gradient
        Color main = PLAYER_COLORS[play];
        Color light = PLAYER_LIGHT[play];
        RadialGradientPaint rgp = new RadialGradientPaint(
                drawX + pawnSize / 2f - 3, drawY + pawnSize / 2f - 3, pawnSize / 2f,
                new float[]{0f, 0.6f, 1f},
                new Color[]{light, main, main.darker()}
        );
        g.setPaint(rgp);
        g.fillOval(drawX, drawY, pawnSize, pawnSize);

        // Highlight shine
        g.setColor(new Color(255, 255, 255, 70));
        g.fillOval(drawX + 3, drawY + 2, pawnSize / 2 - 2, pawnSize / 2 - 2);

        // Border
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(new Color(0, 0, 0, 100));
        g.drawOval(drawX, drawY, pawnSize, pawnSize);
    }
}
