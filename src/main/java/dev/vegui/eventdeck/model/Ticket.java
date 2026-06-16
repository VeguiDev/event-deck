package dev.vegui.eventdeck.model;

import com.beust.jcommander.internal.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket {

    private UUID id;
    private String code;

    private String attendeeName;
    private String attendeeEmail;

    private UUID eventId;

    @Nullable
    private Event event;

    private LocalDateTime createdAt;
    private LocalDateTime attendedAt;
    private LocalDateTime deletedAt;

    public Ticket() {
    }

    public Ticket(UUID id, String code, String attendeeName, String attendeeEmail) {
        this(id, code, attendeeName, attendeeEmail, null, null, LocalDateTime.now(), null, null);
    }

    public Ticket(UUID id, String code, String attendeeName, String attendeeEmail, UUID eventId) {
        this(id, code, attendeeName, attendeeEmail, eventId, null, LocalDateTime.now(), null, null);
    }

    public Ticket(UUID id, String code, String attendeeName, String attendeeEmail, Event event) {
        this(id, code, attendeeName, attendeeEmail, event.getId(), event, LocalDateTime.now(), null, null);
    }

    public Ticket(UUID id, String code, String attendeeName, String attendeeEmail, UUID event_id, Event event, LocalDateTime createdAt, LocalDateTime attendedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.code = code;
        this.attendeeName = attendeeName;
        this.attendeeEmail = attendeeEmail;
        this.eventId = event_id;
        this.event = event;
        this.createdAt = createdAt;
        this.attendedAt = attendedAt;
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public void setAttendeeName(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    public String getAttendeeEmail() {
        return attendeeEmail;
    }

    public void setAttendeeEmail(String attendeeEmail) {
        this.attendeeEmail = attendeeEmail;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getAttendedAt() {
        return attendedAt;
    }

    public void setAttendedAt(LocalDateTime attendedAt) {
        this.attendedAt = attendedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public static String generateAccessCode() {
        final String CHARACTERS = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
        final int codeLength = 12;

        final StringBuilder builder = new StringBuilder(codeLength);

        for (int i = 0; i < codeLength; i++) {

            int randomIndex = (int) (Math.random() * CHARACTERS.length());
            char digit = CHARACTERS.charAt(randomIndex);
            builder.append(digit);

            if (i % 3 == 2 && i != codeLength - 1) {
                builder.append("-");
            }

        }

        return builder.toString();
    }
}
