package dev.vegui.eventdeck.components;

import dev.vegui.eventdeck.model.Ticket;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AttendenceBar extends JPanel {

    public AttendenceBar(
            List<Ticket> tickets
    ) {

        final int total = tickets.size();
        int used = 0;
        int invalid = 0;
        int unused = 0;

        for (Ticket ticket : tickets) {
            if (ticket.getDeletedAt() != null) {
                invalid++;
                continue;
            }

            if (ticket.getAttendedAt() != null) {
                used++;
                continue;
            }

            if (ticket.getEvent().wasEnded()) {
                unused++;
            }
        }

        final float usedPercentage = (float) used / total;
        final float invalidPercentage = (float) invalid / total;
        final float unUsedPercentage = (float) unused / total;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Asistencia");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18));

        final int fullSize = 250;

        // Bar

        JPanel bars = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bars.setPreferredSize(new Dimension(fullSize, 15));
        bars.setMinimumSize(new Dimension(fullSize, 15));
        bars.setMaximumSize(new Dimension(fullSize, 15));
        bars.setBackground(Color.LIGHT_GRAY);

        bars.add(createBox(fullSize * usedPercentage, Color.BLUE));
        bars.add(createBox(fullSize * unUsedPercentage, Color.RED));
        bars.add(createBox(fullSize * invalidPercentage, Color.DARK_GRAY));

        add(title);
        add(bars);
        add(createCircleWithLabel("Asistencias " + used, Color.BLUE));
        add(createCircleWithLabel("Inasistencias " + unused, Color.RED));
        add(createCircleWithLabel("Invalidos " + invalid, Color.DARK_GRAY));
        add(createCircleWithLabel("No Registrado " + (total - (used + invalid + unused)), Color.LIGHT_GRAY));

    }

    private JPanel createBox(float width, Color color) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension((int) width, 15));
        panel.setMinimumSize(new Dimension((int) width, 15));
        panel.setMaximumSize(new Dimension((int) width, 15));
        panel.setBackground(color);
        return panel;
    }

    private JPanel createCircleWithLabel(String label, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        panel.add(new CircleComponent(5, 5, color));
        panel.add(new JLabel(label));

        return panel;
    }

    @Override
    public Component add(Component comp) {

        if (comp instanceof JComponent) {
            ((JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        return super.add(comp);
    }
}
