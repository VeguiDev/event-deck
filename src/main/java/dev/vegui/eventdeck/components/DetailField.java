package dev.vegui.eventdeck.components;

import javax.swing.*;
import java.awt.*;

public class DetailField extends JPanel {
    public DetailField(String field, String value) {
        this(field, value, false);
    }

    public DetailField(String field, String value, boolean inline) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panel = new JPanel();
        if (inline) {
            panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        } else {
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        }

        JLabel label = new JLabel(field);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(valueLabel);

        add(panel, BorderLayout.WEST);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));

    }

}
