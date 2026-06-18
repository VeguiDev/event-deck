package dev.vegui.eventdeck.services;

import dev.vegui.eventdeck.AppConfig;
import dev.vegui.eventdeck.Main;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

    public CompletableFuture<Boolean> sendMail(String to, String subject, String text, String html, Map<String, byte[]> attachments) {

        return CompletableFuture.supplyAsync(() -> sendMailAsync(to, subject, text, html, attachments));

    }

    public boolean sendMailAsync(String to, String subject, String text, String html, Map<String, byte[]> attachments) {

        if (!Main.getAppConfig().getSmtpConfig().enabled()) return false;

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

        if (attachments != null) {
            for (Map.Entry<String, byte[]> entry : attachments.entrySet()) {
                builder.withEmbeddedImage(entry.getKey(), entry.getValue(), "image/png");
            }
        }

        try {
            this.mailer.sendMail(builder.buildEmail());
        } catch (Exception ex1) {
            Main.logger.log(Level.WARNING, "Error al enviar el mail", ex1);
            return false;
        }

        return true;
    }

}
