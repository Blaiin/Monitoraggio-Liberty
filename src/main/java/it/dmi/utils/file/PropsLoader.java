package it.dmi.utils.file;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.dmi.structure.internal.props.ConfigAndAzioneProps;
import it.dmi.system.emails.config.SmtpWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static it.dmi.utils.constants.FileConstants.*;

@Slf4j
public class PropsLoader {

    public static ConfigAndAzioneProps loadQuartzProperties() {
        var props = new ConfigAndAzioneProps(new Properties(), new Properties());
        try (InputStream config = PropsLoader.class.getClassLoader().getResourceAsStream(QUARTZ_CONFIG_PROPS_FILE);
            InputStream azione = PropsLoader.class.getClassLoader().getResourceAsStream(QUARTZ_AZIONE_PROPS_FILE)) {
            if (config != null) {
                props.configProps().load(config);
            }
            if (azione != null) {
                props.azioneProps().load(azione);
            }
        } catch (IOException e) {
            log.error("Failed to load Quartz properties", e);
            throw new RuntimeException(e);
        }
        return props;
    }
    public static SmtpWrapper loadSmtpProperties () {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        try (InputStream inputStream = PropsLoader.class.getClassLoader().getResourceAsStream(SMTP_CONFIG_FILE)) {
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
