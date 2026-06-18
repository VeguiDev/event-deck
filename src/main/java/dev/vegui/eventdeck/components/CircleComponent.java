package dev.vegui.eventdeck.components;

import javax.swing.*;
import java.awt.*;

public class CircleComponent extends JComponent {

    private final int width;
    private final int height;
    private final Color color;

    public CircleComponent(int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;

        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        // Más suave
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(color);

        // Dibuja el círculo/óvalo ocupando todo el componente
        g2.fillOval(0, 0, width, height);

        g2.dispose();
    }
}