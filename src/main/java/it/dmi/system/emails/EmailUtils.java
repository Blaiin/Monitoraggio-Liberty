package it.dmi.system.emails;

import it.dmi.system.emails.config.SmtpWrapper;
import it.dmi.utils.file.ConfigLoader;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

import static it.dmi.utils.constants.FileConstants.*;

@Singleton
@Startup
@Slf4j
public class EmailUtils {

    private static SmtpWrapper smtp;

    private static final Properties PROPS = new Properties();

    private static Session session;

    private static final boolean enableEmailDebug = false;

    @PostConstruct
    private void loadSmtpConfig() {
        try {
            smtp = ConfigLoader.loadSmtpConfig();
            if(smtp == null) {
                log.error("Error loading SMTP configuration file.");
                return;
            }
            log.debug("SMTP configuration loaded successfully, proceeding with email service initialization.");
            PROPS.put(SMTP_HOST, smtp.getSmtp().getHost());
            PROPS.put(SMTP_PORT, smtp.getSmtp().getPort());
            PROPS.put(SMTP_AUTH, smtp.getSmtp().getAuth());
            PROPS.put(SMTP_STARTTLS, smtp.getSmtp().isStartTls());
            PROPS.put(SMTP_DEBUG, String.valueOf(enableEmailDebug));

            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication () {
                    return new PasswordAuthentication(smtp.getSmtp().getUser(), smtp.getSmtp().getPassword());
                }
            };

            session = Session.getInstance(PROPS, auth);
            log.debug("Email service initialized successfully.");
            log.info("Email service initialized successfully.");
        } catch (IOException e) {
            log.error("Error loading configuration file: {}", e.getMessage());
        }
    }

    public static void sendEmail(String toAddress, String subject, String messageBody) {
        log.debug("Sending email to: {}", toAddress);
        try {

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(smtp.getSmtp().getUser()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            msg.setSubject(subject);
            msg.setText(messageBody);

            Transport.send(msg);
            log.debug("Email sent successfully!");
        } catch (MessagingException e) {
            log.error("Failed to send email.", e);
        }
    }
}
