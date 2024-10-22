package it.dmi.utils.file;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.dmi.system.emails.config.SmtpWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static it.dmi.utils.constants.FileConstants.QUARTZ_CONFIG_FILE;
import static it.dmi.utils.constants.FileConstants.SMTP_CONFIG_FILE;

@Slf4j
public class ConfigLoader {

    public static Properties loadQuartzConfig() {
        Properties properties = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(QUARTZ_CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            log.error("Failed to load Quartz properties", e);
            throw new RuntimeException(e);
        }
        return properties;
    }
    public static SmtpWrapper loadSmtpConfig() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(SMTP_CONFIG_FILE)) {
            if (inputStream == null) {
                log.error("Resource not found: {}", SMTP_CONFIG_FILE);
                throw new IOException("Resource not found: " + SMTP_CONFIG_FILE);
            }
            log.debug("Loading smtp configuration file: {}", SMTP_CONFIG_FILE);
            return mapper.readValue(inputStream, SmtpWrapper.class);
        } catch (IOException e) {
            log.error("Error loading smtp configuration file: {}", e.getMessage());
            return null;
        }
    }
}
