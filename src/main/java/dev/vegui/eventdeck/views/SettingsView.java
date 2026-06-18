package dev.vegui.eventdeck.views;

import dev.vegui.eventdeck.AppConfig;
import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.Routes;
import dev.vegui.eventdeck.View;

import javax.swing.*;
import java.awt.*;

public class SettingsView extends View {

    private JCheckBox autoSendTicket;
    private JCheckBox smtpEnabled;
    private JTextField sender;
    private JTextField host;
    private JSpinner port;
    private JTextField user;
    private JPasswordField password;

    public SettingsView() {
        setLayout(new BorderLayout());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> getMainState().getRouter().navigate(Routes.EVENTS_LIST));
        navPanel.add(backButton);

        JLabel title = new JLabel("Configuración");
        title.setFont(
                title.getFont().deriveFont(Font.BOLD, 18f)
        );
        navPanel.add(title);
        add(navPanel, BorderLayout.NORTH);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        wrapper.add(buildGeneralPanel());
        wrapper.add(Box.createVerticalStrut(16));
        wrapper.add(buildSmtpPanel());
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(buildActionsPanel());

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void reload() {
        super.reload();
        loadConfig();
    }

    private JPanel buildGeneralPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("General"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        autoSendTicket = new JCheckBox("Enviar ticket automáticamente al crear");
        autoSendTicket.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(autoSendTicket);

        return panel;
    }

    private JPanel buildSmtpPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("SMTP"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        smtpEnabled = new JCheckBox("Habilitar SMTP");
        smtpEnabled.addActionListener(e -> updateSmtpFieldsEnabled());

        sender = new JTextField(28);
        host = new JTextField(28);
        port = new JSpinner(new SpinnerNumberModel(587, 1, 65535, 1));
        user = new JTextField(28);
        password = new JPasswordField(28);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(6, 8, 8, 8);
        panel.add(smtpEnabled, gbc);

        gbc.gridwidth = 1;
        addField(panel, gbc, "Remitente", sender);
        addField(panel, gbc, "Host", host);
        addField(panel, gbc, "Puerto", port);
        addField(panel, gbc, "Usuario", user);
        addField(panel, gbc, "Password", password);

        return panel;
    }

    private JPanel buildActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveButton = new JButton("Guardar");
        saveButton.addActionListener(e -> onSave());
        panel.add(saveButton);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private void loadConfig() {
        AppConfig config = Main.getAppConfig();
        AppConfig.SMTPConfig smtpConfig = config.getSmtpConfig();

        autoSendTicket.setSelected(config.isAutoSendTicket());

        if (smtpConfig == null) {
            smtpEnabled.setSelected(false);
            sender.setText("");
            host.setText("");
            port.setValue(587);
            user.setText("");
            password.setText("");
        } else {
            smtpEnabled.setSelected(smtpConfig.enabled());
            sender.setText(valueOrEmpty(smtpConfig.sender()));
            host.setText(valueOrEmpty(smtpConfig.host()));
            port.setValue(smtpConfig.port() > 0 ? smtpConfig.port() : 587);
            user.setText(valueOrEmpty(smtpConfig.user()));
            password.setText(valueOrEmpty(smtpConfig.password()));
        }

        updateSmtpFieldsEnabled();
    }

    private void onSave() {
        AppConfig config = Main.getAppConfig();

        config.setAutoSendTicket(autoSendTicket.isSelected());
        config.setSmtpConfig(new AppConfig.SMTPConfig(
                smtpEnabled.isSelected(),
                sender.getText(),
                host.getText(),
                (Integer) port.getValue(),
                user.getText(),
                new String(password.getPassword())
        ));

        Main.saveAppConfig();

        JOptionPane.showMessageDialog(
                this,
                "Configuración guardada",
                "Configuración",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void updateSmtpFieldsEnabled() {
        boolean enabled = smtpEnabled.isSelected();
        sender.setEnabled(enabled);
        host.setEnabled(enabled);
        port.setEnabled(enabled);
        user.setEnabled(enabled);
        password.setEnabled(enabled);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
