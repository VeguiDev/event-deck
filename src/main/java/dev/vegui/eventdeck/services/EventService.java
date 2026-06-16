package dev.vegui.eventdeck.services;

import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.EventLocation;
import dev.vegui.eventdeck.repository.EventRepository;
import dev.vegui.eventdeck.util.Validators;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public List<Event> findLike(String query) {
        return eventRepository.findLike(query);
    }

    public Optional<Event> findById(UUID id) {
        return eventRepository.findById(id);
    }

    public void create(
            String title,
            String description,
            LocalDateTime startDate,
            int duration,
            EventLocation location
    ) {
        Validators.field("title", title).notEmpty().maxLength(255);
        Validators.field("description", description).notEmpty().maxLength(255);
        this.validateEventLocation(location);

        Event event = new Event(title, description, startDate, duration, location);
        eventRepository.save(event);
    }

    public void update(Event event) {
        Validators.field("title", event.getTitle()).notEmpty().maxLength(255);
        Validators.field("description", event.getDescription()).notEmpty().maxLength(255);
        validateEventLocation(event.getLocation());
        eventRepository.save(event);
    }

    public void delete(Event event) {
        this.delete(event.getId());
    }

    public void delete(UUID id) {
        eventRepository.delete(id);
    }

    public void validateEventLocation(
            EventLocation location
    ) {
        Validators.field("venueName", location.getVenueName()).notEmpty().maxLength(255);
        Validators.field("country", location.getCountry()).notEmpty().maxLength(255);
        Validators.field("province", location.getProvince()).notEmpty().maxLength(255);
        Validators.field("city", location.getCity()).notEmpty().maxLength(255);
        Validators.field("street", location.getStreet()).notEmpty().maxLength(255);
    }
}
