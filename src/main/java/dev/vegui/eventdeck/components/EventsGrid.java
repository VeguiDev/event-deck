package dev.vegui.eventdeck.components;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.services.EventService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EventsGrid extends JPanel {

    private List<Event> events;
    private final EventService eventService;

    public EventsGrid() {
        this.events = new ArrayList<>();
        this.eventService = Main.getService(EventService.class);
        setLayout(
                new GridLayout(0, 3, 16, 16)
        );
        setBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
        reload();
    }

    public void reload() {
        this.events = this.eventService.findAll();
        updateEventCards();
    }

    private void updateEventCards() {

        this.removeAll();
        for (Event event : events) {
            JPanel wrapper = new JPanel();
            EventCard card = new EventCard(event);
            wrapper.add(card);
            this.add(wrapper);
        }

    }

}
