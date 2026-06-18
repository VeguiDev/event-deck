package dev.vegui.eventdeck;

import java.io.InvalidClassException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean fristRun = true;

    private SMTPConfig smtpConfig;

    private boolean autoSendTicket;

    public void setFristRun(boolean fristRun) {
        this.fristRun = fristRun;
    }

    public boolean isFristRun() {
        return fristRun;
    }

    public void setSmtpConfig(SMTPConfig smtpConfig) {
        this.smtpConfig = smtpConfig;
    }

    public SMTPConfig getSmtpConfig() {
        return smtpConfig;
    }

    public void setAutoSendTicket(boolean autoSendTicket) {
        this.autoSendTicket = autoSendTicket;
    }

    public boolean isAutoSendTicket() {
        return autoSendTicket;
    }

    public static AppConfig load(Path path) throws IOException, ClassNotFoundException {
        if (!Files.exists(path)) {
            return new AppConfig();
        }

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (AppConfig) input.readObject();
        } catch (InvalidClassException e) {
            return new AppConfig();
        }
    }

    public void save(Path path) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            output.writeObject(this);
        }
    }
    
    public record SMTPConfig(
            boolean enabled,
            String sender,
            String host,
            int port,
            String user,
            String password
    ) implements Serializable {
        private static final long serialVersionUID = 1L;
    }

}
