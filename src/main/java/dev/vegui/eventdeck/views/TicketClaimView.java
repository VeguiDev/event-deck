package dev.vegui.eventdeck.views;

import com.google.zxing.NotFoundException;
import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;
import dev.vegui.eventdeck.model.Ticket;
import dev.vegui.eventdeck.services.TicketService;
import dev.vegui.eventdeck.util.QRUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class TicketClaimView extends View {
    private final TicketService ticketService;

    private JTextField codeInput;
    private JLabel statusLabel;
    private JLabel imageStatusLabel;

    public TicketClaimView() {
        this.ticketService = Main.getService(TicketService.class);

        setLayout(new BorderLayout());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> getMainState().getRouter().navigate(Routes.EVENTS_LIST));
        navPanel.add(backButton);

        JLabel title = new JLabel("Claim ticket");
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 18f)
        );
        navPanel.add(title);
        add(navPanel, BorderLayout.NORTH);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(wrapper, BorderLayout.CENTER);

        JPanel manualPanel = new JPanel();
        manualPanel.setLayout(new BoxLayout(manualPanel, BoxLayout.Y_AXIS));
        manualPanel.setBorder(BorderFactory.createTitledBorder("Código"));
        manualPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        codeInput = new JTextField(24);
        codeInput.setMaximumSize(new Dimension(360, codeInput.getPreferredSize().height));
        manualPanel.add(new JLabel("Código del ticket"));
        manualPanel.add(codeInput);

        JPanel manualActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton claimButton = new JButton("Claim");
        claimButton.addActionListener(e -> claimCode(codeInput.getText()));
        manualActions.add(claimButton);
        manualPanel.add(manualActions);

        wrapper.add(manualPanel);
        wrapper.add(Box.createVerticalStrut(16));

        JPanel qrPanel = new JPanel();
        qrPanel.setLayout(new BoxLayout(qrPanel, BoxLayout.Y_AXIS));
        qrPanel.setBorder(BorderFactory.createTitledBorder("QR"));
        qrPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel qrActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton fileButton = new JButton("Cargar imagen");
        fileButton.addActionListener(this::onLoadImage);
        JButton pasteButton = new JButton("Pegar imagen");
        pasteButton.addActionListener(this::onPasteImage);
        qrActions.add(fileButton);
        qrActions.add(pasteButton);
        qrPanel.add(qrActions);

        imageStatusLabel = new JLabel("Sin imagen cargada");
        imageStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qrPanel.add(imageStatusLabel);

        wrapper.add(qrPanel);
        wrapper.add(Box.createVerticalStrut(16));

        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(statusLabel);
    }

    @Override
    public void reload() {
        super.reload();
        codeInput.setText("");
        statusLabel.setText(" ");
        imageStatusLabel.setText("Sin imagen cargada");
    }

    private void onLoadImage(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);

        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try {
            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                showError("El archivo seleccionado no es una imagen válida");
                return;
            }

            imageStatusLabel.setText(file.getName());
            claimFromImage(image);
        } catch (IOException ex) {
            showError("No se pudo leer la imagen seleccionada");
        }
    }

    private void onPasteImage(ActionEvent e) {
        try {
            Object data = Toolkit
                    .getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.imageFlavor);

            if (!(data instanceof Image image)) {
                showError("El portapapeles no contiene una imagen");
                return;
            }

            imageStatusLabel.setText("Imagen pegada desde portapapeles");
            claimFromImage(toBufferedImage(image));
        } catch (UnsupportedFlavorException ex) {
            showError("El portapapeles no contiene una imagen");
        } catch (IOException | IllegalStateException ex) {
            showError("No se pudo leer la imagen del portapapeles");
        }
    }

    private void claimFromImage(BufferedImage image) {
        try {
            String qrContent = QRUtils.readQR(image);
            codeInput.setText(extractCode(qrContent));
            claimCode(qrContent);
        } catch (NotFoundException ex) {
            showError("No se encontró un QR válido en la imagen");
        }
    }

    private void claimCode(String rawCode) {
        String code = extractCode(rawCode);

        if (code.isEmpty()) {
            showError("Ingresá un código de ticket");
            return;
        }

        Optional<Ticket> ticket = ticketService.findByCode(code);

        if (ticket.isEmpty()) {
            showError("No se encontró un ticket pendiente con ese código");
            return;
        }

        Ticket claimedTicket = ticket.get();
        ticketService.claim(claimedTicket);
        getMainState().setCurrentEvent(null);
        getMainState().setCurrentTicket(claimedTicket);
        getMainState().getRouter().navigate(Routes.TICKET_DETAIL);
    }

    private String extractCode(String rawCode) {
        if (rawCode == null) {
            return "";
        }

        String code = rawCode.trim();

        if (code.startsWith("ticket:")) {
            return code.substring("ticket:".length()).trim();
        }

        return code;
    }

    private BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage bufferedImage) {
            return bufferedImage;
        }

        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return bufferedImage;
    }

    private void showError(String message) {
        statusLabel.setText(message);
        JOptionPane.showMessageDialog(
                this,
                message,
                "Claim ticket",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
