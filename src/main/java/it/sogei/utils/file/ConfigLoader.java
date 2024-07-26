package it.sogei.utils.file;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.sogei.system.emails.config.SmtpWrapper;

import java.io.File;
import java.io.IOException;

public class ConfigLoader {
    public static SmtpWrapper loadSmtpConfig(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        return mapper.readValue(new File(filePath), SmtpWrapper.class);
    }
}
