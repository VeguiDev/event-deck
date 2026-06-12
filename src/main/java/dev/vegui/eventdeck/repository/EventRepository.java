package dev.vegui.eventdeck.repository;

import dev.vegui.eventdeck.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    void save(Event event);
    Optional<Event> findById(UUID id);
    List<Event> findAll();
    List<Event> findLike(String query);
    void delete(UUID id);

}
