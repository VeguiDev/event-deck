package dev.vegui.eventdeck.repository;

import dev.vegui.eventdeck.model.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {
    void save(Ticket ticket);

    Optional<Ticket> findById(UUID id);

    Optional<Ticket> findByCode(String code);

    List<Ticket> findAll();

    List<Ticket> findByEvent(UUID event_id);

    void delete(UUID id);
}
