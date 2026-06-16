package dev.vegui.eventdeck.services;

import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.Ticket;
import dev.vegui.eventdeck.repository.TicketRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TicketService {

    private TicketRepository ticketRepo;

    public TicketService(TicketRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    public List<Ticket> findAll() {
        return ticketRepo.findAll();
    }

    public Optional<Ticket> findByCode(String code) {
        return ticketRepo.findByCode(code);
    }

    public List<Ticket> findByEvent(Event event) {
        return this.findByEvent(event.getId());
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

        if (findByCode(code) == null) {
            return code;
        }

        return generateValidCode(tries++);
    }

    public Ticket create(
            String attendeeName,
            String attendeeEmail,
            Event event
    ) {
        UUID id = UUID.randomUUID();
        Ticket ticket = new Ticket(
                id,
                generateValidCode(0),
                attendeeName,
                attendeeEmail,
                event
        );

        ticketRepo.save(ticket);
        return ticket;
    }

    public void update(Ticket ticket) {
        ticketRepo.save(ticket);
    }

    public void delete(Ticket ticket) {
        delete(ticket.getId());
    }

    public void delete(UUID id) {
        ticketRepo.delete(id);
    }

}
