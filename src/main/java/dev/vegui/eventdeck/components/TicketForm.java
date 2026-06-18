package dev.vegui.eventdeck.components;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.exceptions.ValidationException;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.Ticket;
import dev.vegui.eventdeck.services.TicketService;

import javax.swing.*;
import java.awt.*;

public class TicketForm extends JPanel {

    private final TicketService ticketService;
    private final Event event;
    private Ticket ticket;
    private boolean isEditMode = false;

    private final JPanel wrapper = new JPanel();

    private JTextField code;
    private JTextField attendeeName;
    private JTextField attendeeEmail;

    public TicketForm(TicketService ticketService, Event event) {
        this.ticketService = ticketService;
        this.event = event;
        setupForm();
    }

    public TicketForm(TicketService ticketService, Ticket ticket, Event event) {
        this.ticketService = ticketService;
        this.ticket = ticket;
        this.event = event;
        this.isEditMode = ticket.getId() != null;
        setupForm();
        loadData();
    }

    private void setupForm() {
        wrapper.removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT));

        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        add(wrapper);

        if (event != null) {
            addWrapper(new JLabel("Evento"));
            addWrapper(new JLabel(event.getTitle()));
        }

        if (isEditMode) {
            addWrapper(new JLabel("Código"));
            code = new JTextField(20);
            addWrapper(code);
        }

        addWrapper(new JLabel("Nombre del asistente"));
        attendeeName = new JTextField(20);
        addWrapper(attendeeName);

        addWrapper(new JLabel("Email del asistente"));
        attendeeEmail = new JTextField(20);
        addWrapper(attendeeEmail);

        JButton submitButton = new JButton("Confirmar");
        submitButton.addActionListener(e -> onSubmit());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(submitButton);
        addWrapper(actions);
    }

    private void loadData() {
        if (ticket == null) return;

        if (code != null) {
            code.setText(valueOrEmpty(ticket.getCode()));
        }

        attendeeName.setText(valueOrEmpty(ticket.getAttendeeName()));
        attendeeEmail.setText(valueOrEmpty(ticket.getAttendeeEmail()));
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private void onSubmit() {
        try {
            if (isEditMode) {
                ticket.setCode(code.getText());
                ticket.setAttendeeName(attendeeName.getText());
                ticket.setAttendeeEmail(attendeeEmail.getText());

                if (event != null) {
                    ticket.setEvent(event);
                    ticket.setEventId(event.getId());
                }

                ticketService.update(ticket);
                Main.getState().getRouter().navigate(Routes.EVENT_DETAIL);
                return;
            }

            ticketService.create(
                    attendeeName.getText(),
                    attendeeEmail.getText(),
                    event
            );

            Main.getState().getRouter().navigate(Routes.EVENT_DETAIL);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addWrapper(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(component);
    }
}
