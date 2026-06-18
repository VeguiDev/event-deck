package dev.vegui.eventdeck;

import java.io.Serializable;

public class AppConfig implements Serializable {

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
    
    public record SMTPConfig(
            String sender,
            String host,
            int port,
            String user,
            String password
    ) implements Serializable {
    }

}
