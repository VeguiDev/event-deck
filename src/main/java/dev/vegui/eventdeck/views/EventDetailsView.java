package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.services.EventService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EventDetailsView extends View {
    private EventService eventService;
    private Event event;

    // Components
    private JPanel navPanel;
    private JLabel title;

    public EventDetailsView() {

        this.eventService = Main.getService();

        setLayout(new BorderLayout());
        this.navPanel = new JPanel(new BorderLayout());
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
        });
        leftNav.add(backButton);
        title = new JLabel("Event");
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 18f)
        );
        leftNav.add(title);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> {
            getMainState().getRouter().navigate(Routes.EVENT_EDIT);
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::onDelete);

        rightNav.add(editButton);
        rightNav.add(deleteButton);

        navPanel.add(leftNav, BorderLayout.WEST);
        navPanel.add(rightNav, BorderLayout.EAST);
        add(navPanel, BorderLayout.NORTH);
    }

    @Override
    public void reload() {
        super.reload();

        if (getMainState().getCurrentEvent() == null) {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        this.event = getMainState().getCurrentEvent();
        String titleText = event.getTitle() + " - Detalles";
        title.setText(titleText);
    }

    @Override
    public void onHide() {
        super.onHide();
        Main.getState().setCurrentEvent(null);
    }

    private void onDelete(ActionEvent e) {

        if (event == null) return;

        int option = JOptionPane.showConfirmDialog(
                this,
                "Estás seguro de que quieres eliminar este evento?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            this.eventService.delete(this.event);

            Main.getState().getRouter().navigate(Routes.EVENTS_LIST);
        }

    }
}
