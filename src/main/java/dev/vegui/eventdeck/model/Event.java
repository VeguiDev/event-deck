package dev.vegui.eventdeck.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {

    private UUID id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private int duration;
    private EventLocation location;

    public Event() {
        this.location = new EventLocation();
    }

    public Event(UUID id, String title, String description, LocalDateTime startDate, int duration, EventLocation location){
        this(title, description, startDate, duration, location);
        this.id = id;
    }

    public Event(String title, String description, LocalDateTime startDate, int duration, EventLocation location) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.duration = duration;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public EventLocation getLocation() {
        return location;
    }

    public void setLocation(EventLocation location) {
        this.location = location;
    }
}
