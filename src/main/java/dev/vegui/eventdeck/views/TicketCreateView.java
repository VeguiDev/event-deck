package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.components.TicketForm;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.services.TicketService;

import javax.swing.*;
import java.awt.*;

public class TicketCreateView extends View {
    private final TicketService ticketService;

    private JLabel title;
    private JScrollPane scrollPane;

    public TicketCreateView() {
        this.ticketService = Main.getService(TicketService.class);

        setLayout(new BorderLayout());

        JPanel haederPanel = new JPanel(new BorderLayout());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> navigateBack());
        navPanel.add(backButton);

        title = new JLabel("Crear nuevo ticket");
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 18f)
        );
        navPanel.add(title);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton bulkButton = new JButton("Crear lote");
        bulkButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        bulkButton.addActionListener(e -> {
            Main.getState().getRouter().navigate(Routes.TICKET_CREATE_BULK);
        });
        actionsPanel.add(bulkButton);

        haederPanel.add(navPanel, BorderLayout.WEST);
        haederPanel.add(actionsPanel, BorderLayout.EAST);
        add(haederPanel, BorderLayout.NORTH);
    }

    @Override
    public void reload() {
        super.reload();

        Event event = getMainState().getCurrentEvent();

        if (event == null) {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        title.setText(event.getTitle() + " - Crear ticket");

        if (scrollPane != null) {
            remove(scrollPane);
        }

        TicketForm form = new TicketForm(this.ticketService, event);
        scrollPane = new JScrollPane(form);
        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void navigateBack() {
        if (getMainState().getCurrentEvent() == null) {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        getMainState().getRouter().navigate(Routes.EVENT_DETAIL);
    }
}
