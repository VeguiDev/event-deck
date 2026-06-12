package dev.vegui.eventdeck.repository;

import dev.vegui.eventdeck.model.Event;

import java.util.*;

public class InMemoryEventRepository implements EventRepository {

    private final Map<UUID, Event> events = new HashMap<>();


    @Override
    public void save(Event event) {
        events.put(event.getId(), event);
    }

    @Override
    public Optional<Event> findById(UUID id) {
        return Optional.ofNullable(events.get(id));
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(events.values());
    }

    @Override
    public List<Event> findLike(String query) {
        List<Event> result = new ArrayList<>();
        for (Event event : events.values()) {
            if (event.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                event.getDescription().toLowerCase().contains(query.toLowerCase())) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        events.remove(id);
    }
}