package dev.vegui.eventdeck.components;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.util.Util;

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

        JLabel title = new JLabel(Util.truncateWithEllipsis(event.getTitle(), 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 14f)
        );
        panel.add(title);

        JTextArea description = new JTextArea(
                Util.truncateWithEllipsis(event.getDescription(), 46)
        );
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setOpaque(false);
        description.setFocusable(false);
        description.setBackground(new Color(0, 0, 0, 0));
        description.setBorder(null);

        description.setPreferredSize(new Dimension(180, 45));

        panel.add(description);

        JButton details = new JButton("details");
        details.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.addActionListener((e) -> {
            Main.getState().setCurrentEvent(this.event);
            Main.getState().getRouter().navigate(Routes.EVENT_DETAIL);
        });

        panel.add(details);
        add(panel);
    }


}
