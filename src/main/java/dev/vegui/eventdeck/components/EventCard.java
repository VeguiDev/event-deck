package dev.vegui.eventdeck.components;

import dev.vegui.eventdeck.model.Event;

import javax.swing.*;
import java.awt.*;

public class EventCard extends RoundedPanel {

    private final Event event;

    public EventCard(Event event) {
        super(5);
        this.event = event;
        setPreferredSize(new Dimension(240, 140));

        setBorder(
                new RoundedBorder(5, Color.GRAY, 2)
        );

        JPanel panel = new JPanel();
        panel.setBorder(
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(event.getTitle());
        panel.add(title, BorderLayout.WEST);

        JLabel description = new JLabel(event.getDescription());
        panel.add(description, BorderLayout.EAST);

        JButton details = new JButton("details");
        panel.add(details, BorderLayout.SOUTH);

        add(panel);
    }


}
