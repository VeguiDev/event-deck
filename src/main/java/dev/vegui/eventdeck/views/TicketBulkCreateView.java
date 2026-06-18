package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.services.TicketService;
import dev.vegui.eventdeck.util.Validators;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TicketBulkCreateView extends View {

    private final TicketService ticketService;

    private Event event;

    private JLabel title;
    private JPanel contentPanel;

    private JSpinner nameColumn;
    private JSpinner emailColumn;
    private JCheckBox ignoreFristRowCheckBox;

    private JFileChooser csvFileChooser;
    private JButton createButton;

    public TicketBulkCreateView() {
        this.ticketService = Main.getService(TicketService.class);
        setLayout(new BorderLayout());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> navigateBack());
        navPanel.add(backButton);

        title = new JLabel("Crear tickets en lote");
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 18f)
        );
        navPanel.add(title);
        add(navPanel, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        wrapper.add(contentPanel);
        add(wrapper, BorderLayout.CENTER);
    }

    @Override
    public void reload() {
        super.reload();

        if (Main.getState().getCurrentEvent() == null) {
            Main.getState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        this.event = Main.getState().getCurrentEvent();

        final int contentGap = 20;

        contentPanel.removeAll();

        addContent(Box.createVerticalStrut(contentGap));

        JLabel instructions = new JLabel("""
                <html>
                Para poder crear tickets en lote necesita un archivo de hojas de calculo en formato .csv.
                <br /> 
                Necesita especificar que columna de la tabla va a ser el nombre y cual el email del asistente.
                </html>
                """);
        addContent(instructions);

        addContent(Box.createVerticalStrut(contentGap));
        addContent(Box.createVerticalStrut(contentGap));

        JLabel titleLabel = new JLabel("Columna del nombre");
        titleLabel.setFont(
                titleLabel.getFont().deriveFont(Font.BOLD, 18f)
        );
        addContent(titleLabel);
        addContent(Box.createVerticalStrut(contentGap));

        nameColumn = new JSpinner(new SpinnerNumberModel(
                1,
                0,
                1000,
                1
        ));

        addContent(nameColumn);
        addContent(Box.createVerticalStrut(contentGap));

        JLabel emailLabel = new JLabel("Email del nombre");
        emailLabel.setFont(
                emailLabel.getFont().deriveFont(Font.BOLD, 18f)
        );
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addContent(emailLabel);
        addContent(Box.createVerticalStrut(contentGap));

        emailColumn = new JSpinner(new SpinnerNumberModel(
                2,
                0,
                1000,
                1
        ));
        addContent(emailColumn);
        addContent(Box.createVerticalStrut(contentGap));

        ignoreFristRowCheckBox = new JCheckBox("Ignorar primera fila", true);

        addContent(ignoreFristRowCheckBox);
        addContent(Box.createVerticalStrut(contentGap));

        JButton openButton = new JButton("Selecionar hoja de calculo");
        openButton.addActionListener(this::openCSVChooser);

        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter(
                "Hojas de Calculo CSV",
                "csv"
        );


        csvFileChooser = new JFileChooser();
        csvFileChooser.setFileFilter(csvFilter);
        csvFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        csvFileChooser.setMultiSelectionEnabled(false);
        csvFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

        addContent(openButton);

        createButton = new JButton("Crear");
        createButton.addActionListener(this::onCreate);
        refreshButtonStatus();
        addContent(createButton);
    }

    private void openCSVChooser(ActionEvent e) {
        int result = this.csvFileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            refreshButtonStatus();
        }
    }

    private void onCreate(ActionEvent e) {

        File file = csvFileChooser.getSelectedFile();

        if (!file.exists() || file == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "El archivo no existe o no fue seleccionado correctamente.",
                    "Archivo Inexistente",
                    JOptionPane.ERROR_MESSAGE
            );

        }

        final int nameIndex = (int) nameColumn.getValue();
        final int emailIndex = (int) emailColumn.getValue();

        Path filePath = file.toPath();
        List<String[]> tickets = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;

            int index = 0;

            while ((line = reader.readLine()) != null) {

                if (index == 0 && ignoreFristRowCheckBox.isSelected()) {
                    index++;
                    continue;
                }

                String[] fields = line.split(",");

                String nombre = fields[nameIndex - 1];
                String email = fields[emailIndex - 1];

                Validators.field("Nombre fila #" + index + 1, nombre).personName().maxLength(56).notEmpty();
                Validators.field("Email fila #" + index + 1, email).email().maxLength(120).notEmpty();

                String[] ticket = {
                        nombre,
                        email
                };

                tickets.add(ticket);

                index++;
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error de lectura",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        for (String[] ticket : tickets) {

            ticketService.create(ticket[0], ticket[1], this.event);

        }

        JOptionPane.showMessageDialog(
                this,
                "Se han creado " + tickets.size() + " tickets de forma exitosa!",
                "Creación exitosa",
                JOptionPane.INFORMATION_MESSAGE
        );

        Main.getState().getRouter().navigate(Routes.EVENT_DETAIL);

    }

    private void refreshButtonStatus() {
        createButton.setEnabled(this.csvFileChooser.getSelectedFile() != null);
    }

    private void addContent(Component component) {

        if (component instanceof JComponent component1) {
            component1.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        contentPanel.add(component);
    }

    private void navigateBack() {
        if (getMainState().getCurrentEvent() == null) {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        getMainState().getRouter().navigate(Routes.EVENT_DETAIL);
    }

}
