package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.components.EventForm;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.services.EventService;

import javax.swing.*;
import java.awt.*;

public class EventEditView extends View {
    private final EventService eventService;
    private Event event;

    // Components
    private JPanel navPanel;
    private JLabel title;

    public EventEditView() {

        this.eventService = Main.getService(EventService.class);

        setLayout(new BorderLayout());
        this.navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> {
            getMainState().getRouter().navigate(Routes.EVENT_DETAIL);
        });
        navPanel.add(backButton);
        title = new JLabel("Event");
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 18f)
        );
        navPanel.add(title);
        add(navPanel, BorderLayout.NORTH);
        reload();
    }

    @Override
    public void reload() {
        super.reload();

        if (getMainState().getCurrentEvent() == null) {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        this.event = getMainState().getCurrentEvent();
        String titleText = event.getTitle() + " - Editar";
        title.setText(titleText);

        EventForm form = new EventForm(this.eventService, this.event);
        JScrollPane scrollPane = new JScrollPane(form);
        add(scrollPane, BorderLayout.CENTER);
    }
}
