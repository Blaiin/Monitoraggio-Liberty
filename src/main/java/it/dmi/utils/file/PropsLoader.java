package it.dmi.utils.file;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.dmi.structure.exceptions.MSDRuntimeException;
import it.dmi.system.emails.config.SmtpWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static it.dmi.utils.constants.FileConstants.QUARTZ_MSD_PROPS_FILE;
import static it.dmi.utils.constants.FileConstants.SMTP_CONFIG_FILE;

@Slf4j
public class PropsLoader {

    public static Properties loadQuartzProperties() {
        var props = new Properties();
        try (InputStream msdSchedulerProps = PropsLoader.class.getClassLoader().getResourceAsStream(QUARTZ_MSD_PROPS_FILE)) {
            if (msdSchedulerProps != null) {
                props.load(msdSchedulerProps);
            }
        } catch (IOException e) {
            log.error("Failed to load Quartz properties", e);
            throw new MSDRuntimeException(e);
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
