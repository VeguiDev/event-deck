package dev.vegui.eventdeck.components;

import javax.swing.border.Border;
import java.awt.*;

public class RoundedBorder implements Border {
    private final int radius;
    private final Color color;
    private final int thickness;

    public RoundedBorder(int radius, Color color, int thickness) {
        this.radius = radius;
        this.color = color;
        this.thickness = thickness;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(
            Component c,
            Graphics g,
            int x,
            int y,
            int width,
            int height
    ) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));

        int offset = thickness / 2;

        g2.drawRoundRect(
                x + offset,
                y + offset,
                width - thickness,
                height - thickness,
                radius,
                radius
        );

        g2.dispose();
    }
}
