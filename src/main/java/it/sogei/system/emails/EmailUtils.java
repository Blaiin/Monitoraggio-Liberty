package it.sogei.system.emails;

import it.sogei.system.emails.config.SmtpWrapper;
import it.sogei.utils.file.ConfigLoader;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

import static it.sogei.utils.constants.MailingConstants.*;

@Singleton
@Startup
@Slf4j
public class EmailUtils {

    private static SmtpWrapper smtp;

    private static final Properties PROPS = new Properties();

    private static Session session;

    private static final boolean enableEmailDebug = true;

    @PostConstruct
    private void loadSmtpConfig() {
        try {
            smtp = ConfigLoader.loadSmtpConfig();
            if(smtp == null) {
                log.error("Error loading SMTP configuration file.");
                return;
            }
            log.info("SMTP configuration loaded successfully, proceeding with email service initialization.");
            PROPS.put(SMTP_HOST, smtp.smtp().host());
            PROPS.put(SMTP_PORT, smtp.smtp().port());
            PROPS.put(SMTP_AUTH, smtp.smtp().auth());
            PROPS.put(SMTP_STARTTLS, smtp.smtp().startTls());
            PROPS.put(SMTP_DEBUG, String.valueOf(enableEmailDebug));

            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication () {
                    return new PasswordAuthentication(smtp.smtp().user(), smtp.smtp().password());
                }
            };

            session = Session.getInstance(PROPS, auth);
            log.info("Email service initialized successfully.");
        } catch (IOException e) {
            log.error("Error loading configuration file: {}", e.getMessage());
        }
    }

    public static void sendEmail(String toAddress, String subject, String messageBody) {
        log.info("Sending email to: {}", toAddress);
        try {

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(smtp.smtp().user()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            msg.setSubject(subject);
            msg.setText(messageBody);

            Transport.send(msg);
            log.info("Email sent successfully!");
        } catch (MessagingException e) {
            log.error("Failed to send email.", e);
        }
    }
}
