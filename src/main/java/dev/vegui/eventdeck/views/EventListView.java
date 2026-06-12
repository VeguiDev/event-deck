package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.components.EventListHeader;
import dev.vegui.eventdeck.components.EventsGrid;

import javax.swing.*;
import java.awt.*;

public class EventListView extends View {
    private final EventListHeader header;
    private final EventsGrid eventsGrid;

    public EventListView() {

        setLayout(new BorderLayout());

        header = new EventListHeader();
        add(header, BorderLayout.NORTH);

        eventsGrid = new EventsGrid();
        JScrollPane scrollPane = new JScrollPane(eventsGrid);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void reload() {
        super.reload();
        eventsGrid.reload();
    }
}
