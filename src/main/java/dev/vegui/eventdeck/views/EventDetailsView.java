package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.components.DetailField;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.Ticket;
import dev.vegui.eventdeck.services.EventService;
import dev.vegui.eventdeck.services.TicketService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class EventDetailsView extends View {
    private EventService eventService;
    private TicketService ticketService;
    private Event event;

    // Components
    private JPanel navPanel;
    private JLabel title;

    private JPanel wrapperPanel;

    public EventDetailsView() {

        this.eventService = Main.getService(EventService.class);
        this.ticketService = Main.getService(TicketService.class);

        setLayout(new BorderLayout());
        this.navPanel = new JPanel(new BorderLayout());
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
        });
        leftNav.add(backButton);
        title = new JLabel("Event");
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 18f)
        );
        leftNav.add(title);

        JButton editButton = new JButton("Editar");
        editButton.addActionListener(e -> {
            getMainState().getRouter().navigate(Routes.EVENT_EDIT);
        });
        JButton createTicketButton = new JButton("Crear ticket");
        createTicketButton.addActionListener(e -> {
            getMainState().setCurrentTicket(null);
            getMainState().getRouter().navigate(Routes.TICKET_CREATE);
        });
        JButton deleteButton = new JButton("Borrar");
        deleteButton.addActionListener(this::onDelete);

        rightNav.add(editButton);
        rightNav.add(createTicketButton);
        rightNav.add(deleteButton);

        navPanel.add(leftNav, BorderLayout.WEST);
        navPanel.add(rightNav, BorderLayout.EAST);
        add(navPanel, BorderLayout.NORTH);

        wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(wrapperPanel, BorderLayout.CENTER);
        reload();
    }

    @Override
    public void reload() {
        super.reload();
        this.wrapperPanel.removeAll();

        if (getMainState().getCurrentEvent() == null) {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        this.event = getMainState().getCurrentEvent();

        String titleText = event.getTitle() + " - Detalles";
        title.setText(titleText);

        final int detailsGap = 20;

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Resumen"));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Details
        detailsPanel.add(new DetailField("Titulo", event.getTitle()));
        detailsPanel.add(Box.createVerticalStrut(detailsGap));
        detailsPanel.add(new DetailField("Descripción", event.getDescription()));
        detailsPanel.add(Box.createVerticalStrut(detailsGap));

        JPanel shortDetails = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        shortDetails.setOpaque(false);
        shortDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
        shortDetails.add(new DetailField("Inicia el", event.getFormattedStartDate()));
        shortDetails.add(Box.createHorizontalStrut(detailsGap));
        shortDetails.add(new DetailField("Duración", event.getDuration() + "hrs"));
        shortDetails.add(Box.createHorizontalStrut(detailsGap));

        if (event.wasEnded()) {
            shortDetails.add(new DetailField("Estado", "HA TERMINADO"));
        } else if (event.wasStarted()) {
            shortDetails.add(new DetailField("Estado", "HA INICIADO"));
        } else {
            shortDetails.add(new DetailField("Estado", "NO HA INICIADO"));
        }

        shortDetails.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                shortDetails.getPreferredSize().height
        ));

        detailsPanel.add(shortDetails);

        detailsPanel.add(Box.createVerticalStrut(detailsGap));

        detailsPanel.add(new DetailField("Dirección", event.getLocation().getFormattedAddress()));

        detailsPanel.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                detailsPanel.getPreferredSize().height
        ));

        wrapperPanel.add(detailsPanel);
        wrapperPanel.add(Box.createVerticalStrut(detailsGap));
        wrapperPanel.add(buildTicketsPanel());
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    private JPanel buildTicketsPanel() {
        JPanel ticketsPanel = new JPanel();
        ticketsPanel.setLayout(new BoxLayout(ticketsPanel, BoxLayout.Y_AXIS));
        ticketsPanel.setBorder(BorderFactory.createTitledBorder("Tickets"));
        ticketsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<Ticket> tickets = ticketService.findByEvent(event);

        if (tickets.isEmpty()) {
            ticketsPanel.add(new JLabel("No hay tickets creados"));
        }

        for (Ticket ticket : tickets) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            row.add(new JLabel(ticket.getCode()));
            row.add(Box.createHorizontalStrut(12));
            row.add(new JLabel(ticket.getAttendeeName()));
            row.add(Box.createHorizontalStrut(12));
            row.add(new JLabel(ticket.getAttendeeEmail()));
            row.add(Box.createHorizontalStrut(12));
            row.add(new JLabel(getTicketStatus(ticket)));

            JButton editTicketButton = new JButton("Editar");
            editTicketButton.addActionListener(e -> {
                ticket.setEvent(event);
                getMainState().setCurrentTicket(ticket);
                getMainState().getRouter().navigate(Routes.TICKET_EDIT);
            });
            row.add(editTicketButton);

            ticketsPanel.add(row);
        }

        ticketsPanel.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                ticketsPanel.getPreferredSize().height
        ));

        return ticketsPanel;
    }

    private String getTicketStatus(Ticket ticket) {
        if (ticket.getDeletedAt() != null) {
            return "Eliminado";
        }

        if (ticket.getAttendedAt() != null) {
            return "Asistió";
        }

        return "Pendiente";
    }

    private void onDelete(ActionEvent e) {

        if (event == null) return;

        int option = JOptionPane.showConfirmDialog(
                this,
                "Estás seguro de que quieres eliminar este evento?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            this.eventService.delete(this.event);

            Main.getState().getRouter().navigate(Routes.EVENTS_LIST);
        }

    }
}
