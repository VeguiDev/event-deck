package dev.vegui.eventdeck.services;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.Ticket;
import dev.vegui.eventdeck.repository.TicketRepository;
import dev.vegui.eventdeck.util.TicketExporter;
import dev.vegui.eventdeck.util.Validators;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class TicketService {

    private TicketRepository ticketRepo;
    private final EmailService emailService;

    public TicketService(TicketRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
        this.emailService = Main.getService(EmailService.class);
    }

    public List<Ticket> findAll() {
        return ticketRepo.findAll();
    }

    public Optional<Ticket> findByCode(String code) {
        return ticketRepo.findByCode(code);
    }

    public List<Ticket> findByEvent(Event event) {
        return this.findByEvent(event.getId()).stream().peek(t -> t.setEvent(event)).toList();
    }

    public List<Ticket> findByEvent(UUID eventId) {
        return ticketRepo.findByEvent(eventId);
    }

    public Optional<Ticket> findById(UUID id) {
        return ticketRepo.findById(id);
    }

    private String generateValidCode(int tries) {

        if (tries >= 15) {
            throw new RuntimeException("Too many tries to generate a valid code");
        }

        String code = Ticket.generateAccessCode();

        if (findByCode(code).isEmpty()) {
            return code;
        }

        return generateValidCode(tries + 1);
    }

    public Ticket create(
            String attendeeName,
            String attendeeEmail,
            Event event
    ) {
        Validators.field("attendeeName", attendeeName).notEmpty().maxLength(255);
        Validators.field("attendeeEmail", attendeeEmail).notEmpty().maxLength(255);
        Validators.field("event", event).notEmpty();

        UUID id = UUID.randomUUID();
        Ticket ticket = new Ticket(
                id,
                generateValidCode(0),
                attendeeName,
                attendeeEmail,
                event
        );

        ticketRepo.save(ticket);

        if (Main.getAppConfig().isAutoSendTicket()) {
            this.notifyViaEmail(ticket, event);
        }

        return ticket;
    }

    public void update(Ticket ticket) {
        Validators.field("code", ticket.getCode()).notEmpty().maxLength(255);
        Validators.field("attendeeName", ticket.getAttendeeName()).notEmpty().maxLength(255);
        Validators.field("attendeeEmail", ticket.getAttendeeEmail()).notEmpty().maxLength(255);
        ticketRepo.save(ticket);
    }

    public void claim(Ticket ticket) {
        if (ticket.getDeletedAt() != null || ticket.getAttendedAt() != null) {
            return;
        }

        ticket.setAttendedAt(LocalDateTime.now());
        ticketRepo.save(ticket);
    }

    public void softDelete(Ticket ticket) {

        if (ticket.getDeletedAt() != null) {
            return;
        }

        ticket.setDeletedAt(LocalDateTime.now());
        ticketRepo.save(ticket);

    }

    public void notifyViaEmail(Ticket ticket, Event event) {
        if (!Main.getAppConfig().getSmtpConfig().enabled()) {
            Main.logger.log(Level.WARNING, "Email notification has been disabled, ignoring.");
            return;
        }
        
        try {
            String plainText = TicketExporter.exportToString(ticket, event);
            String htmlText = TicketExporter.exportToHTML(ticket, event);

            this.emailService.sendMail(
                    ticket.getAttendeeEmail(),
                    "EventDeck: Te han invitado a participar de este evento.",
                    plainText,
                    htmlText
            );

        } catch (Exception e1) {
            e1.printStackTrace();
            Main.logger.log(Level.SEVERE, "Error sending email ticket notification", e1);
        }
    }

}
