package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.components.TicketForm;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.Ticket;
import dev.vegui.eventdeck.services.TicketService;

import javax.swing.*;
import java.awt.*;

public class TicketEditView extends View {
    private final TicketService ticketService;

    private JLabel title;
    private JScrollPane scrollPane;

    public TicketEditView() {
        this.ticketService = Main.getService(TicketService.class);

        setLayout(new BorderLayout());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> navigateBack());
        navPanel.add(backButton);

        title = new JLabel("Editar ticket");
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 18f)
        );
        navPanel.add(title);
        add(navPanel, BorderLayout.NORTH);
    }

    @Override
    public void reload() {
        super.reload();

        Ticket ticket = getMainState().getCurrentTicket();
        Event event = getMainState().getCurrentEvent();

        if (ticket == null) {
            navigateBack();
            return;
        }

        title.setText(ticket.getCode() + " - Editar");

        if (scrollPane != null) {
            remove(scrollPane);
        }

        TicketForm form = new TicketForm(this.ticketService, ticket, event);
        scrollPane = new JScrollPane(form);
        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void navigateBack() {
        if (getMainState().getCurrentTicket() != null) {
            getMainState().getRouter().navigate(Routes.TICKET_DETAIL);
            return;
        }

        if (getMainState().getCurrentEvent() == null) {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        getMainState().getRouter().navigate(Routes.EVENT_DETAIL);
    }
}
