package dev.vegui.eventdeck.components;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.exceptions.ValidationException;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.EventLocation;
import dev.vegui.eventdeck.services.EventService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class EventForm extends JPanel {

    private final EventService eventService;
    private Event event = new Event();
    private boolean isEditMode = false;

    private final JPanel wrapper = new JPanel();

    // Inputs
    private JTextField title;
    private JTextArea description;
    private JSpinner dateSpinner;
    private JSpinner durationSpinner;

    private JTextField venueName;
    private JTextField city;
    private JTextField province;
    private JTextField country;
    private JTextField street;

    private JButton submitButton;

    public EventForm(EventService eventService, Event event) {
        this.eventService = eventService;
        this.event = event;
        this.isEditMode = event.getId() != null;
        setupForm();
        loadData();
    }

    public EventForm(EventService eventService) {
        this.eventService = eventService;
        setupForm();
    }

    private void setupForm() {
        wrapper.removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT));

        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        add(wrapper);

        addWrapper(new JLabel("Titulo"));
        title = new JTextField(20);
        addWrapper(title);

        addWrapper(new JLabel("Descripcion"));

        description = new JTextArea(5, 20);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setPreferredSize(new Dimension(360, 90));
        addWrapper(description);

        dateSpinner = new JSpinner(
                new SpinnerDateModel()
        );

        JSpinner.DateEditor editor = new JSpinner.DateEditor(
                dateSpinner,
                "dd/MM/yyyy HH:mm"
        );

        dateSpinner.setEditor(editor);

        addWrapper(new JLabel("Día y hora de inicio"));
        addWrapper(dateSpinner);

        addWrapper(new JLabel("Duración (horas)"));

        durationSpinner = new JSpinner(
                new SpinnerNumberModel(
                        1,
                        0,
                        1000,
                        1
                )
        );

        addWrapper(durationSpinner);

        setupLocationForm();

        JPanel actions = new JPanel();
        actions.setLayout(new FlowLayout(FlowLayout.LEFT));

        submitButton = new JButton("Confirmar");
        submitButton.addActionListener(this::onSubmit);

        actions.add(submitButton);
        addWrapper(actions);
    }

    private void setupLocationForm() {
        JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
        locationPanel.add(new JLabel("Location"));

        venueName = new JTextField(20);
        locationPanel.add(new JLabel("Venue Name"));
        locationPanel.add(venueName);
        city = new JTextField(20);
        locationPanel.add(new JLabel("City"));
        locationPanel.add(city);
        province = new JTextField(20);
        locationPanel.add(new JLabel("Province"));
        locationPanel.add(province);
        country = new JTextField(20);
        locationPanel.add(new JLabel("Country"));
        locationPanel.add(country);
        street = new JTextField(20);
        locationPanel.add(new JLabel("Street"));
        locationPanel.add(street);
        addWrapper(locationPanel);
    }

    private void loadData() {
        title.setText(valueOrEmpty(event.getTitle()));
        description.setText(valueOrEmpty(event.getDescription()));

        LocalDateTime startDate = event.getStartDate();

        if (startDate != null) {
            Date spinnerDate = Date.from(
                    startDate
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
            );

            dateSpinner.setValue(spinnerDate);
        }

        durationSpinner.setValue(event.getDuration());

        EventLocation location = event.getLocation();

        if (location == null) {
            location = new EventLocation();
        }

        venueName.setText(valueOrEmpty(location.getVenueName()));
        city.setText(valueOrEmpty(location.getCity()));
        province.setText(valueOrEmpty(location.getProvince()));
        country.setText(valueOrEmpty(location.getCountry()));
        street.setText(valueOrEmpty(location.getStreet()));
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private void onSubmit(
            ActionEvent e
    ) {
        event.setTitle(title.getText());
        event.setDescription(description.getText());

        Date selectedDate = (Date) dateSpinner.getValue();

        LocalDateTime startDate = selectedDate
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        event.setStartDate(startDate);
        event.setDuration((Integer) durationSpinner.getValue());

        // Location
        EventLocation location = new EventLocation();
        location.setVenueName(venueName.getText());
        location.setCity(city.getText());
        location.setProvince(province.getText());
        location.setCountry(country.getText());
        location.setStreet(street.getText());
        event.setLocation(location);

        try {
            if (isEditMode) {
                this.eventService.update(
                        event
                );
                Main.getState().getRouter().navigate(Routes.EVENTS_LIST);
                return;
            }

            this.eventService.create(
                    event.getTitle(),
                    event.getDescription(),
                    event.getStartDate(),
                    event.getDuration(),
                    event.getLocation()
            );

            Main.getState().getRouter().navigate(Routes.EVENTS_LIST);
        } catch (ValidationException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );

        }

    }

    private void addWrapper(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(component);
    }

}
