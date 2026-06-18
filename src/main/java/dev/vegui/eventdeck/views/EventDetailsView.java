package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.components.DetailField;
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

    private JPanel wrapperPanel;

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

        JButton editButton = new JButton("Editar");
        editButton.addActionListener(e -> {
            getMainState().getRouter().navigate(Routes.EVENT_EDIT);
        });
        JButton deleteButton = new JButton("Borrar");
        deleteButton.addActionListener(this::onDelete);

        rightNav.add(editButton);
        rightNav.add(deleteButton);

        navPanel.add(leftNav, BorderLayout.WEST);
        navPanel.add(rightNav, BorderLayout.EAST);
        add(navPanel, BorderLayout.NORTH);

        wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(wrapperPanel, BorderLayout.CENTER);
        reload();
    }

    @Override
    public void reload() {
        super.reload();
        this.wrapperPanel.removeAll();

        if (getMainState().getCurrentEvent() == null) {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        this.event = getMainState().getCurrentEvent();

        String titleText = event.getTitle() + " - Detalles";
        title.setText(titleText);

        final int detailsGap = 20;

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Resumen"));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Details
        detailsPanel.add(new DetailField("Titulo", event.getTitle()));
        detailsPanel.add(Box.createVerticalStrut(detailsGap));
        detailsPanel.add(new DetailField("Descripción", event.getDescription()));
        detailsPanel.add(Box.createVerticalStrut(detailsGap));

        JPanel shortDetails = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        shortDetails.setOpaque(false);
        shortDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
        shortDetails.add(new DetailField("Inicia el", event.getFormattedStartDate()));
        shortDetails.add(Box.createHorizontalStrut(detailsGap));
        shortDetails.add(new DetailField("Duración", event.getDuration() + "hrs"));
        shortDetails.add(Box.createHorizontalStrut(detailsGap));

        if (event.wasEnded()) {
            shortDetails.add(new DetailField("Estado", "HA TERMINADO"));
        } else if (event.wasStarted()) {
            shortDetails.add(new DetailField("Estado", "HA INICIADO"));
        } else {
            shortDetails.add(new DetailField("Estado", "NO HA INICIADO"));
        }

        shortDetails.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                shortDetails.getPreferredSize().height
        ));

        detailsPanel.add(shortDetails);

        detailsPanel.add(Box.createVerticalStrut(detailsGap));

        detailsPanel.add(new DetailField("Dirección", event.getLocation().getFormattedAddress()));

        detailsPanel.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                detailsPanel.getPreferredSize().height
        ));

        wrapperPanel.add(detailsPanel, BorderLayout.CENTER);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
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
