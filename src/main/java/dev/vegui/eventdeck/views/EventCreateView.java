package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.components.EventForm;
import dev.vegui.eventdeck.services.EventService;

import javax.swing.*;
import java.awt.*;

public class EventCreateView extends View {
    private final EventService eventService;

    public EventCreateView() {
        this.eventService = Main.getService(EventService.class);

        setLayout(new BorderLayout());
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
        });
        navPanel.add(backButton);
        JLabel label = new JLabel("Crear nuevo evento");
        label.setFont(
                label.getFont().deriveFont(Font.BOLD, 18f)
        );
        navPanel.add(label);
        add(navPanel, BorderLayout.NORTH);
        EventForm form = new EventForm(this.eventService);
        JScrollPane scrollPane = new JScrollPane(form);
        add(scrollPane, BorderLayout.CENTER);
    }

}
