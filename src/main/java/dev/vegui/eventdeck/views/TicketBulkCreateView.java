package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.services.TicketService;

import javax.swing.*;
import java.awt.*;

public class TicketBulkCreateView extends View {

    private final TicketService ticketService;

    private JLabel title;

    public TicketBulkCreateView() {
        this.ticketService = Main.getService(TicketService.class);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> navigateBack());
        navPanel.add(backButton);

        title = new JLabel("Crear nuevo ticket");
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 18f)
        );
        navPanel.add(title);
        add(navPanel, BorderLayout.NORTH);
    }

    private void navigateBack() {
        if (getMainState().getCurrentEvent() == null) {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        getMainState().getRouter().navigate(Routes.EVENT_DETAIL);
    }

}
