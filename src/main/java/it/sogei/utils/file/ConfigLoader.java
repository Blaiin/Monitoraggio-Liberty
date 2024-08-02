package it.sogei.utils.file;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.sogei.system.emails.config.SmtpWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

import static it.sogei.utils.constants.MailingConstants.SMTP_CONFIG_FILE;

@Slf4j
public class ConfigLoader {

    public static SmtpWrapper loadSmtpConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(SMTP_CONFIG_FILE)) {
            if (inputStream == null) {
                log.error("Resource not found: {}", SMTP_CONFIG_FILE);
                throw new IOException("Resource not found: " + SMTP_CONFIG_FILE);
            }
            log.info("Loading smtp configuration file: {}", SMTP_CONFIG_FILE);
            return mapper.readValue(inputStream, SmtpWrapper.class);
        } catch (IOException e) {
            log.error("Error loading smtp configuration file: {}", e.getMessage());
            return null;
        }
    }
}
