package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.components.DetailField;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.Ticket;
import dev.vegui.eventdeck.services.TicketService;
import dev.vegui.eventdeck.util.QRUtils;
import dev.vegui.eventdeck.util.TicketExporter;
import dev.vegui.eventdeck.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketDetailsView extends View {
    private final TicketService ticketService;
    private Ticket ticket;

    private JLabel title;
    private JPanel wrapperPanel;
    private JButton invalidateButton;

    public TicketDetailsView() {
        this.ticketService = Main.getService(TicketService.class);

        setLayout(new BorderLayout());

        JPanel navPanel = new JPanel(new BorderLayout());
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> navigateBack());
        leftNav.add(backButton);

        title = new JLabel("Ticket");
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 18f)
        );
        leftNav.add(title);

        JButton editButton = new JButton("Editar");
        editButton.addActionListener(e -> {
            getMainState().getRouter().navigate(Routes.TICKET_EDIT);
        });

        invalidateButton = new JButton("Invalidar");
        invalidateButton.addActionListener(this::onInvalidate);

        rightNav.add(editButton);
        rightNav.add(invalidateButton);

        navPanel.add(leftNav, BorderLayout.WEST);
        navPanel.add(rightNav, BorderLayout.EAST);
        add(navPanel, BorderLayout.NORTH);

        wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(wrapperPanel, BorderLayout.CENTER);
    }

    @Override
    public void reload() {
        super.reload();
        wrapperPanel.removeAll();

        ticket = getMainState().getCurrentTicket();

        if (ticket == null) {
            navigateBack();
            return;
        }

        title.setText(ticket.getCode() + " - Detalles");
        invalidateButton.setEnabled(ticket.getDeletedAt() == null);

        final int detailsGap = 20;
        JPanel contentPanel = new JPanel(new BorderLayout(24, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Resumen"));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(new DetailField("Nombre del asistente", valueOrEmpty(ticket.getAttendeeName())));
        detailsPanel.add(Box.createVerticalStrut(detailsGap));
        detailsPanel.add(new DetailField("Email del asistente", valueOrEmpty(ticket.getAttendeeEmail())));
        detailsPanel.add(Box.createVerticalStrut(detailsGap));
        detailsPanel.add(new DetailField("Evento", getEventLabel()));
        detailsPanel.add(Box.createVerticalStrut(detailsGap));

        JPanel datesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datesPanel.setOpaque(false);
        datesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        datesPanel.add(new DetailField("Creado", formatDate(ticket.getCreatedAt())));
        datesPanel.add(Box.createHorizontalStrut(detailsGap));
        datesPanel.add(new DetailField("Asistencia", formatDate(ticket.getAttendedAt())));
        datesPanel.add(Box.createHorizontalStrut(detailsGap));
        datesPanel.add(new DetailField("Invalidado", formatDate(ticket.getDeletedAt())));
        datesPanel.add(Box.createHorizontalStrut(detailsGap));
        datesPanel.add(new DetailField("Estado", getTicketStatus()));

        datesPanel.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                datesPanel.getPreferredSize().height
        ));

        detailsPanel.add(datesPanel);
        detailsPanel.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                detailsPanel.getPreferredSize().height
        ));

        contentPanel.add(detailsPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Acceso"),
                BorderFactory.createEmptyBorder(14, 18, 18, 18)
        ));
        rightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        BufferedImage qr = QRUtils.generateQR("ticket:" + ticket.getCode(), 180);
        JLabel qrLabel = new JLabel(new ImageIcon(qr));
        qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel codeLabel = new JLabel(ticket.getCode());
        codeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        codeLabel.setFont(codeLabel.getFont().deriveFont(Font.BOLD, 20));

        JButton exportButton = new JButton("Exportar PDF");
        exportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportButton.addActionListener(this::onExportPdf);

        JButton emailButton = new JButton("Notificar por email");
        emailButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailButton.addActionListener(this::onNotifyViaEmail);
        emailButton.setEnabled(Main.getAppConfig().getSmtpConfig().enabled());

        rightPanel.add(qrLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(codeLabel);
        rightPanel.add(Box.createVerticalStrut(14));
        rightPanel.add(exportButton);
        rightPanel.add(Box.createVerticalStrut(14));
        rightPanel.add(emailButton);
        rightPanel.setPreferredSize(new Dimension(260, rightPanel.getPreferredSize().height));

        contentPanel.add(rightPanel, BorderLayout.EAST);

        wrapperPanel.add(contentPanel);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
    }

    private void onInvalidate(ActionEvent e) {
        if (ticket == null || ticket.getDeletedAt() != null) return;

        int option = JOptionPane.showConfirmDialog(
                this,
                "Estás seguro de que querés invalidar este ticket?",
                "Confirmar invalidación",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            ticketService.softDelete(ticket);
            reload();
        }
    }

    private void onNotifyViaEmail(ActionEvent e) {
        Event event = Main.getState().getCurrentEvent();

        if (ticket == null || event == null) return;

        if (!Main.getAppConfig().getSmtpConfig().enabled()) return;


        this.ticketService.notifyViaEmail(ticket, event);
    }

    private void onExportPdf(ActionEvent e) {
        if (ticket == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Seleccionar carpeta para exportar");

        int option = chooser.showOpenDialog(this);

        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File folder = chooser.getSelectedFile();
        File output = new File(folder, "ticket-" + sanitizeFileName(ticket.getCode()) + ".pdf");

        try {
            TicketExporter.exportToPDF(
                    output.getAbsolutePath(),
                    Main.getState().getCurrentEvent(),
                    ticket
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Ticket exportado en:\n" + output.getAbsolutePath(),
                    "Exportar PDF",
                    JOptionPane.INFORMATION_MESSAGE
            );

            Util.openFile(output);

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Exportar PDF",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String getTicketStatus() {
        if (ticket.getDeletedAt() != null) {
            return "Invalidado";
        }

        if (ticket.getAttendedAt() != null) {
            return "Asistió";
        }

        return "Pendiente";
    }

    private String getEventLabel() {
        Event event = ticket.getEvent();

        if (event != null) {
            return valueOrEmpty(event.getTitle());
        }

        if (ticket.getEventId() != null) {
            return ticket.getEventId().toString();
        }

        return "Sin evento";
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return "No registrado";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String sanitizeFileName(String value) {
        return valueOrEmpty(value).replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void navigateBack() {
        if (getMainState().getCurrentEvent() == null) {
            getMainState().getRouter().navigate(Routes.EVENTS_LIST);
            return;
        }

        getMainState().getRouter().navigate(Routes.EVENT_DETAIL);
    }
}
