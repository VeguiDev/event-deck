package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.components.AttendenceBar;
import dev.vegui.eventdeck.components.DetailField;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.Ticket;
import dev.vegui.eventdeck.services.EventService;
import dev.vegui.eventdeck.services.TicketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class EventDetailsView extends View {
    private EventService eventService;
    private TicketService ticketService;
    private Event event;
    private List<Ticket> tickets;

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
        this.tickets = ticketService.findByEvent(event);

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

        detailsPanel.add(new AttendenceBar(tickets));
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

        if (tickets.isEmpty()) {
            ticketsPanel.add(new JLabel("No hay tickets creados"));
            ticketsPanel.setMaximumSize(new Dimension(
                    Integer.MAX_VALUE,
                    ticketsPanel.getPreferredSize().height
            ));
            return ticketsPanel;
        }

        String[] columnNames = {"Código", "Nombre", "Email", "Estado"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Ticket ticket : tickets) {
            model.addRow(new Object[]{
                    ticket.getCode(),
                    ticket.getAttendeeName(),
                    ticket.getAttendeeEmail(),
                    getTicketStatus(ticket)
            });
        }

        JTable ticketsTable = new JTable(model);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketsTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        scrollPane.setPreferredSize(new Dimension(700, 180));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        ticketsPanel.add(scrollPane);

        JButton detailsTicketButton = new JButton("Ver detalle");
        detailsTicketButton.addActionListener(e -> {
            int selectedRow = ticketsTable.getSelectedRow();

            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccioná un ticket para ver el detalle",
                        "Ticket no seleccionado",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int modelRow = ticketsTable.convertRowIndexToModel(selectedRow);
            Ticket selectedTicket = tickets.get(modelRow);
            selectedTicket.setEvent(event);
            getMainState().setCurrentTicket(selectedTicket);
            getMainState().getRouter().navigate(Routes.TICKET_DETAIL);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.add(detailsTicketButton);
        ticketsPanel.add(actions);

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
