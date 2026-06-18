package dev.vegui.eventdeck.services;

import dev.vegui.eventdeck.AppConfig;
import dev.vegui.eventdeck.Main;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.util.logging.Level;

public class EmailService {

    private Mailer mailer = null;

    public EmailService() {
        buildMailer();
    }

    private void buildMailer() {
        if (mailer != null) {
            try {
                mailer.close();
            } catch (Exception e) {
                Main.logger.log(Level.WARNING, "Error al cerrar el mailer", e);
            }
        }

        if (!Main.getAppConfig().getSmtpConfig().enabled()) {
            mailer = null;

            return;
        }

        AppConfig.SMTPConfig config = Main.getAppConfig().getSmtpConfig();

        mailer = MailerBuilder
                .withSMTPServer(config.host(), config.port(), config.user(), config.password())
                .withTransportStrategy(org.simplejavamail.api.mailer.config.TransportStrategy.SMTP_TLS)
                .buildMailer();

    }

    public void reload() {
        buildMailer();
    }

    public void sendMail(String to, String subject, String text, String html) {

        if (!Main.getAppConfig().getSmtpConfig().enabled()) return;

        if (mailer == null) {
            buildMailer();
        }

        EmailPopulatingBuilder builder = EmailBuilder.startingBlank()
                .from(Main.getAppConfig().getSmtpConfig().sender())
                .to(to)
                .withSubject(subject)
                .withPlainText(text);

        if (html != null) {
            builder = builder.withHTMLText(html);
        }

        this.mailer.sendMail(builder.buildEmail());

    }

}
